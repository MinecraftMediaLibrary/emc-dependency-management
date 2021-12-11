/**
 * MIT License
 *
 * Copyright (c) 2021 Brandon Li
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
package io.github.pulsebeat02.emcdependencymanagement.injector;

import static io.github.pulsebeat02.emcdependencymanagement.unsafe.UnsafeUtils.getField;

import io.github.pulsebeat02.emcdependencymanagement.unsafe.UnsafeManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import sun.misc.Unsafe;

public final class UnsafeInjection {

  private static final Unsafe UNSAFE;

  static {
    UNSAFE = UnsafeManager.getUnsafe();
  }

  private final Iterable<Path> jars;
  private final Deque<URL> unopened;
  private final List<URL> paths;

  private UnsafeInjection(final Iterable<Path> jars, final URLClassLoader classloader)
      throws NoSuchFieldException {
    this.jars = jars;
    final Object ucp = this.getUCP(classloader);
    this.unopened = this.getUnopenedURLs(ucp);
    this.paths = this.getPathURLs(ucp);
  }

  public static UnsafeInjection ofInjection(
      final Iterable<Path> jars, final URLClassLoader classloader) throws NoSuchFieldException {
    return new UnsafeInjection(jars, classloader);
  }

  public void inject() throws MalformedURLException {
    for (final Path path : this.jars) {
      final URL url = path.toUri().toURL();
      this.unopened.add(url);
      this.paths.add(url);
    }
  }

  private ArrayList<URL> getPathURLs(final Object ucp) throws NoSuchFieldException {
    return (ArrayList<URL>) getField(ucp, "path");
  }

  private ArrayDeque<URL> getUnopenedURLs(final Object ucp) throws NoSuchFieldException {
    return (ArrayDeque<URL>) getField(ucp, "unopenedUrls");
  }

  private Object getUCP(final URLClassLoader classloader) throws NoSuchFieldException {
    return getField(URLClassLoader.class, classloader, "ucp");
  }
}
