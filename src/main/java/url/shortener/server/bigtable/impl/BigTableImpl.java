package url.shortener.server.bigtable.impl;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.bigtable.util.FileUtils;

@Slf4j
public class BigTableImpl implements BigTable {

  private final int bucketsNumber;
  private final File databaseRoot;
  private final Map<Integer, Bucket> hashToBucket;

  public BigTableImpl(
      @NotBlank String tableName,
      @Min(1) int bucketsNumber,
      @Min(1) int keyLength
  ) {
    this(tableName, null, bucketsNumber, keyLength);
  }

  public BigTableImpl(
      @NotBlank String tableName,
      @NotBlank String workingRootDirectory,
      @Min(1) int bucketsNumber,
      @Min(1) int keyLength
  ) {
    this.bucketsNumber = bucketsNumber;
    this.databaseRoot = FileUtils.createWorkingRootDirectory(workingRootDirectory, tableName);
    this.hashToBucket = initBuckets(keyLength);
  }

  @Override
  public boolean containsKey(@NotNull String key) {
    return hashToBucket.get(getBucketNumber(key))
        .containsKey(key);
  }

  @Override
  public boolean put(@NotNull String key, @NotNull String value) {
    return hashToBucket.get(getBucketNumber(key))
        .put(key, value);
  }

  @Override
  public boolean deleteByKey(@NotNull String key) {
    return hashToBucket.get(getBucketNumber(key))
        .deleteByKey(key);
  }

  @Override
  public Optional<String> findByKey(@NotNull String key) {
    return hashToBucket.get(getBucketNumber(key))
        .findByKey(key);
  }

  private Map<Integer, Bucket> initBuckets(int keyLength) {
    return Stream.iterate(0, i -> i < bucketsNumber, i -> i + 1)
        .map(i -> Map.entry(i, new Bucket(i, keyLength, databaseRoot)))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  private int getBucketNumber(String key) {
    return Math.abs(key.hashCode()) % bucketsNumber;
  }

  File getDatabaseRoot() {
    return databaseRoot;
  }
}
