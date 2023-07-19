package com.mx.event;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventListenerManager {
    private static EventListenerManager instance = null;
    private ExecutorService threadPool = null;
    private final int THREAD_COUNT = 3;

    private EventListenerManager() {
        threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
        managerMap = new ConcurrentHashMap<>();
    }

    private Map<String, EventRunableManager> managerMap = null;

    public static EventListenerManager getInstance() {
        if (null == instance) {
            synchronized (EventListenerManager.class) {
                if (null == instance) {
                    instance = new EventListenerManager();
                }
            }
        }
        return instance;
    }

    public boolean addEventListener(String event,EventRunnable runnable){
        EventRunableManager runableManager = managerMap.get(event);
        if(runableManager == null){
            runableManager = new EventRunableManager();
        }
        boolean addResult = runableManager.add(runnable);
        if (addResult)
            managerMap.put(event,runableManager);
        return addResult;
    }

    public boolean removeEventListener(String event,EventRunnable runnable){
        EventRunableManager runableManager = managerMap.get(event);
        if(runableManager == null){
            return false;
        }
        boolean removeResult = runableManager.remove(runnable);
        if(runableManager.size() == 0){
            managerMap.remove(event);
        }
        return removeResult;
    }

    public void emit(String event){
        final EventRunableManager runableManager = managerMap.get(event);
        if(runableManager == null){
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                runableManager.run(null);
            }
        });
    }

    public void emit(String event, EventData data){
        final EventRunableManager runableManager = managerMap.get(event);
        if(runableManager == null){
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                runableManager.run(data);
            }
        });
    }

    interface EventRunnable<EventData>{
        void run(EventData data);
    }

    private class EventRunableManager {
        private List<EventRunnable> runnableList = null;
        private boolean run = false;

        private final int MAX_WAIT_TIME = 15 * 1000; // 最长等待时间

        EventRunableManager() {
            runnableList = new ArrayList<>();
        }

        public synchronized boolean add(EventRunnable run) {
            if(isRun()) {
                try {
                    wait(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                    return  false;
                }
            }
            runnableList.add(run);
            return true;
        }

        public synchronized boolean remove(EventRunnable run) {
            if(isRun()) {
                try {
                    wait(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                    return  false;
                }
            }
            runnableList.remove(run);
            return true;
        }

        private  boolean isRun() {
            return run;
        }

        private void setRun(boolean run) {
            this.run = run;
        }

        public int size(){
            return runnableList.size();
        }

        public synchronized boolean run(EventData data) {
            if(isRun()) {
                try {
                    wait(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                    return false;
                }
            }
            try {
                setRun(true);
                for (EventRunnable itemRun:runnableList){
                    try {
                        itemRun.run(data);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                setRun(false);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            } finally {
                notify();
            }
            return true;
        }
    }
}

