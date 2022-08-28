package com.emcikem.kcpbase.threadpool;

/**
 * @author Emcikem
 * @create 2022/8/28
 * @desc
 */
public interface IMessageExecutorPool {

    /**
     * 从线程池中按算法获得一个线程对象
     * @return
     */
    IMessageExecutor getIMessageExecutor();

    void stop();
}
