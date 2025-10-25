package com.github.jtorleonstudios.gradle.gen.mixins.file.dto;

import com.github.jtorleonstudios.gradle.gen.mixins.file.MixinSideType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public record ClassFileInfoDTO(
  String className,
  String packageName
) {

  @Contract("_ -> new")
  public static @NotNull ClassFileInfoDTO from(
    @NotNull Path relativePath
  ) {
    return new ClassFileInfoDTO(
      relativePath
        .getFileName()
        .toString()
        .replace(".java", ""),
      relativePath
        .getParent()
        .toString()
        .replace(File.separator, ".")
    );
  }

  public boolean is(
    @NotNull MixinSideType mixinSideType
  ) {
    return this.packageName().contains(mixinSideType.getPackages());
  }

}
