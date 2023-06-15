package com.example.anidbapi.proxy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Proxy {

    private final String ip;

    private final int port;

    private final String username;

    private final String password;

    private final ProxyType type;

    private final boolean permanent = true; //  TODO

    private long lastUsed;

    private int usage;

    public Proxy(String ip, int port, String username, String password, ProxyType type) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.type = type;
        this.lastUsed = System.currentTimeMillis();
        this.usage = 0;
    }

    public Proxy(String ip, int port, ProxyType type) {
        this.ip = ip;
        this.port = port;
        this.username = null;
        this.password = null;
        this.type = type;
        this.lastUsed = System.currentTimeMillis();
        this.usage = 0;
    }

}
