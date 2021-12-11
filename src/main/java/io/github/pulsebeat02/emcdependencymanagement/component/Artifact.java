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
package io.github.pulsebeat02.emcdependencymanagement.component;

import static io.github.pulsebeat02.emcdependencymanagement.util.Packages.correctPackage;

public final class Artifact {

  private final String group;
  private final String artifact;
  private final String version;

  /**
   * Create a new Artifact. The group id, artifact id, and version String must ALWAYS use colons (:)
   * in place of periods (.)!
   *
   * <p>For example, io.github.pulsebeat02 -> io:github:pulsebeat02
   *
   * @param group the group id
   * @param artifact the artifact id
   * @param version the version
   */
  Artifact(final String group, final String artifact, final String version) {
    this.group = correctPackage(group);
    this.artifact = correctPackage(artifact);
    this.version = correctPackage(version);
  }

  public static Artifact ofArtifact(final String group, final String artifact, final String version) {
    return new Artifact(group, artifact, version);
  }

  public String getGroup() {
    return this.group;
  }

  public String getArtifact() {
    return this.artifact;
  }

  public String getVersion() {
    return this.version;
  }
}
