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
package io.github.pulsebeat02.emcdependencymanagement.component.search;

import io.github.pulsebeat02.emcdependencymanagement.component.Artifact;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public final class JarSearcher {

  private final Collection<Artifact> artifacts;
  private final Path folder;

  JarSearcher(final Collection<Artifact> artifacts, final Path folder) {
    this.artifacts = artifacts;
    this.folder = folder;
  }

  public static JarSearcher ofSearcher(final Collection<Artifact> artifacts, final Path folder) {
    return new JarSearcher(artifacts, folder);
  }

  public Collection<Artifact> getNeededInstallation() throws IOException {
    final List<Artifact> needed = new ArrayList<>();
    for (final Artifact artifact : this.artifacts) {
      this.handleArtifact(needed, artifact);
    }
    return needed;
  }

  private void handleArtifact(final List<Artifact> needed, final Artifact artifact)
      throws IOException {
    try (final Stream<Path> stream = Files.walk(this.folder).parallel()) {
      final boolean present =
          stream.filter(Files::isRegularFile).anyMatch(path -> this.matchFile(artifact, path));
      if (!present) {
        needed.add(artifact);
      }
    }
  }

  private boolean matchFile(final Artifact artifact, final Path file) {
    final String name = file.getFileName().toString();
    return name.contains(artifact.getGroup())
        && name.contains(artifact.getArtifact())
        && name.contains(artifact.getVersion());
  }
}
