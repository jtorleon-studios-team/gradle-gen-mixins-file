package com.github.jtorleonstudios.gradle.gen.mixins.file.exceptions;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class MultipleMixinPackagesException extends GradleException {

  public MultipleMixinPackagesException(
    HashSet<String> multiplePackage
  ) {
    super(toMessage(multiplePackage));
  }

  private static @NotNull String toMessage(
    @NotNull HashSet<String> multiplePackage
  ) {
    var sb = new StringBuilder();
    var br = System.lineSeparator();

    sb.append("Multiple mixins packages detected:").append(br);

    for (var packageOwner : multiplePackage) {
      sb.append(" - ").append(packageOwner).append(br);
    }

    sb.append("Please ensure a single, consistent package structure for your mixins.")
      .append(br)
      .append("---------------------------------------------------------------------")
      .append(br)
      .append("Theoretical Example:")
      .append(br)
      .append(" Incorrect:")
      .append(br)
      .append("  - com.github.foo.mixins.client.FooMixin.java -> [com.github.foo.mixins]")
      .append(br)
      .append("  - com.github.bar.mixins.client.BarMixin.java -> [com.github.bar.mixins]")
      .append(br)
      .append(" Correct:")
      .append(br)
      .append("  - com.github.foo.mixins.client.FooMixin.java -> [com.github.foo.mixins]")
      .append(br)
      .append("  - com.github.foo.mixins.client.BarMixin.java -> [com.github.foo.mixins]");

    return sb.toString();
  }

}
