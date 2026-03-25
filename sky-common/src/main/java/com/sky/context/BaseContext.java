package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 像当前线程存入当前用户id
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }


    /**
     * 像当前线程获取用户id
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 移除当前线程中的数据
     * 防止线程复用时造成脏数据污染
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
