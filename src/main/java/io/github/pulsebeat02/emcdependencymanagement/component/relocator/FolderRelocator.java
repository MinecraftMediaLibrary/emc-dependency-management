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
package io.github.pulsebeat02.emcdependencymanagement.component.relocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;

public final class FolderRelocator {

  private final Collection<Relocation> relocations;
  private final Collection<Path> files;

  private FolderRelocator(
      final Collection<io.github.pulsebeat02.emcdependencymanagement.component.Relocation>
          relocations,
      final Collection<Path> files) {
    this.relocations = this.convertToProperRelocator(relocations);
    this.files = files;
  }

  public static FolderRelocator ofRelocator(
      final Collection<io.github.pulsebeat02.emcdependencymanagement.component.Relocation>
          relocations,
      final Collection<Path> files) {
    return new FolderRelocator(relocations, files);
  }

  public void relocate() throws IOException {
    try (final Stream<Path> stream = this.files.parallelStream()) {
      stream.filter(Files::isRegularFile).forEach(this::relocateJarExceptionally);
    }
  }

  private void relocateJarExceptionally(final Path jar) {
    try {
      this.relocateJar(jar);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private void relocateJar(final Path jar) throws IOException {
    final String original = jar.getFileName().toString();
    final String name = String.format("temp-%s", original);
    final Path relocated = jar.getParent().resolve(name);
    final JarRelocator relocator =
        new JarRelocator(jar.toFile(), relocated.toFile(), this.relocations);
    relocator.run();
    Files.deleteIfExists(jar);
    Files.move(relocated, relocated.resolveSibling(original));
  }

  private Collection<Relocation> convertToProperRelocator(
      final Collection<io.github.pulsebeat02.emcdependencymanagement.component.Relocation>
          relocations) {
    return relocations.stream().map(this::createRelocation).collect(Collectors.toList());
  }

  private Relocation createRelocation(
      final io.github.pulsebeat02.emcdependencymanagement.component.Relocation relocation) {
    return new Relocation(relocation.getOriginal(), relocation.getRelocation());
  }
}
