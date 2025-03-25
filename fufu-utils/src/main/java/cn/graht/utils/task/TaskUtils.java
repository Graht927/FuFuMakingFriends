package cn.graht.utils.task;


import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author GRAHT
 */

public class TaskUtils {
    private static Map<String, ExecutorService> executorServices = new HashMap<>();
    public static ExecutorService init(String poolName,int poolSize){
            return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(),
                    new ThreadFactoryBuilder().setNamePrefix("Pool-").setDaemon(false).build(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
    }
    public static ExecutorService getOrInitExecuteService(String poolName,int poolSize){
        ExecutorService executorService = executorServices.get(poolName);
        if (null == executorService) {
            synchronized (TaskUtils.class){
                executorService = executorServices.get(poolName);
                if (null == executorService) {
                    executorService = init(poolName, poolSize);
                    executorServices.put(poolName, executorService);
                }
            }
        }
        return executorService;
    }
    public static void releaseExecutors(String poolName){
        ExecutorService executorService = executorServices.get(poolName);
        if (null != executorService) {
            executorService.shutdown();
        }
    }
}
