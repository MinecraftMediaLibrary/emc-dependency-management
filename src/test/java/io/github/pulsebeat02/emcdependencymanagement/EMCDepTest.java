package io.github.pulsebeat02.emcdependencymanagement;

import static io.github.pulsebeat02.emcdependencymanagement.component.Artifact.ofArtifact;
import static io.github.pulsebeat02.emcdependencymanagement.component.Relocation.ofRelocation;

import io.github.pulsebeat02.emcdependencymanagement.component.Artifact;
import io.github.pulsebeat02.emcdependencymanagement.component.Relocation;
import io.github.pulsebeat02.emcdependencymanagement.component.Repository;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EMCDepTest {

  public static void main(final String[] args) throws ReflectiveOperationException, IOException {
    setClassLoader();
    final Set<Repository> repos =
        Stream.of(
                "https://papermc.io/repo/repository/maven-public/",
                "https://oss.sonatype.org/content/repositories/snapshots",
                "https://libraries.minecraft.net/",
                "https://jitpack.io/",
                "https://repo.codemc.org/repository/maven-public/",
                "https://m2.dv8tion.net/releases/",
                "https://repo.vshnv.tech/releases/",
                "https://repo.mattstudios.me/artifactory/public/",
                "https://pulsebeat02.jfrog.io/artifactory/pulse-gradle-release-local/",
                "https://pulsebeat02.jfrog.io/artifactory/pulse-libs-release-local/")
            .map(Repository::ofRepo)
            .collect(Collectors.toSet());

    final Set<Artifact> artifacts =
        Stream.of(
                ofArtifact("uk:co:caprica", "vlcj", "4:7:1"),
                ofArtifact("uk:co:caprica", "vlcj-natives", "4:5:0"),
                ofArtifact("com:github:sealedtx", "java-youtube-downloader", "3:0:1"), //
                ofArtifact("com:alibaba", "fastjson", "1:2:78"),
                ofArtifact("net:java:dev:jna", "jna", "5:9:0"),
                ofArtifact("net:java:dev:jna", "jna-platform", "5:9:0"),
                ofArtifact("se:michaelthelin:spotify", "spotify-web-api-java", "6:5:4"),
                ofArtifact("com:github:kokorin:jaffree", "jaffree", "2021:08:31"),
                ofArtifact("org:jcodec", "jcodec", "0:2:5"),
                ofArtifact("com:github:ben-manes:caffeine", "caffeine", "3:0:3"),
                ofArtifact("io:github:pulsebeat02", "jarchivelib", "v1:4:0"),
                ofArtifact("io:github:pulsebeat02", "emc-installers", "v1:0:1"),
                ofArtifact("it:unimi:dsi", "fastutil", "8:5:6"))
            .collect(Collectors.toSet());

    final Set<Relocation> relocations =
        Stream.of(
                ofRelocation("uk:co:caprica:vlcj", "io:github:pulsebeat02:ezmediacore:lib:vlcj"),
                ofRelocation(
                    "com:github:kiulian:downloader",
                    "io:github:pulsebeat02:ezmediacore:lib:youtube"),
                ofRelocation(
                    "com:wrapper:spotify", "io:github:pulsebeat02:ezmediacore:lib:spotify"),
                ofRelocation("com:github:kokorin", "io:github:pulsebeat02:ezmediacore:lib:kokorin"),
                ofRelocation("org:jcodec", "io:github:pulsebeat02:ezmediacore:lib:jcodec"),
                ofRelocation(
                    "com:github:benmanes:caffeine",
                    "io:github:pulsebeat02:ezmediacore:lib:caffeine"),
                ofRelocation(
                    "it:unimi:dsi:fastutil", "io:github:pulsebeat02:ezmediacore:lib:fastutil"))
            .collect(Collectors.toSet());

    final EMCDepManagement management =
        EMCDepManagement.builder()
            .setApplicationName("EzMediaCore - TESTING")
            .setFolder(Paths.get(System.getProperty("user.dir"), "emc-library-test"))
            .setRepos(repos)
            .setArtifacts(artifacts)
            .setRelocations(relocations)
            .createEMCDepManagement();
    management.load();
  }

  private static void setClassLoader() {
    Thread.currentThread().setContextClassLoader(new ClassLoaderConfig().getClassLoader());
  }
}
