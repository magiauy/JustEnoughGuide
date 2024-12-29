package com.balugaq.jeg.api.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a class should be displayed in the survival mode menu.
 * Priority lower than {@link NotDisplayInSurvivalMode}
 * <p>
 * Usage:
 * <p>
 * &#064;DisplayInSurvivalMode
 * public class MyGroup extends ItemGroup {
 * //...
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DisplayInSurvivalMode {}
