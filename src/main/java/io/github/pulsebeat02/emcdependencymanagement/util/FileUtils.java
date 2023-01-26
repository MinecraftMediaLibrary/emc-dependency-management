/**
 * MIT License
 *
 * Copyright (c) 2023 Brandon Li
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
