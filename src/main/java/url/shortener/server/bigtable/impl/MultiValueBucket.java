package url.shortener.server.bigtable.impl;

import static java.text.MessageFormat.format;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.bigtable.MultiValueTable;
import url.shortener.server.bigtable.util.ByteUtils;

@Slf4j
class MultiValueBucket implements MultiValueTable {

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private static final String INDEX_FILE_PATTERN = "index_{0}.dbkf";
  private static final String TMP_INDEX_FILE_PATTERN = "index_{0}.dbkf_tmp";
  private static final String VALUE_FILE_PATTERN = "value_{0}.dbvf";
  private static final String GARBAGE_FILE_PATTERN = "grbg_{0}.dbvf";

  private final AtomicLong position = new AtomicLong();

  private final ReadWriteLock indexReadWriteLock = new ReentrantReadWriteLock();
  private final ReadWriteLock valueReadWriteLock = new ReentrantReadWriteLock();
  private final ReadWriteLock garbageReadWriteLock = new ReentrantReadWriteLock();

  private final int bucketNumber;
  private final int keyLength;
  private final int valueLength;
  private final int bufferSize;
  private final File indexFile;
  private final File valueFile;
  private final File garbageFile;

  MultiValueBucket(int bucketNumber, int keyLength, File rootPath) {
    indexReadWriteLock.writeLock().lock();
    valueReadWriteLock.writeLock().lock();
    garbageReadWriteLock.writeLock().lock();
    log.debug("::DB:: Start initializing of bucket #'{}' by root path '{}'",
        bucketNumber, rootPath
    );
    this.bucketNumber = bucketNumber;
    this.keyLength = keyLength;
    this.valueLength = keyLength + Long.BYTES;
    //overflow check
    this.bufferSize = Math.multiplyExact(valueLength, 100);
    this.indexFile = new File(rootPath, format(INDEX_FILE_PATTERN, bucketNumber));
    this.valueFile = new File(rootPath, format(VALUE_FILE_PATTERN, bucketNumber));
    this.garbageFile = new File(rootPath, format(GARBAGE_FILE_PATTERN, bucketNumber));
    initFiles();
    initInMemoryData();
    log.debug("::DB:: Finish initializing of bucket #'{}' by root path '{}'",
        bucketNumber, rootPath
    );
    indexReadWriteLock.writeLock().unlock();
    valueReadWriteLock.writeLock().unlock();
    garbageReadWriteLock.writeLock().unlock();
  }

  private void initFiles() {
    if (indexFile.exists() && valueFile.exists()) {
      log.debug("::DB:: Files for bucket #'{}' already exists. Skip creation phase", bucketNumber);
      return;
    }

    log.debug("::DB:: Create files for bucket #'{}'", bucketNumber);
    try {
      if (!indexFile.createNewFile()) {
        log.info("::DB:: index file for bucket #'{}' already exists", bucketNumber);
      }
      if (!valueFile.createNewFile()) {
        log.info("::DB:: value file for bucket #'{}' already exists", bucketNumber);
      }
      if (!garbageFile.createNewFile()) {
        log.info("::DB:: Garbage file for bucket #'{}' already exists", bucketNumber);
      }
    } catch (IOException e) {
      log.error("::DB:: Could not create files for bucket #'{}'", bucketNumber, e);
      throw new IllegalStateException("Could not create DB files", e);
    }
  }

  private void initInMemoryData() {
    try (RandomAccessFile valueAccess = new RandomAccessFile(valueFile, "r")) {
      position.set(valueAccess.length());
      log.debug("::DB:: set position of value file to '{}' in bucket #'{}'",
          position, bucketNumber);
    } catch (IOException e) {
      log.error("::DB:: Could not read from value file in bucket #'{}'", bucketNumber);
      throw new IllegalStateException("Cannot initialize in-memory data", e);
    }
  }

  @Override
  public boolean put(String key, String value) {
    boolean saved = saveKey(key);
    if (!saved) {
      return false;
    }
    return saveValue(value);
  }

  private boolean saveKey(String key) {
    byte[] keyBytes = ByteUtils.formatKey(key, keyLength);
    indexReadWriteLock.writeLock().lock();
    try (RandomAccessFile indexAccess = new RandomAccessFile(indexFile, "r")) {
      long lower = 0;
      long indexLength = indexAccess.length();
      long higher = indexLength == 0 ? 0 : indexLength - valueLength;
      long center = (higher - lower) / 2;
      center = center - (center % valueLength);
      byte[] buffer = new byte[keyLength];

      while (true) {
        indexAccess.seek(center);
        indexAccess.read(buffer);

        int compare = Arrays.compare(keyBytes, buffer);

        if (compare == 0) {
          log.debug("::DB:: Key '{}' already exists", key);
          insertKey(keyBytes, center + valueLength);
          return true;
        }
        if (compare > 0) {
          if (center == higher) {
            log.debug("::DB:: Save key '{}' into '{}'", key, center + valueLength);
            insertKey(keyBytes, center + valueLength);
            return true;
          }
          lower = center + valueLength;
        } else {
          if (center == lower) {
            log.debug("::DB:: Save key '{}' into '{}'", key, center);
            insertKey(keyBytes, center);
            return true;
          }
          higher = center - valueLength;
        }
        center = (higher + lower) / 2;
        center = center - (center % valueLength);
      }
    } catch (IOException e) {
      log.error("::DB:: Error during key save in bucket#'{}'", bucketNumber, e);
      return false;
    } finally {
      indexReadWriteLock.writeLock().unlock();
    }
  }

