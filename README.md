# EMC Dependency Management
EMC Dependency Management is a lightweight dependency that downloads dependencies on runtime for
Bukkit Plugins. Example code can be seen here:

```java
    final EMCDepManagement management = EMCDepManagement.builder()
        .setApplicationName("MyPlugin")
        .addArtifact(ofArtifact("me:lucko", "jar-relocator", "1.5"))
        .addRelocation(ofRelocation("me:lucko", "io:github:pulsebeat02:lucko"))
        .createEMCDepManagement();
    management.load();
```

## Setup

1) Add the repository:
```kotlin
    repositories {
        maven("https://pulsebeat02.jfrog.io/artifactory/pulse-gradle-release-local");
    }
```

2) Add the dependency:
```kotlin
    dependencies {
        implementation("io.github.pulsebeat02", "emc-dependency-management", "v1.0.1")
    }
```