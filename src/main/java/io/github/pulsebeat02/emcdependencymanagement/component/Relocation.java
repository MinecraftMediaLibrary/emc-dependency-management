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
package io.github.pulsebeat02.emcdependencymanagement.component;

import static io.github.pulsebeat02.emcdependencymanagement.util.PackageUtils.correctPackage;

/** Class used to hold relocations. */
public final class Relocation {

  private final String original;
  private final String relocation;

  Relocation(final String original, final String relocation) {
    this.original = correctPackage(original);
    this.relocation = correctPackage(relocation);
  }

  /**
   * Create a new relocation standard. original relocation and new relocation String must ALWAYS use
   * colons (:) in place of periods (.)!
   *
   * <p>For example, io.github.pulsebeat02 -> io:github:pulsebeat02
   *
   * @param original the original relocation
   * @param relocation the new relocation
   */
  public static Relocation ofRelocation(final String original, final String relocation) {
    return new Relocation(original, relocation);
  }

  public String getOriginal() {
    return this.original;
  }

  public String getRelocation() {
    return this.relocation;
  }
}
