package cn.graht.test.designPatterns.singleton;

/**
 * @author GRAHT
 */
//懒汉式
public class M1Singleton {
    private static M1Singleton m1Singleton;

    private M1Singleton() {
    }

    public static M1Singleton getInstance() {
        if (m1Singleton == null) {
            m1Singleton = new M1Singleton();
        }
        return m1Singleton;
    }
}
