package io.github.pulsebeat02.emcdependencymanagement;

public interface SimpleLogger {

  void info(final String line);

  void warning(final String line);

  void error(final String line);
}
