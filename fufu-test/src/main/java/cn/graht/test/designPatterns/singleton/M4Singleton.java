package cn.graht.test.designPatterns.singleton;

/**
 * @author GRAHT
 */
//静态内部类
public class M4Singleton {
    private static class SingletonHolder {
        private static final M4Singleton INSTANCE = new M4Singleton();
    }

    public static M4Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
