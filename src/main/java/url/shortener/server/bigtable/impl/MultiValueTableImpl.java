package url.shortener.server.bigtable.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import url.shortener.server.bigtable.MultiValueTable;
import url.shortener.server.bigtable.util.FileUtils;

public class MultiValueTableImpl implements MultiValueTable {

  private final int bucketsNumber;
  private final File databaseRoot;
  private final int keyLength;
  private final Map<Integer, MultiValueBucket> hashToBucket;

  public MultiValueTableImpl(
      @NotBlank String tableName,
      @Min(1) int bucketsNumber,
      @Min(1) int keyLength
  ) {
    this(tableName, null, bucketsNumber, keyLength);
  }

  public MultiValueTableImpl(
      @NotBlank String tableName,
      @NotBlank String workingRootDirectory,
      @Min(1) int bucketsNumber,
      @Min(1) int keyLength
  ) {
    this.bucketsNumber = bucketsNumber;
    this.databaseRoot = FileUtils.createWorkingRootDirectory(workingRootDirectory, tableName);
    this.keyLength = keyLength;
    this.hashToBucket = initBuckets();
  }

  private Map<Integer, MultiValueBucket> initBuckets() {
    return Stream.iterate(0, i -> i < bucketsNumber, i -> i + 1)
        .map(i -> Map.entry(i, new MultiValueBucket(i, keyLength, databaseRoot)))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public boolean put(@NotNull String key, @NotNull String value) {
    return hashToBucket.get(getBucketsNumber(key))
        .put(key, value);
  }

  @Override
  public boolean deleteByKey(@NotNull String key, @NotNull String value) {
    return hashToBucket.get(getBucketsNumber(key))
        .deleteByKey(key, value);
  }

  @Override
  public List<String> findByKey(@NotNull String key) {
    return hashToBucket.get(getBucketsNumber(key))
        .findByKey(key);
  }

  private int getBucketsNumber(String key) {
    return Math.abs(key.hashCode()) % bucketsNumber;
  }

  File getDatabaseRoot() {
    return databaseRoot;
  }
}
