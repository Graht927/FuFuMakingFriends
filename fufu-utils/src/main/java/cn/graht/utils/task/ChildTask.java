package cn.graht.utils.task;

import cn.graht.utils.task.model.Cat;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author GRAHT
 */


public class ChildTask {
    private final int POOL_SIZE = 3;
    private final int SPILT_SIZE = 4;
    private String taskName;
    protected volatile boolean terminal = false;
    public ChildTask(String taskName){
        this.taskName = taskName;
    }
    public void doExecute(){
        int i = 0;
        while (true) {
            System.out.println(taskName + ":Cycle-" + i + "-Begin");
            List<Cat> cats = queryData();

            System.out.println(taskName + ":Cycle-" + i + "-End");
            if (terminal){
                break;
            }
            i++;
        }
        TaskUtils.releaseExecutors(taskName);
    }
    public void terminal(){
        terminal = true;
        System.out.println(taskName + "shut down ");
    }
    private void doProcessData(List<Cat> datas, CountDownLatch countDownLatch){
        try {
            for (Cat data : datas) {
                System.out.println(taskName + ":Process-" + JSONUtil.toJsonStr(data) + "ThreadCurrentName" + Thread.currentThread().getName());
                Thread.sleep(1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }
    private void taskExecute(List<Cat> sourceDatas) {
        if (CollectionUtil.isEmpty(sourceDatas)) return;
        List<List<Cat>> partition = Lists.partition(sourceDatas, SPILT_SIZE);
        CountDownLatch countDownLatch = new CountDownLatch(partition.size());
        partition.forEach(datas -> {
            ExecutorService orInitExecuteService = TaskUtils.getOrInitExecuteService(taskName, POOL_SIZE);
            orInitExecuteService.submit(() -> doProcessData(datas, countDownLatch));
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Cat> queryData(){
        List<Cat> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Cat cat = new Cat();
            cat.setName("cat-" + i);
            data.add(cat);
        }
        return data;
    }
}
