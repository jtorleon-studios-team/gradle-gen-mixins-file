package com.github.jtorleonstudios.gradle.gen.mixins.file;

import com.github.jtorleonstudios.gradle.gen.mixins.file.dto.ClassFileInfoDTO;
import com.github.jtorleonstudios.gradle.gen.mixins.file.dto.MixinFileDTO;
import com.github.jtorleonstudios.gradle.gen.mixins.file.dto.MixinsJsonFileDTO;
import com.github.jtorleonstudios.gradle.gen.mixins.file.exceptions.MultipleMixinPackagesException;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenMixinsFileTask extends DefaultTask {
  public final static String GEN_PATH_JAVA = "generated-mixins-src/main/java";
  public final static String GEN_PATH_RESOURCE = "generated-mixins-src/main/resources";
  public final static String TASK_NAME = "genMixinsFile";

  public static void register(
    @NotNull Project project
  ) {
    var task = project
      .getTasks()
      .register(
        GenMixinsFileTask.TASK_NAME,
        GenMixinsFileTask.class,
        v -> {
          v.setGroup("genAssets");
          v.setDescription("Generate mixins file");
        }
      );

    project
      .getTasks()
      .named("compileJava")
      .configure(v -> v.dependsOn(task));

    // Setup source set
    project
      .getExtensions()
      .getByType(SourceSetContainer.class)
      .named(
        SourceSet.MAIN_SOURCE_SET_NAME,
        sourceSet -> {
          var buildDir = project.getLayout().getBuildDirectory();
          sourceSet
            .getJava()
            .srcDir(buildDir.dir(GenMixinsFileTask.GEN_PATH_JAVA));
          sourceSet
            .getResources()
            .srcDir(buildDir.dir(GenMixinsFileTask.GEN_PATH_RESOURCE));
        }
      );

    // Setup IDEA
    project
      .getPlugins()
      .withId(
        "idea",
        plugin -> project
          .getExtensions()
          .configure(
            IdeaModel.class,
            idea -> {
              var buildDir = project.getLayout().getBuildDirectory();
              idea
                .getModule()
                .getGeneratedSourceDirs()
                .add(buildDir
                  .dir(GenMixinsFileTask.GEN_PATH_JAVA)
                  .get()
                  .getAsFile()
                );
              idea
                .getModule()
                .getResourceDirs()
                .add(buildDir
                  .dir(GenMixinsFileTask.GEN_PATH_RESOURCE)
                  .get()
                  .getAsFile()
                );
            }
          )
      );
  }

  @TaskAction
  void run() {
    // clean old generation
    this.cleanGeneratedDirectory(GEN_PATH_JAVA);
    this.cleanGeneratedDirectory(GEN_PATH_RESOURCE);

    var logger = getProject().getLogger();
    var srcDir = getProject().file("src/main/java").toPath();
    var common = new ArrayList<MixinFileDTO>();
    var client = new ArrayList<MixinFileDTO>();
    var server = new ArrayList<MixinFileDTO>();
    var checkUniqueMixinPackage = new HashSet<String>();
    var allJavaPackage = new HashSet<String>();

    // Scanning for mixins common, client, server
    logger.lifecycle("Scanning for mixins...");
    try (var stream = Files.walk(srcDir)) {
      stream
        .filter(Files::isRegularFile)
        .filter(v -> v.toString().endsWith(".java"))
        .forEach(v -> {
          var classFileInfoDTO = ClassFileInfoDTO.from(srcDir.relativize(v));
          allJavaPackage.add(classFileInfoDTO.packageName());
          // CLIENT
          if (classFileInfoDTO.is(MixinSideType.CLIENT)) {
            var mixinFileDto = MixinFileDTO.from(MixinSideType.CLIENT, classFileInfoDTO);
            checkUniqueMixinPackage.add(mixinFileDto.packageOwnerName());
            client.add(mixinFileDto);
          }

          // SERVER
          else if (classFileInfoDTO.is(MixinSideType.SERVER)) {
            var mixinFileDto = MixinFileDTO.from(MixinSideType.COMMON, classFileInfoDTO);
            checkUniqueMixinPackage.add(mixinFileDto.packageOwnerName());
            server.add(mixinFileDto);
          }

          // COMMON
          else if (classFileInfoDTO.is(MixinSideType.COMMON)) {
            var mixinFileDto = MixinFileDTO.from(MixinSideType.COMMON, classFileInfoDTO);
            checkUniqueMixinPackage.add(mixinFileDto.packageOwnerName());
            common.add(mixinFileDto);
          }

        });
    } catch (IOException e) {
      throw new GradleException(e.getMessage(), e);
    }


    if (checkUniqueMixinPackage.size() > 1) {
      throw new MultipleMixinPackagesException(checkUniqueMixinPackage);
    }

    // Show to user
    logger.lifecycle("Client mixins: {}", client.isEmpty() ? System.lineSeparator() + " |- empty" : "");
    client.forEach(v -> logger.lifecycle(" |- Adding mixin: \"{}\"", v.mixinName()));
    logger.lifecycle("Server mixins: {}", server.isEmpty() ? System.lineSeparator() + " |- empty" : "");
    server.forEach(v -> logger.lifecycle(" |- Adding mixin: \"{}\"", v.mixinName()));
    logger.lifecycle("Common mixins: {}", common.isEmpty() ? System.lineSeparator() + " |- empty" : "");
    common.forEach(v -> logger.lifecycle(" |- Adding mixin: \"{}\"", v.mixinName()));

    this.generateMixinsJsonResource(MixinsJsonFileDTO.from(
      common.isEmpty() && client.isEmpty() && server.isEmpty()
        ? this.generateFallbackMixinsPackage(allJavaPackage)
        : checkUniqueMixinPackage.stream().findFirst().orElseThrow(),
      getProject().getExtensions().getByType(GenMixinsFileExt.class),
      common,
      client,
      server
    ));
  }

  private @NotNull File getFallbackJavaDirectory(
    @NotNull String fallbackPackage
  ) {
    return getProject()
      .getLayout()
      .getBuildDirectory()
      .dir(GEN_PATH_JAVA + "/" + fallbackPackage.replace(".", "/")
      )
      .get()
      .getAsFile();
  }

  private @NotNull String generateFallbackMixinsPackage(
    @NotNull Set<String> allJavaPackage
  ) {
    if (allJavaPackage.isEmpty()) {
      throw new GradleException(
        """
          No Java packages were detected in the project.
          Cannot determine a valid package for generating mixins.json.
          Please ensure that your project contains at least one Java class.
          If your project is intended to be empty, consider adding a placeholder package for mixins."""
      );
    }

    var shortestPackage = allJavaPackage
      .stream()
      .min(Comparator.comparingInt(String::length))
      .orElseThrow(() -> new GradleException("Unexpected error: could not determine the shortest package."));// should never happens

    var fallbackPackage = shortestPackage + ".fallback.unused.mixins";
    var fallbackDirectory = this.getFallbackJavaDirectory(fallbackPackage);

    tryCreateDirectoryOrThrow(fallbackDirectory);

    tryWriteFileOrThrow(
      fallbackDirectory,
      "EmptyMixin.java",
      Stream.of(
        "package " + fallbackPackage + ";",
        "public interface EmptyMixin {}"
      ).collect(Collectors.joining(System.lineSeparator()))
    );

    return fallbackPackage;
  }

  private @NotNull File getResourceDirectory() {
    return getProject()
      .getLayout()
      .getBuildDirectory()
      .dir(GEN_PATH_RESOURCE)
      .get()
      .getAsFile();
  }

  private void generateMixinsJsonResource(
    MixinsJsonFileDTO mixinsJsonFileDTO
  ) {
    var resourceDirectory = this.getResourceDirectory();
    tryCreateDirectoryOrThrow(resourceDirectory);

    try {
      tryWriteFileOrThrow(
        resourceDirectory,
        mixinsJsonFileDTO.fileName(),
        mixinsJsonFileDTO.toJson()
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void tryCreateDirectoryOrThrow(
    @NotNull File dir
  ) {
    var resultMkDirs = dir.mkdirs();

    if (!resultMkDirs && !dir.exists()) {
      throw new GradleException(String.format(
        "Failed to generate output directory '%s'.%n" +
        "Possible reasons:%n" +
        " - Insufficient permissions to create directories%n" +
        " - A file with the same name already exists%n" +
        " - Path contains invalid characters%n%n" +
        "Please ensure that the path is writable and does not conflict with existing files.",
        dir.getAbsolutePath()
      ));
    }
  }

  private static void tryWriteFileOrThrow(
    @NotNull File directory,
    @NotNull String fileName,
    @NotNull String content
  ) {
    var file = new File(directory, fileName);
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(content);
    } catch (IOException e) {
      throw new GradleException(e.getMessage(), e);
    }
  }

  private void cleanGeneratedDirectory(String directory) {
    var outputDir = getProject()
      .getLayout()
      .getBuildDirectory()
      .file(directory)
      .get()
      .getAsFile();

    if (outputDir.exists()) {
      getProject().delete(outputDir);
    }
  }

}
