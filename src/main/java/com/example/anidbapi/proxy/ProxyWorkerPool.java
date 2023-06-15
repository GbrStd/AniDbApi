package com.example.anidbapi.proxy;

public interface ProxyWorkerPool {

    <T> T execute(Proxy proxy, ProxyTask<T> proxyTask) throws Exception;

    /**
     * Execute a task using a random proxy from the pool of proxies
     *
     * @param proxyTask the task to be executed
     * @param <T>       the return type of the task
     * @return the result of the task
     * @throws Exception if the task fails
     */
    <T> T execute(ProxyTask<T> proxyTask) throws Exception;

    <T> T executeUntilSuccess(Proxy proxy, ProxyTask<T> proxyTask) throws ProxyWorkerException;

    /**
     * Execute the task until it succeeds or until the max tries is reached which is {@link ProxyWorkerPoolImpl#MAX_TRIES_UNTIL_STOP}
     *
     * @param proxyTask the task to be executed
     * @param <T>       the return type of the task
     * @return the result of the task
     * @throws ProxyWorkerException if the task fails
     */
    <T> T executeUntilSuccess(ProxyTask<T> proxyTask) throws ProxyWorkerException;

    void addProxy(Proxy proxy);

    void removeProxy(Proxy proxy);

    Proxy getProxy() throws ProxyWorkerException;

}
