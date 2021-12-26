/**
 * MIT License
 *
 * <p>Copyright (c) 2021 Brandon Li
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.pulsebeat02.emcdependencymanagement;

import io.github.pulsebeat02.emcdependencymanagement.component.Artifact;
import io.github.pulsebeat02.emcdependencymanagement.component.Relocation;
import io.github.pulsebeat02.emcdependencymanagement.component.Repository;
import io.github.pulsebeat02.emcdependencymanagement.component.downloader.JarInstaller;
import io.github.pulsebeat02.emcdependencymanagement.component.relocator.FileRelocator;
import io.github.pulsebeat02.emcdependencymanagement.component.search.JarSearcher;
import io.github.pulsebeat02.emcdependencymanagement.injector.UnsafeInjection;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Main class for handling JAR dependencies. */
public final class EMCDepManagement {

  private final SimpleLogger logger;
  private final Collection<Artifact> artifacts;
  private final Collection<Relocation> relocations;
  private final Collection<Repository> repositories;
  private final Path folder;

  EMCDepManagement(
      final SimpleLogger logger,
      final Collection<Artifact> artifacts,
      final Collection<Relocation> relocations,
      final Collection<Repository> repositories,
      final Path folder) {
    this.logger = logger;
    this.artifacts = artifacts == null ? new ArrayList<>() : artifacts;
    this.relocations = relocations == null ? new ArrayList<>() : relocations;
    this.repositories = repositories == null ? new ArrayList<>() : repositories;
    this.folder = folder;
  }

  /**
   * Creates a new builder.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Loads the JARs into the classpath.
   *
   * @throws IOException if an issue occurred during installation
   * @throws ReflectiveOperationException if an issue occurred during loading
   */
  public void load() throws IOException, ReflectiveOperationException {

    final Collection<Artifact> download = this.needsDownload();
    final Collection<Path> paths = this.installedJars(download);

    this.relocate(paths);
    this.inject();
  }

  private void inject() throws IOException, NoSuchFieldException {
    final UnsafeInjection injection =
        UnsafeInjection.ofInjection(
            this.getJars(), (URLClassLoader) EMCDepManagement.class.getClassLoader());
    injection.inject();
  }

  private void relocate(final Collection<Path> paths) throws IOException {
    final FileRelocator relocator = FileRelocator.ofRelocator(this.relocations, paths);
    relocator.relocate();
  }

  private Collection<Path> installedJars(final Collection<Artifact> download) throws IOException {
    final JarInstaller installer =
        JarInstaller.ofInstaller(this.logger, download, this.repositories, this.folder);
    return installer.install();
  }

  private Collection<Artifact> needsDownload() throws IOException {
    final JarSearcher searcher = JarSearcher.ofSearcher(this.artifacts, this.folder);
    return searcher.getNeededInstallation();
  }

  private Collection<Path> getJars() throws IOException {
    try (final Stream<Path> paths = Files.walk(this.folder).parallel()) {
      return paths.filter(Files::isRegularFile).collect(Collectors.toList());
    }
  }

  public SimpleLogger getLogger() {
    return this.logger;
  }

  public Collection<Artifact> getArtifacts() {
    return this.artifacts;
  }

  public Collection<Relocation> getRelocations() {
    return this.relocations;
  }

  public Collection<Repository> getRepositories() {
    return this.repositories;
  }

  public Path getFolder() {
    return this.folder;
  }

  public static class Builder {

    private SimpleLogger logger;
    private Collection<Artifact> artifacts;
    private Collection<Relocation> relocations;
    private Collection<Repository> repositories;
    private Path folder;
    private String name;

    {
      this.logger =
          new SimpleLogger() {
            @Override
            public void info(final String line) {
              System.out.printf("[INFO] %s%n", line);
            }

            @Override
            public void warning(final String line) {
              System.out.printf("[WARN] %s%n", line);
            }

            @Override
            public void error(final String line) {
              System.err.printf("[ERROR] %s%n", line);
            }
          };
      this.artifacts = new ArrayList<>();
      this.relocations = new ArrayList<>();
      this.repositories = new ArrayList<>();
    }

    /**
     * Sets the application name.
     *
     * @param name the name
     * @return the same builder
     */
    public Builder setApplicationName(final String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the artifacts to install.
     *
     * @param artifacts the artifacts
     * @return the same builder
     */
    public Builder setArtifacts(final Collection<Artifact> artifacts) {
      this.artifacts = artifacts;
      return this;
    }

    /**
     * Adds an artifact to install.
     *
     * @param artifact the artifact
     * @return the same builder
     */
    public Builder addArtifact(final Artifact artifact) {
      this.artifacts.add(artifact);
      return this;
    }

    /**
     * Sets the relocations to apply.
     *
     * @param relocations the relocations
     * @return the same builder
     */
    public Builder setRelocations(final Collection<Relocation> relocations) {
      this.relocations = relocations;
      return this;
    }

    /**
     * Adds a relocation to apply.
     *
     * @param relocation the relocation
     * @return the same builder
     */
    public Builder addRelocation(final Relocation relocation) {
      this.relocations.add(relocation);
      return this;
    }

    /**
     * Sets the repositories to search from.
     *
     * @param repositories the repositories
     * @return the same builder
     */
    public Builder setRepos(final Collection<Repository> repositories) {
      this.repositories = repositories;
      return this;
    }

    /**
     * Adds a repository to search from.
     *
     * @param repository the repository
     * @return the same builder
     */
    public Builder addRepo(final Repository repository) {
      this.repositories.add(repository);
      return this;
    }

    /**
     * Sets the target directory.
     *
     * @param folder the folder
     * @return the same builder
     */
    public Builder setFolder(final Path folder) {
      this.folder = folder;
      return this;
    }

    /**
     * Sets the logger to log messages.
     *
     * @param logger the logger
     * @return the same builder
     */
    public Builder setLogger(final SimpleLogger logger) {
      this.logger = logger;
      return this;
    }

    /**
     * Creates a new EMCDepManagement.
     *
     * @return a new EMCDepManagement
     * @throws IOException if an issue occured during folder creation
     */
    public EMCDepManagement create() throws IOException {
      final Path file = (this.folder == null ? this.getFolder() : this.folder).resolve(this.name);
      this.createFile(file);
      return new EMCDepManagement(
          this.logger,
          new ArrayList<>(this.artifacts),
          new ArrayList<>(this.relocations),
          new ArrayList<>(this.repositories),
          file);
    }

    private void createFile(final Path file) throws IOException {
      if (Files.notExists(file)) {
        Files.createDirectories(file);
      }
    }

    private Path getFolder() {
      return Paths.get(System.getProperty("user.home"), ".emc-dependency-management");
    }
  }
}
