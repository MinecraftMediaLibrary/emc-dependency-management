package io.github.pulsebeat02.emcdependencymanagement;

import java.net.URL;

public final class ClassLoaderConfig {

  private final MockClassLoader classLoader;

  ClassLoaderConfig() {
    this.classLoader = new MockClassLoader(new URL[0], this.getClass().getClassLoader());
  }

  public MockClassLoader getClassLoader() {
    return this.classLoader;
  }
}
