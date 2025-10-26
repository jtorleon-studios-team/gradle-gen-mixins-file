package com.github.jtorleonstudios.gradle.gen.mixins.file.dto;

import com.github.jtorleonstudios.gradle.gen.mixins.file.MixinSideType;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MixinFileDTOTest {

  @Test
  void test_from_with_valid_client_mixin_single_segment() {
    var classFileInfoDTO = new ClassFileInfoDTO(
      "MyClass",
      "com.github.foo.bar.mixins.client"
    );

    var result = MixinFileDTO.from(
      MixinSideType.CLIENT,
      classFileInfoDTO
    );

    assertEquals(MixinSideType.CLIENT, result.type());
    assertEquals("client.MyClass", result.mixinName());
    assertEquals("com.github.foo.bar.mixins", result.packageOwnerName());
  }

  @Test
  void test_from_with_valid_client_mixin_single_segment1() {
    var classFileInfoDTO = new ClassFileInfoDTO(
      "MyClass",
      "com.github.foo.bar.mixins.client.foo"
    );

    var result = MixinFileDTO.from(
      MixinSideType.CLIENT,
      classFileInfoDTO
    );

    assertEquals(MixinSideType.CLIENT, result.type());
    assertEquals("client.foo.MyClass", result.mixinName());
    assertEquals("com.github.foo.bar.mixins", result.packageOwnerName());
  }

  @Test
  void test_from_with_valid_client_mixin_multiple_segments() {
    var classFileInfoDTO = new ClassFileInfoDTO(
      "MyClass",
      "com.github.foo.bar.mixins.client.foo.mixins.client.bar"
    );

    var result = MixinFileDTO.from(
      MixinSideType.CLIENT,
      classFileInfoDTO
    );

    assertEquals(MixinSideType.CLIENT, result.type());
    assertEquals("client.foo.mixins.client.bar.MyClass", result.mixinName());
    assertEquals("com.github.foo.bar.mixins", result.packageOwnerName());
  }

  @Test
  void testFrom_withWrongType_throwsGradleException() {
    var dto = new ClassFileInfoDTO(
      "MyClass",
      "com.github.foo.bar.mixins.client.foo"
    );

    var ex = assertThrows(GradleException.class, () ->
      MixinFileDTO.from(MixinSideType.SERVER, dto)
    );

    assertTrue(ex.getMessage().contains("Invalid Mixin type"));
  }

}