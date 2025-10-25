package com.github.jtorleonstudios.gradle.gen.mixins.file.dto;

import com.github.jtorleonstudios.gradle.gen.mixins.file.MixinSideType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class ClassInfoDTOTest {

  @Test
  void test_dto() {
    var relativePath = Path.of("com/github/hello/world/JavaClass.java");
    var result = ClassFileInfoDTO.from(relativePath);

    Assertions.assertEquals("JavaClass", result.className());
    Assertions.assertEquals("com.github.hello.world", result.packageName());

    Assertions.assertFalse(result.is(MixinSideType.CLIENT));
    Assertions.assertFalse(result.is(MixinSideType.COMMON));
    Assertions.assertFalse(result.is(MixinSideType.SERVER));
  }

  @Test
  void test_is_mixin_side_client() {
    Assertions.assertTrue(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/client/JavaClass.java"))
        .is(MixinSideType.CLIENT)
    );

    Assertions.assertTrue(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/client/extra/JavaClass.java"))
        .is(MixinSideType.CLIENT)
    );

    Assertions.assertFalse(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/JavaClass.java"))
        .is(MixinSideType.CLIENT)
    );
  }

  @Test
  void test_is_mixin_side_server() {
    Assertions.assertTrue(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/server/JavaClass.java"))
        .is(MixinSideType.SERVER)
    );

    Assertions.assertTrue(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/server/extra/JavaClass.java"))
        .is(MixinSideType.SERVER)
    );

    Assertions.assertFalse(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/JavaClass.java"))
        .is(MixinSideType.SERVER)
    );
  }

  @Test
  void test_is_mixin_side_common() {
    Assertions.assertTrue(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/common/JavaClass.java"))
        .is(MixinSideType.COMMON)
    );

    Assertions.assertTrue(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/common/extra/JavaClass.java"))
        .is(MixinSideType.COMMON)
    );

    Assertions.assertFalse(
      ClassFileInfoDTO
        .from(Path.of("com/github/hello/world/mixins/JavaClass.java"))
        .is(MixinSideType.COMMON)
    );
  }
}