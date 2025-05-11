package com.balugaq.jeg.api.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class should be displayed in the cheat mode menu.
 * Priority lower than {@link NotDisplayInCheatMode}
 * <p>
 * Usage:
 * <p>
 * &#064;DisplayInCheatMode
 * public class MyGroup extends ItemGroup {
 * //...
 * }
 *
 * @author balugaq
 * @since 1.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DisplayInCheatMode {
}
