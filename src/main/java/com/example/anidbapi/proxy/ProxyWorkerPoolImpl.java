package com.example.anidbapi.proxy;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProxyWorkerPoolImpl implements ProxyWorkerPool {

    public static final int MAX_TRIES_UNTIL_STOP = 10;
    private static final int REUSE_SAME_PROXY_AMOUNT_OF_TIMES = 3; // 1 = use proxy only once
    private final ArrayList<Proxy> proxies = new ArrayList<>();
    private final AtomicInteger currentProxyUsage = new AtomicInteger(0); // hold the amount of times the proxy was used
    private final AtomicInteger currentProxyIndex = new AtomicInteger(0); // hold the index of the current proxy

    public ProxyWorkerPoolImpl(List<Proxy> proxies) {
        this.proxies.addAll(proxies);
    }

    public ProxyWorkerPoolImpl() {
    }

    @Override
    public <T> T execute(Proxy proxy, ProxyTask<T> proxyTask) throws Exception {
        final T t;
        try {
            t = proxyTask.execute(proxy);
        } catch (Exception e) {
            if (!proxy.isPermanent())
                removeProxy(proxy);
            throw e;
        }
        return t;
    }

    @Override
    public <T> T execute(ProxyTask<T> proxyTask) throws Exception {
        if (noProxies())
            throw new ProxyWorkerException("No proxy available");
        return execute(getProxy(), proxyTask);
    }

    @Override
    public <T> T executeUntilSuccess(Proxy proxy, ProxyTask<T> proxyTask) throws ProxyWorkerException {
        int tries = 0;
        while (tries < MAX_TRIES_UNTIL_STOP) {
            try {
                final T t = execute(proxy, proxyTask);
                if (t == null)
                    throw new ProxyWorkerException("Failed to execute the task");
                return t;
            } catch (Exception e) {
                if (noProxies())
                    throw new ProxyWorkerException(e.getMessage(), e);
                log.warn("Failed to execute the task with proxy: {}, {}, current tries {}", proxy, e.getMessage(), tries);
                tries++;
            }
        }
        throw new ProxyWorkerException("Failed to execute the task after " + MAX_TRIES_UNTIL_STOP + " tries");
    }

    @Override
    public <T> T executeUntilSuccess(ProxyTask<T> proxyTask) throws ProxyWorkerException {
        return executeUntilSuccess(getProxy(), proxyTask);
    }

    @Override
    public void addProxy(Proxy proxy) {
        this.proxies.add(proxy);
    }

    @Override
    public void removeProxy(Proxy proxy) {
        this.proxies.remove(proxy);
    }

    @Override
    public synchronized Proxy getProxy() throws ProxyWorkerException {
        if (noProxies())
            throw new ProxyWorkerException("No proxy available");
        int index = (int) (Math.random() * proxies.size());
        if (currentProxyUsage.incrementAndGet() >= REUSE_SAME_PROXY_AMOUNT_OF_TIMES) {
            currentProxyIndex.set(index);
            currentProxyUsage.set(0);
        }
        return proxies.get(currentProxyIndex.get());
    }

    public boolean noProxies() {
        return proxies.isEmpty();
    }

}
