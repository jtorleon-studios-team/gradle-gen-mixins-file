package com.github.jtorleonstudios.gradle.gen.mixins.file;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * MixinSideType
 * <table>
 *   <thead>
 *     <tr>
 *       <th>Type</th>
 *       <th>Package</th>
 *       <th>Package Owner</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>common</td>
 *       <td>com.foo.mixins.common</td>
 *       <td>com.foo.mixins</td>
 *     </tr>
 *     <tr>
 *       <td>client</td>
 *       <td>com.foo.mixins.client</td>
 *       <td>com.foo.mixins</td>
 *     </tr>
 *     <tr>
 *       <td>server</td>
 *       <td>com.foo.mixins.server</td>
 *       <td>com.foo.mixins</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum MixinSideType {

  /**
   * Common Mixins
   * <hr>
   * Example:
   * <br>
   * {@code com.foo.mixins.common.BarMixin.java}
   * <br>
   * {@code com.foo.mixins.common.foo.FooMixin.java}
   */
  COMMON("common"),

  /**
   * Client Mixins
   * <hr>
   * Example:
   * <br>
   * {@code com.foo.mixins.client.BarMixin.java}
   * <br>
   * {@code com.foo.mixins.client.foo.FooMixin.java}
   */
  CLIENT("client"),

  /**
   * Server Mixins
   * <hr>
   * Example:
   * <br>
   * {@code com.foo.mixins.server.BarMixin.java}
   * <br>
   * {@code com.foo.mixins.server.foo.FooMixin.java}
   */
  SERVER("server");

  public final static String MIXINS_PACKAGE = "mixins";
  private final String sidePackage;

  @Contract(pure = true)
  public @NotNull String getPackages() {
    return MixinSideType.MIXINS_PACKAGE + "." + sidePackage;
  }

}
