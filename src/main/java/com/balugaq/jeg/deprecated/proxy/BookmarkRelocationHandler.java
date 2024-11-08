package com.balugaq.jeg.deprecated.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
@Deprecated
public class BookmarkRelocationHandler implements InvocationHandler {
    private final Object targetInstance;

    public BookmarkRelocationHandler(Object targetInstance) {
        this.targetInstance = targetInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        switch (method.getName()) {
            case "getBackButton" -> {
                return 0;
            }
            case "getSearchButton" -> {
                return 0;
            }
            case "getPreviousButton" -> {
                return 0;
            }
            case "getNextButton" -> {
                return 0;
            }
            case "getBookMark" -> {
                return 0;
            }
            case "getItemMark" -> {
                return 0;
            }
            case "getBorder" -> {
                return new int[0];
            }
            case "getMainContents" -> {
                return new int[0];
            }
            default -> {
                return null;
            }
        }
    }
}
