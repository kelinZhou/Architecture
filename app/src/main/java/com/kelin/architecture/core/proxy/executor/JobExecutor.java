package com.kelin.architecture.core.proxy.executor;



import com.kelin.architecture.EnvConfig;
import com.kelin.architecture.tools.AppLayerErrorCatcher;
import com.kelin.architecture.util.LogHelper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Decorated {@link ThreadPoolExecutor}
 */
//@Singleton
public class JobExecutor implements ThreadExecutor {

    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 9;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 10;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final BlockingQueue<Runnable> workQueue;

    private ThreadPoolExecutor threadPoolExecutor;

    private final ThreadFactory threadFactory;


    public void shutdown() {
        threadPoolExecutor.shutdownNow();
    }

    private void reset() {
        threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30, KEEP_ALIVE_TIME_UNIT,
                this.workQueue, this.threadFactory);
    }

    //@Inject
    public JobExecutor() {
        this.workQueue = new SynchronousQueue<>();//new LinkedBlockingQueue<>();
        this.threadFactory = new JobThreadFactory();

        this.threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30, KEEP_ALIVE_TIME_UNIT,
                this.workQueue, this.threadFactory);
    }

    @Override
    public void execute(@NotNull Runnable runnable) {
        if (threadPoolExecutor.isShutdown()) {
            reset();
        }

        if (!threadPoolExecutor.isShutdown()) {
            this.threadPoolExecutor.execute(runnable);
            System.out.println("JobThreadFactory getTaskCount: " + threadPoolExecutor.getTaskCount() + " getCompletedTaskCount: " + threadPoolExecutor.getCompletedTaskCount() + " getPoolSize: " + threadPoolExecutor.getPoolSize());
        } else {
            System.err.println("JobThreadFactory(isShutdown) getTaskCount: " + threadPoolExecutor.getTaskCount() + " getCompletedTaskCount: " + threadPoolExecutor.getCompletedTaskCount() + " getPoolSize: " + threadPoolExecutor.getPoolSize());
        }

    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "android_";
        private int counter = 0;

        @Override
        public Thread newThread(@NotNull Runnable runnable) {
            Thread thread = new Thread(runnable, THREAD_NAME + counter++) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable e) {
                        LogHelper.Companion.getSystem().e("JobThreadFactory run error:" + e.getLocalizedMessage());
                        AppLayerErrorCatcher.INSTANCE.throwException(e);
                        if (EnvConfig.IS_DEBUG) {
                            throw e;
                        }
                    }
                }
            };

            thread.setDaemon(true);
            System.out.println("JobThreadFactory newThread: " + thread.getName());
            return thread;
        }
    }
}
