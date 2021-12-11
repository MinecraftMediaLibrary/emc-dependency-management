package io.github.pulsebeat02.emcdependencymanagement;

import java.net.URL;
import java.net.URLClassLoader;

public final class MockClassLoader extends URLClassLoader {

  public MockClassLoader(final URL[] urls, final ClassLoader parent) {
    super(urls, parent);
  }

  @Override
  public void addURL(final URL url) {
    super.addURL(url);
  }
}
