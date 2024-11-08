package com.balugaq.jeg.proxy;

import com.balugaq.jeg.interfaces.BookmarkRelocation;

import java.lang.reflect.Proxy;

@Deprecated
public class ProxyFactory {
    public static BookmarkRelocation createProxy(Object targetInstance) {
        ClassLoader classLoader = targetInstance.getClass().getClassLoader();
        Class<?>[] interfaces = new Class<?>[]{BookmarkRelocation.class};
        BookmarkRelocationHandler handler = new BookmarkRelocationHandler(targetInstance);

        return (BookmarkRelocation) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
}
