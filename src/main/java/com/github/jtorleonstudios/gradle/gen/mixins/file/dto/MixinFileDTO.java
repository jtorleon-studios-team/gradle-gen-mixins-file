package com.github.jtorleonstudios.gradle.gen.mixins.file.dto;

import com.github.jtorleonstudios.gradle.gen.mixins.file.MixinSideType;
import org.gradle.api.GradleException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record MixinFileDTO(
  MixinSideType type,
  String mixinName,
  String packageOwnerName
) {

  @Contract("_, _ -> new")
  public static @NotNull MixinFileDTO from(
    MixinSideType type,
    @NotNull ClassFileInfoDTO classFileInfoDTO
  ) {

    // ensure right type
    if (!classFileInfoDTO.is(type)) {
      throw new GradleException(
        "Invalid Mixin type: class " + classFileInfoDTO +
        " does not match expected type (" + type + ")."
      );
    }

    var packageSplit = new ArrayList<>(
      List.of(classFileInfoDTO
        .packageName()
        .split(type.getPackages())
      )
    );

    // v: com.github.foo.bar.mixins.client.foo
    // v.split(mixins.client) (if type = client)
    // 0: [com.github.foo.bar.]
    // 1: [.foo]

    // v: com.github.foo.bar.mixins.client.foo.mixins.client.bar
    // v.split(mixins.client)
    // 0: [com.github.foo.bar.]
    // 1: [.foo.]
    // 2: [.bar]

    // left: [com.github.foo.bar., ...] -> com.github.foo.bar.mixins
    var leftPart = packageSplit.remove(0) + MixinSideType.MIXINS_PACKAGE;

    // right: [.foo, .bar] -> client.foo.mixins.client.bar
    var rightPart = type.getSidePackage() + String.join(type.getPackages(), packageSplit);

    // right: [.client.foo] + fileName -> client.foo.FileName
    var mixinName = rightPart + "." + classFileInfoDTO.className();

    return new MixinFileDTO(
      type,
      mixinName,
      leftPart
    );
  }
}
