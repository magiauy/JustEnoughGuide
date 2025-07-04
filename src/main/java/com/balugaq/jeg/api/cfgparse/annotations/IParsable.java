package com.balugaq.jeg.api.cfgparse.annotations;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings("unused")
public interface IParsable {
    @SneakyThrows
    static String @NotNull [] fieldNames(@NotNull Class<? extends IParsable> clazz) {
        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No no-arg constructor found in " + clazz.getName(), e);
        }

        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Key.class)).map(Field::getName).toArray(String[]::new);
    }
}
