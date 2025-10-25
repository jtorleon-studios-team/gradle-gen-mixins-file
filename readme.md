# Gradle Plugin : Generate Mixins File

This plugin automatically generates a `mixins.json` for your project and enforces
a consistent package structure.

- **License:** MIT
- **Author:** JTorleon Studios Team
- **Public Maven Repository:** https://jtorleon-studios-team-github-io.pages.dev/

## Installation

Add the public Maven repository in your `settings.gradle`:

```groovy
pluginManagement {
  repositories {
    maven {
      url = 'https://jtorleon-studios-team-github-io.pages.dev/maven'
    }
  }
}

```

Then apply the plugin in your `build.gradle`:


```groovy
plugin {
  id 'gradle-gen-mixins-file' version "1.0.0"
}
```

Configuration:

```groovy
setupMixinsFile {
  fileName = mod_id              // string - required !

  // optional field:
  compatibilityLevel = "JAVA_17" // string - default value is JAVA_8

  required = true                // boolean - default value is true

  minVersion = "0.8"             // string - default value is 0.8

  plugin = ""                    // string - default value is empty (unused)

  injectorsDefaultRequire = 1    // int - default value is 1

}
```
 
## Conventions Mixin Package Introduced
 
- All mixins must be centralized under a single package named `mixins`.
- Client-only mixins must reside in `mixins.client`.
- Server-only mixins must reside in `mixins.server`.
- Mixins used by both client and server must reside in `mixins.common`.

> [!CAUTION]
> Mixing multiple base packages (e.g., com.example.project.mixins and com.example.project.test.mixins) 
> will cause a build error with a clear message showing incorrect vs correct structure.

Example of the expected structure:

```
 |- com.github.example
     |- mixins
     |   |- client
     |   |   |- TestClientMixin.java <- (com.github.example.mixins.client.TestClientMixin.java)
     |   |
     |   |- server
     |   |   |- TestServerMixin.java <- (com.github.example.mixins.server.TestServerMixin.java)
     |   |
     |   |- common
     |       |- TestCommonMixin.java <- (com.github.example.mixins.common.TestCommonMixin.java)
     |   
     |- Main.java
```

If the project does not use any mixins, the plugin will still generate a valid mixins.json
and create a fallback mixins package in the generated sources to ensure stability.

```
 |- com.github.example
     |- fallback
     |   |- unused
     |       |- mixins
     |           |- EmptyMixin.java <- (com.github.example.fallback.unused.mixins.EmptyMixin.java)
     |
     |   
     |- Main.java
```

> [!NOTE]
> `EmptyMixin.java` is not an actual mixin and does nothing—it is simply an empty `interface`.
> It was added to ensure that the `fallback.unused.mixins` package is preserved in the output JAR. 
> This package also serves as the value for the package field in the generated `mixins.json` file.
> It guarantees that the build and runtime remain stable even without actual mixins, and ensures 
> compatibility with tools that read the mixins.json file.

# Note

If you encounter the following error during build your mod:

```
* What went wrong: Execution failed for task ':remapJar'. 
> A failure occurred while executing net.fabricmc.loom.task.RemapJarTask$RemapAction 
> Failed to remap, java.nio.file.FileSystemAlreadyExistsException: null
```

This usually happens because a Java process is already running (for example, a Gradle
daemon or an IntelliJ instance).

Possible solutions:

- Stop all Gradle daemons `gradlew --stop`
- Close any IntelliJ / background Java processes
- Clean and rebuild `gradlew clean build`

This error is not caused by the plugin or project code; it occurs only when a JAR file
is already mounted as a FileSystem by another process.