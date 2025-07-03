package com.balugaq.jeg.api.cfgparse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
}
