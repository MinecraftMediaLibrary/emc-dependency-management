package io.github.pulsebeat02.emcdependencymanagement;

import static io.github.pulsebeat02.emcdependencymanagement.component.Artifact.ofArtifact;
import static io.github.pulsebeat02.emcdependencymanagement.component.Relocation.ofRelocation;
import static io.github.pulsebeat02.emcdependencymanagement.component.Repository.ofRepo;

import java.io.IOException;

public final class EMCDepTest {

  public static void main(final String[] args) throws ReflectiveOperationException, IOException {
    setClassLoader();
    final EMCDepManagement management = EMCDepManagement.builder()
        .setApplicationName("EMC Dependency Test")
        .addRepo(ofRepo("https://libraries.minecraft.net/"))
        .addArtifact(ofArtifact("me:lucko", "jar-relocator", "1:5"))
        .addArtifact(ofArtifact("com:mojang", "authlib", "1:5:26"))
        .addRelocation(ofRelocation("me:lucko", "io:github:pulsebeat02:lib:lucko"))
        .addRelocation(ofRelocation("com:mojang", "io:github:pulsebeat02:lib:mojang"))
        .createEMCDepManagement();
    management.load();
  }

  private static void setClassLoader() {
    Thread.currentThread().setContextClassLoader(new ClassLoaderConfig().getClassLoader());
  }
}
