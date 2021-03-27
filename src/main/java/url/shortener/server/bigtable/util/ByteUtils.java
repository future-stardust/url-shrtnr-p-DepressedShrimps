package url.shortener.server.bigtable.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class ByteUtils {

  public static final String LINE_SEPARATOR = System.getProperty("line.separator");

  private static final ByteBuffer BUFFER = ByteBuffer.allocate(Long.BYTES);

  public static byte[] longToBytes(long x) {
    BUFFER.putLong(0, x);
    return BUFFER.array();
  }

  public static long bytesToLong(byte[] bytes) {
    BUFFER.put(bytes, 0, bytes.length);
    BUFFER.flip();
    return BUFFER.getLong();
  }

  public static byte[] formatKey(String key, int keyLength) {
    if (key.length() > keyLength) {
      throw new IllegalArgumentException("Key length is bigger then " + keyLength);
    }
    return (key + StringUtils.repeat(" ", keyLength - key.length()))
        .getBytes(StandardCharsets.UTF_8);
  }

  public static String normalizeKey(byte[] key) {
    return new String(key, StandardCharsets.UTF_8)
        .stripTrailing();
  }
}
