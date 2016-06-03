package com.shorty.core.event;

import android.os.Looper;

import com.shorty.core.annotation.Subscribe;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by yue.huang on 2016/6/1.
 */
public class Subscriber {
    private EventListener listener;
    private int threadType;

     public Subscriber(EventListener listener, int threadType){
        this.listener = listener;
         this.threadType = threadType;
     }

    /**
     * send event to listener with setting thread's type
     * @param bus
     * @param event
     */
    public void dispatchEvent(EventManager bus, final Object event){
        switch (threadType){
            case Subscribe.DEFAULT:
                listener.onEvent(event);
                break;
            case Subscribe.MAIN_THREAD:
                if(Looper.myLooper() == Looper.getMainLooper()){
                    listener.onEvent(event);
                } else {
                    bus.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onEvent(event);
                        }
                    });
                }
                break;
            case Subscribe.BACKGROUND_THREAD:
                if(Looper.myLooper() == Looper.getMainLooper()){
                    bus.getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.onEvent(event);
                        }
                    });
                } else {
                    listener.onEvent(event);
                }
                break;
            case Subscribe.ASYNC_THREAD:
                bus.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.onEvent(event);
                    }
                });
                break;
        }
    }
}