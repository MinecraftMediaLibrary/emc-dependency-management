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

/** Class used to hold repository URLs. */
public final class Repository {

  public static final Repository MAVEN_CENTRAL;
  public static final Repository OSS_SONATYPE;
  public static final Repository JCENTER;

  static {
    MAVEN_CENTRAL = ofRepo("https://repo1.maven.org/maven2/");
    OSS_SONATYPE = ofRepo("https://oss.sonatype.org/content/repositories/releases/");
    JCENTER = ofRepo("https://jcenter.bintray.com/");
  }

  private final String url;

  Repository(final String url) {
    this.url = url;
  }

  /**
   * Creates a new repository based on the url.
   *
   * @param url the url
   * @return a new Repository
   */
  public static Repository ofRepo(final String url) {
    return new Repository(url);
  }

  public String getUrl() {
    return this.url;
  }
}
