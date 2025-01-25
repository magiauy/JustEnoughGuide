package com.balugaq.jeg.api.objects;

import com.balugaq.jeg.utils.Debug;

public class Timer {
    public static long start;
    public final String name;
    public long starts;

    public Timer(String name) {
        this.name = name;
    }

    public static void start() {
        start = System.nanoTime();
    }

    public static long stop() {
        return System.nanoTime() - start;
    }

    public static void log() {
        Debug.debug("[Static] Time elapsed: " + stop() / 1_000_000.0F + "ms");
    }

    public void starts() {
        this.starts = System.nanoTime();
    }

    public long stops() {
        return System.nanoTime() - this.starts;
    }

    public void logs() {
        Debug.debug("[" + this.name + "] Time elapsed: " + stops() / 1_000_000.0F + "ms");
    }
}
