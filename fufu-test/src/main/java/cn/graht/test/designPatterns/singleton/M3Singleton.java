package cn.graht.test.designPatterns.singleton;

/**
 * @author GRAHT
 */
//双锁
public class M3Singleton {
    private static volatile M3Singleton instance;
    private M3Singleton(){}
    public static M3Singleton getInstance(){
        if (instance == null){
            synchronized (M3Singleton.class){
                if (instance == null){
                    instance = new M3Singleton();
                }
            }
        }
        return instance;
    }
}
