package com.balugaq.jeg.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class provides some useful methods for reflection.
 *
 * @author Final_ROOT, balugaq
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "unused"})
@UtilityClass
public final class ReflectionUtil {
    public static boolean setValue(@NotNull Object object, @NotNull String field, Object value) {
        try {
            Field declaredField = object.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static <T> boolean setStaticValue(@NotNull Class<T> clazz, @NotNull String field, @Nullable Object value) {
        try {
            Field declaredField = clazz.getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static @Nullable Method getMethod(@NotNull Class<?> clazz, @NotNull String methodName) {
        while (clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static @Nullable Field getField(@NotNull Class<?> clazz, @NotNull String fieldName) {
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static <T, V> @Nullable T getProperty(@NotNull Object o, @NotNull Class<V> clazz, @NotNull String fieldName)
            throws IllegalAccessException {
        Field field = getField(clazz, fieldName);
        if (field != null) {
            boolean b = field.canAccess(o);
            field.setAccessible(true);
            Object result = field.get(o);
            field.setAccessible(b);
            return (T) result;
        }

        return null;
    }
}
