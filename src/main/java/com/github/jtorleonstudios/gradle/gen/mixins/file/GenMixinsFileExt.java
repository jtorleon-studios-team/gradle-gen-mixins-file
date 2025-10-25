package com.github.jtorleonstudios.gradle.gen.mixins.file;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public abstract class GenMixinsFileExt {
  /**
   * <p>
   * The name of this extension when it is added to a Gradle project.
   * </p>
   */
  public final static String NAME = "setupMixinsFile";

  public static void register(
    @NotNull Project project
  ) {
    project
      .getExtensions()
      .create(
        GenMixinsFileExt.NAME,
        GenMixinsFileExt.class
      );
  }

  public GenMixinsFileExt() {

    this.getRequired().convention(true);

    this.getPlugin().convention("");

    this.getInjectorsDefaultRequire().convention(1);

    this.getCompatibilityLevel().convention("JAVA_8");

    this.getMinVersion().convention("0.8");
  }

  public abstract Property<String> getFileName();

  public abstract Property<Boolean> getRequired();

  public abstract Property<String> getPlugin();

  public abstract Property<Integer> getInjectorsDefaultRequire();

  public abstract Property<String> getCompatibilityLevel();

  public abstract Property<String> getMinVersion();
}