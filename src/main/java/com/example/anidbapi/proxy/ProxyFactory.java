package com.example.anidbapi.proxy;

public class ProxyFactory {

    public static Proxy createProxy(String proxy, ProxyType type) {
        String[] proxySplit = proxy.split(":");
        String host = proxySplit[0];
        int port = Integer.parseInt(proxySplit[1]);
        return new Proxy(host, port, type);
    }

    public static Proxy createProxy(String proxy, ProxyType type, String username, String password) {
        String[] proxySplit = proxy.split(":");
        String host = proxySplit[0];
        int port = Integer.parseInt(proxySplit[1]);
        return new Proxy(host, port, username, password, type);
    }

}
