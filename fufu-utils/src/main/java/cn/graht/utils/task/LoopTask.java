package cn.graht.utils.task;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author GRAHT
 */

public class LoopTask {
    private List<ChildTask> childTasks;
    public void initLoopTask(){
        childTasks = new ArrayList<>();
        childTasks.add(new ChildTask("task-1"));
        childTasks.add(new ChildTask("task-2"));
        childTasks.forEach( childTask -> ((Runnable) childTask::doExecute).run());
    }
    public void shutdownLoopTask(){
        if (CollectionUtil.isNotEmpty(childTasks)){
            childTasks.forEach(ChildTask::terminal);
        }
    }
    public static void main(String[] args) {
        LoopTask loopTask = new LoopTask();
        loopTask.initLoopTask();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loopTask.shutdownLoopTask();
    }
}
