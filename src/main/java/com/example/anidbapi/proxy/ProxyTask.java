package com.example.anidbapi.proxy;

public interface ProxyTask<V> {

    V execute(Proxy proxy) throws Exception;

}
