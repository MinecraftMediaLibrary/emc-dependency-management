package io.github.pulsebeat02.emcdependencymanagement.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Locale;

public final class FileUtils {

  private static final byte[] HEX_ARRAY;

  static {
    HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
  }

  private FileUtils() {}

  public static String getUppercaseHash(final Path file) {
    try {
      return bytesToHex(createByteSHA(file)).toUpperCase(Locale.ROOT);
    } catch (final Exception e) {
      throw new AssertionError(String.format("Failed to get hash of file for %s!", file));
    }
  }

  private static byte[] createByteSHA(final Path file) throws Exception {
    final MessageDigest digest = MessageDigest.getInstance("SHA-1");
    final InputStream fis = Files.newInputStream(file);
    int n = 0;
    final byte[] buffer = new byte[8192];
    while (n != -1) {
      n = fis.read(buffer);
      if (n > 0) {
        digest.update(buffer, 0, n);
      }
    }
    return digest.digest();
  }

  private static String bytesToHex(final byte[] bytes) {
    final byte[] hexChars = new byte[bytes.length << 1];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.UTF_8);
  }
}
