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
package io.github.pulsebeat02.emcdependencymanagement.component.downloader;

import io.github.pulsebeat02.emcdependencymanagement.component.Artifact;
import io.github.pulsebeat02.emcdependencymanagement.component.Repository;
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
import java.util.Optional;
import java.util.Set;

public final class JarInstaller {

  private final Collection<Artifact> artifacts;
  private final Collection<Repository> repositories;
  private final Set<Path> paths;
  private final Path target;

  JarInstaller(
      final Collection<Artifact> artifacts,
      final Collection<Repository> repositories,
      final Path target) {
    this.artifacts = artifacts;
    this.repositories = repositories;
    this.target = target;
    this.paths = new HashSet<>();
    repositories.addAll(
        Arrays.asList(Repository.MAVEN_CENTRAL, Repository.OSS_SONATYPE, Repository.JCENTER));
  }

  public static JarInstaller ofInstaller(
      final Collection<Artifact> artifacts,
      final Collection<Repository> repositories,
      final Path target) {
    return new JarInstaller(artifacts, repositories, target);
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
      e.printStackTrace();
    }
  }

  private void downloadJar(final String url) throws IOException {
    final URL website = new URL(url);
    final String jar = this.getFilename(url);
    final Path jarPath = this.target.resolve(jar);
    try (final InputStream in = website.openStream()) {
      Files.copy(in, jarPath, StandardCopyOption.REPLACE_EXISTING);
    }
    this.paths.add(jarPath);
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
    final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
    con.setRequestMethod("HEAD");
    return con.getResponseCode() == HttpURLConnection.HTTP_OK;
  }

  private String getAppendedUrl(final Artifact artifact) {
    final String groupId = artifact.getGroup().replace('.', '/');
    final String artifactId = artifact.getArtifact();
    final String version = artifact.getVersion();
    final String jar = String.format("%s-%s.jar", artifactId, version);
    return String.format("%s/%s/%s/%s", groupId, artifactId, version, jar);
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
