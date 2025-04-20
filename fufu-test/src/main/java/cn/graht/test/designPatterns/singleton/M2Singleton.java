package cn.graht.test.designPatterns.singleton;

/**
 * @author GRAHT
 */
//饿汉式
public class M2Singleton {
    private final static M2Singleton instance = new M2Singleton();
}
