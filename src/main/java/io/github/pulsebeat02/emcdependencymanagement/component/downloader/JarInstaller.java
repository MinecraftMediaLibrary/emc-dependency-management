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
package io.github.pulsebeat02.emcdependencymanagement.component.downloader;

import io.github.pulsebeat02.emcdependencymanagement.SimpleLogger;
import io.github.pulsebeat02.emcdependencymanagement.component.Artifact;
import io.github.pulsebeat02.emcdependencymanagement.component.Repository;
import io.github.pulsebeat02.emcdependencymanagement.util.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public final class JarInstaller {

  private final SimpleLogger logger;
  private final Collection<Artifact> artifacts;
  private final Collection<Repository> repositories;
  private final Set<Path> paths;
  private final Path target;

  JarInstaller(
      final SimpleLogger logger,
      final Collection<Artifact> artifacts,
      final Collection<Repository> repositories,
      final Path target) {
    this.logger = logger;
    this.artifacts = artifacts;
    this.repositories = repositories;
    this.target = target;
    this.paths = new HashSet<>();
    repositories.addAll(
        Arrays.asList(Repository.MAVEN_CENTRAL, Repository.OSS_SONATYPE, Repository.JCENTER));
  }

  public static JarInstaller ofInstaller(
      final SimpleLogger logger,
      final Collection<Artifact> artifacts,
      final Collection<Repository> repositories,
      final Path target) {
    return new JarInstaller(logger, artifacts, repositories, target);
  }

  public Collection<Path> install() throws IOException {
    this.createFolder();
    this.artifacts.parallelStream().forEach(this::installArtifact);
    return this.paths;
  }

  private void createFolder() throws IOException {
    if (Files.notExists(this.target)) {
      Files.createDirectories(this.target);
    }
  }

  private void installArtifact(final Artifact artifact) {
    final String appended = this.getAppendedUrl(artifact);
    for (final Repository repository : this.repositories) {
      this.tryRepository(repository, appended).ifPresent(this::downloadJarExceptionally);
    }
  }

  private void downloadJarExceptionally(final String url) {
    try {
      this.downloadJar(url);
    } catch (final IOException e) {
      this.logger.error(String.format("Failed to download JAR located at url %s!", url));
    }
  }

  private void downloadJar(final String url) throws IOException {

    final Path jarPath = this.downloadFile(url);
    if (this.checkHash(jarPath, url)) {
      this.downloadJar(url);
    }

    this.paths.add(jarPath);
  }

  private boolean checkHash(final Path jarPath, final String url) throws IOException {

    final String originalHash = this.getCheckSumArtifact(url);
    final String newHash = FileUtils.getUppercaseHash(jarPath);
    if (originalHash.isEmpty()) {
      this.logger.warning(
          String.format("Could not retrieve SHA1 hash for artifact %s! Skipping hash check!", url));
      return false;
    }

    if (!originalHash.equals(newHash)) {
      Files.deleteIfExists(jarPath);
      return true;
    }

    return false;
  }

  private Path downloadFile(final String url) throws IOException {
    final URL website = new URL(url);
    final String jar = this.getFilename(url);
    final Path jarPath = this.target.resolve(jar);
    try (final InputStream in = website.openStream()) {
      Files.copy(in, jarPath, StandardCopyOption.REPLACE_EXISTING);
    }
    return jarPath;
  }

  private String getFilename(final String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

  private Optional<String> tryRepository(final Repository repository, final String jarUrl) {
    final String url = String.format("%s%s", repository.getUrl(), jarUrl);
    try {
      if (this.isValidUrl(url)) {
        return Optional.of(url);
      }
    } catch (final IOException ignored) {
    }
    return Optional.empty();
  }

  private boolean isValidUrl(final String url) throws IOException {
    return this.createConnection(url).getResponseCode() == HttpURLConnection.HTTP_OK;
  }

  private String getAppendedUrl(final Artifact artifact) {
    final String groupId = artifact.getGroup().replace('.', '/');
    final String artifactId = artifact.getArtifact();
    final String version = artifact.getVersion();
    final String jar = String.format("%s-%s.jar", artifactId, version);
    return String.format("%s/%s/%s/%s", groupId, artifactId, version, jar);
  }

  private String getCheckSumArtifact(final String url) throws IOException {
    final String hashUrl = String.format("%s.sha1", url);
    try (final Scanner scanner =
        new Scanner(new URL(hashUrl).openStream(), "UTF-8").useDelimiter("\\A")) {
      return scanner.next().toUpperCase(Locale.ROOT);
    }
  }

  private HttpURLConnection createConnection(final String url) throws IOException {
    final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
    con.setRequestMethod("HEAD");
    return con;
  }

  public Collection<Artifact> getArtifacts() {
    return this.artifacts;
  }

  public Collection<Repository> getRepositories() {
    return this.repositories;
  }

  public Path getTarget() {
    return this.target;
  }
}
