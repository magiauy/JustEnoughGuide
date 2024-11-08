package com.balugaq.jeg.api.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotDisplayInSurvivalMode {
}

/*
 * Usage:
 * <p>
 * @NotDisplayInSurvivalMode
 * public class MyGroup extends ItemGroup {
 *     //...
 * }
 */
