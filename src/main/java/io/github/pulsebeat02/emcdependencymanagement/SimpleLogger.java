package io.github.pulsebeat02.emcdependencymanagement;

/**
 * Logger interface to use for logging messages.
 */
public interface SimpleLogger {

  /**
   * Logs an info line.
   *
   * @param line the info line
   */
  void info(final String line);

  /**
   * Logs a warning line.
   *
   * @param line the warning line
   */
  void warning(final String line);

  /**
   * Logs an error line.
   *
   * @param line the error line
   */
  void error(final String line);
}
