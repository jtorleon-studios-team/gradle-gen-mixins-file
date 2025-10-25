package com.github.jtorleonstudios.gradle.gen.mixins.file.dto;

import com.github.jtorleonstudios.gradle.gen.mixins.file.GenMixinsFileExt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MixinsJsonFileDTO(
  String fileName,
  boolean fieldRequired,
  String fieldMinVersion,
  String fieldPackage,
  String compatibilityLevel,
  String refmap,
  String plugin,
  List<String> mixins,
  List<String> client,
  List<String> server,
  InjectorsDto injectors
) {

  public record InjectorsDto(
    int defaultRequire
  ) {
  }

  @Contract("_, _, _, _, _ -> new")
  public static @NotNull MixinsJsonFileDTO from(
    String packageOwner,
    @NotNull GenMixinsFileExt ext,
    @NotNull List<MixinFileDTO> commonMixins,
    @NotNull List<MixinFileDTO> clientMixins,
    @NotNull List<MixinFileDTO> serverMixins
  ) {
    return new MixinsJsonFileDTO(

      ext.getFileName().get().trim() + ".mixins.json",

      ext.getRequired().get(),

      ext.getMinVersion().get().trim(),

      packageOwner,

      ext.getCompatibilityLevel().get().trim(),

      ext.getFileName().get().trim() + ".refmap.json",

      ext.getPlugin().get().trim(),

      commonMixins.stream().map(MixinFileDTO::mixinName).toList(),

      clientMixins.stream().map(MixinFileDTO::mixinName).toList(),

      serverMixins.stream().map(MixinFileDTO::mixinName).toList(),

      new InjectorsDto(ext.getInjectorsDefaultRequire().get())

    );
  }

  public @NotNull String toJson() {
    var sb = new StringBuilder();
    var br = System.lineSeparator();
    sb.append("{").append(br);
    sb.append(" \"required\":").append(this.fieldRequired).append(",").append(br);
    sb.append(" \"minVersion\": \"").append(this.fieldMinVersion).append("\",").append(br);
    sb.append(" \"compatibilityLevel\": \"").append(this.compatibilityLevel).append("\",").append(br);
    sb.append(" \"package\": \"").append(this.fieldPackage).append("\",").append(br);
    sb.append(" \"refmap\": \"").append(this.refmap).append("\",").append(br);

    // plugin (optional)
    if (!this.plugin.isEmpty()) {
      sb.append(" \"plugin\": \"").append(this.plugin).append("\",").append(br);
    }

    // Mixins
    sb.append(" \"mixins\": [").append(br);
    for (var v : this.mixins) {
      sb.append(" \"").append(v).append("\"").append(br);
    }
    sb.append(" ],").append(br);

    // Client
    sb.append(" \"client\": [").append(br);
    for (var v : this.client) {
      sb.append(" \"").append(v).append("\"").append(br);
    }
    sb.append(" ],").append(br);

    // Server
    sb.append(" \"server\": [").append(br);
    for (var v : this.server) {
      sb.append(" \"").append(v).append("\"").append(br);
    }
    sb.append(" ],").append(br);

    // Injector
    sb.append(" \"injectors\": {").append(br);
    sb.append(" \"defaultRequire\": ").append(this.injectors.defaultRequire).append(br);
    sb.append(" }").append(br);
    sb.append("}");

    return sb.toString();
  }

}
