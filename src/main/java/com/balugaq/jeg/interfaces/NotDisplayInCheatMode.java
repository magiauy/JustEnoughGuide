package com.balugaq.jeg.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotDisplayInCheatMode {
}

/*
 * Usage:
 * <p>
 * @NotDisplayInCheatMode
 * public class MyGroup extends ItemGroup {
 *     //...
 * }
 */
