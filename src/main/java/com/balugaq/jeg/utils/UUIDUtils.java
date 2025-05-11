package com.balugaq.jeg.utils;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Ddggdd135
 * @since 1.4
 */
public class UUIDUtils {
    public static byte[] toByteArray(@NotNull UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        byte[] bytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (mostSigBits >>> 8 * (7 - i));
            bytes[8 + i] = (byte) (leastSigBits >>> 8 * (7 - i));
        }
        return bytes;
    }
}