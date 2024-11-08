package com.balugaq.jeg.deprecated.proxy;

import com.balugaq.jeg.api.interfaces.BookmarkRelocation;

import java.lang.reflect.Proxy;

@SuppressWarnings("unused")
@Deprecated
public class ProxyFactory {
    public static BookmarkRelocation createProxy(Object targetInstance) {
        ClassLoader classLoader = targetInstance.getClass().getClassLoader();
        Class<?>[] interfaces = new Class<?>[]{BookmarkRelocation.class};
        BookmarkRelocationHandler handler = new BookmarkRelocationHandler(targetInstance);

        return (BookmarkRelocation) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
}