  void insertKey(byte[] keyBytes, long insertPosition) throws IOException {
    File copyFile = new File(indexFile.getParent(), format(TMP_INDEX_FILE_PATTERN, bucketNumber));
    if (!copyFile.createNewFile()) {
      log.error("::DB:: Could not create '{}'", copyFile.getName());
    }
    long timesBeforePosition = insertPosition / bufferSize;
    int count;

    try (
        InputStream inputStream = new BufferedInputStream(new FileInputStream(indexFile));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(copyFile))
    ) {
      byte[] buffer = new byte[bufferSize];
      for (long i = 0; i < timesBeforePosition; i++) {
        if ((count = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, count);
          outputStream.flush();
        }
      }
      buffer = new byte[(int) (insertPosition % bufferSize)];
      if (buffer.length > 0 && inputStream.read(buffer) > 0) {
        outputStream.write(buffer);
      }
      outputStream.write(keyBytes);
      outputStream.write(ByteUtils.longToBytes(position.get()));
      log.debug("::DB:: save key with position '{}'", position.get());

      buffer = new byte[bufferSize];
      while ((count = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, count);
        outputStream.flush();
      }
    }
    if (!indexFile.delete()) {
      log.error("::DB:: Could not delete '{}'", indexFile.getName());
    }
    if (!copyFile.renameTo(indexFile)) {
      log.error("::DB:: Could not rename '{}' to '{}'", copyFile.getName(), indexFile.getName());
    }
  }

  private boolean saveValue(String value) {
    valueReadWriteLock.writeLock().lock();
    try (var valueAccess = new RandomAccessFile(valueFile, "rw")) {
      valueAccess.seek(valueAccess.length());
      valueAccess.write(
          value.concat(LINE_SEPARATOR)
              .getBytes(StandardCharsets.UTF_8)
      );
      position.set(valueAccess.length());
      log.debug("::DB:: set position to '{}' in file '{}'", position.get(), valueFile.getName());
    } catch (IOException e) {
      log.error("::DB:: could not save value to file '{}'", valueFile.getName(), e);
      return false;
    } finally {
      valueReadWriteLock.writeLock().unlock();
    }
    return true;
  }

  @Override
  public List<String> findByKey(String key) {
    byte[] keyBytes = ByteUtils.formatKey(key, keyLength);
    indexReadWriteLock.readLock().lock();
    try (RandomAccessFile indexAccess = new RandomAccessFile(indexFile, "r")) {
      long lower = 0;
      long indexLength = indexAccess.length();
      long higher = indexLength == 0 ? 0 : indexLength - valueLength;
      long center = (higher - lower) / 2;
      center = center - (center % valueLength);
      byte[] buffer = new byte[keyLength];

      while (true) {
        indexAccess.seek(center);
        indexAccess.read(buffer);

        int compare = Arrays.compare(keyBytes, buffer);

        if (compare == 0) {
          List<Long> indexes = new ArrayList<>();
          indexes.add(indexAccess.readLong());
          long tmpPosition = center - valueLength;

          if (tmpPosition > 0) {
            indexAccess.seek(tmpPosition);
          }
          while (tmpPosition > 0
              && (indexAccess.read(buffer) != -1)
              && Arrays.compare(buffer, keyBytes) == 0
          ) {
            indexes.add(indexAccess.readLong());
            indexAccess.seek(tmpPosition);
            tmpPosition -= valueLength;
          }
          tmpPosition = center + valueLength;
          indexAccess.seek(tmpPosition);
          while ((indexAccess.read(buffer) != -1) && Arrays.compare(buffer, keyBytes) == 0) {
            indexes.add(indexAccess.readLong());
            tmpPosition += valueLength;
            indexAccess.seek(tmpPosition);
          }
          return mapToValue(indexes);
        }
        if (compare > 0) {
          if (center == higher) {
            return Collections.emptyList();
          }
          lower = center + valueLength;
        } else {
          if (center == lower) {
            return Collections.emptyList();
          }
          higher = center - valueLength;
        }
        center = (higher + lower) / 2;
        center = center - (center % valueLength);
      }
    } catch (IOException e) {
      log.error("::DB:: Error during key save in bucket#'{}'", bucketNumber, e);
      return Collections.emptyList();
    } finally {
      indexReadWriteLock.readLock().unlock();
    }
  }

  private List<String> mapToValue(List<Long> indexes) {
    valueReadWriteLock.readLock().lock();
    try (RandomAccessFile valueAccess = new RandomAccessFile(valueFile, "r")) {
      return indexes
          .stream()
          .sorted(Long::compare)
          .map(entry -> {
            try {
              valueAccess.seek(entry);
              return valueAccess.readLine();
            } catch (IOException e) {
              return "";
            }
          })
          .collect(Collectors.toList());
    } catch (IOException e) {
      log.error("::DB:: Could not read '{}'", valueFile.getName(), e);
      return Collections.emptyList();
    } finally {
      valueReadWriteLock.readLock().unlock();
    }
  }

  @Override
  public boolean deleteByKey(String key, String value) {
    byte[] keyBytes = ByteUtils.formatKey(key, keyLength);
    indexReadWriteLock.readLock().lock();
    try (RandomAccessFile indexAccess = new RandomAccessFile(indexFile, "r")) {
      long lower = 0;
      long indexLength = indexAccess.length();
      long higher = indexLength == 0 ? 0 : indexLength - valueLength;
      long center = (higher - lower) / 2;
      center = center - (center % valueLength);
      byte[] buffer = new byte[keyLength];

      while (true) {
        indexAccess.seek(center);
        indexAccess.read(buffer);

        int compare = Arrays.compare(keyBytes, buffer);

        if (compare == 0) {
          Map<Long, Long> indexToPosition = new HashMap<>();
          indexToPosition.put(center, indexAccess.readLong());
          long tmpPosition = center - valueLength;
          if (tmpPosition > 0) {
            indexAccess.seek(tmpPosition);
          }
          while (tmpPosition > 0
              && (indexAccess.read(buffer) != -1)
              && Arrays.compare(buffer, keyBytes) == 0
          ) {
            indexToPosition.put(tmpPosition, indexAccess.readLong());
            indexAccess.seek(tmpPosition);
            tmpPosition -= valueLength;
          }

          tmpPosition = center + valueLength;
          indexAccess.seek(tmpPosition);
          while ((indexAccess.read(buffer) != -1) && Arrays.compare(buffer, keyBytes) == 0) {
            indexToPosition.put(tmpPosition, indexAccess.readLong());
            tmpPosition += valueLength;
            indexAccess.seek(tmpPosition);
          }
          long deletePosition = deleteWithValue(indexToPosition, value);
          if (deletePosition == -1) {
            return false;
          }
          removeKey(deletePosition);
          return true;
        }
        if (compare > 0) {
          if (center == higher) {
            return false;
          }
          lower = center + valueLength;
        } else {
          if (center == lower) {
            return false;
          }
          higher = center - valueLength;
        }
        center = (higher + lower) / 2;
        center = center - (center % valueLength);
      }
    } catch (IOException e) {
      log.error("::DB:: Error during key save in bucket#'{}'", bucketNumber, e);
      return false;
    } finally {
      indexReadWriteLock.readLock().unlock();
    }
  }

  private void removeKey(long removePosition) throws IOException {
    File copyFile = new File(indexFile.getParent(), format(TMP_INDEX_FILE_PATTERN, bucketNumber));
    if (!copyFile.createNewFile()) {
      log.error("::DB:: Could not create '{}'", copyFile.getName());
    }
    long timesBeforePosition = removePosition / bufferSize;
    int count;

    try (
        InputStream inputStream = new BufferedInputStream(new FileInputStream(indexFile));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(copyFile))
    ) {
      byte[] buffer = new byte[bufferSize];
      for (long i = 0; i < timesBeforePosition; i++) {
        if ((count = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, count);
          outputStream.flush();
        }
      }
      buffer = new byte[(int) (removePosition % bufferSize)];
      if (buffer.length > 0 && inputStream.read(buffer) > 0) {
        outputStream.write(buffer);
      }
      inputStream.readNBytes(valueLength);

      buffer = new byte[bufferSize];
      while ((count = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, count);
        outputStream.flush();
      }
    }
    if (!indexFile.delete()) {
      log.error("::DB:: Could not delete '{}'", indexFile.getName());
    }
    if (!copyFile.renameTo(indexFile)) {
      log.error("::DB:: Could not rename '{}' to '{}'", copyFile.getName(), indexFile.getName());
    }
  }

  private long deleteWithValue(Map<Long, Long> indexToPosition, String value) {
    List<Entry<Long, Long>> sortedEntries = indexToPosition.entrySet()
        .stream()
        .sorted(Entry.comparingByValue())
        .collect(Collectors.toList());
    valueReadWriteLock.readLock().lock();
    try (RandomAccessFile valueAccess = new RandomAccessFile(valueFile, "r")) {
      for (Entry<Long, Long> entry : sortedEntries) {
        valueAccess.seek(entry.getValue());
        if (StringUtils.equals(valueAccess.readLine(), value)) {
          return entry.getKey();
        }
      }
    } catch (IOException e) {
      log.error("::DB:: could not read '{}' file", valueFile.getName());
    } finally {
      valueReadWriteLock.readLock().unlock();
    }
    return -1;
  }
}
