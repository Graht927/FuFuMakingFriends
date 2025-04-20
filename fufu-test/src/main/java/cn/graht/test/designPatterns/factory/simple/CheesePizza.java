package cn.graht.test.designPatterns.factory.simple;

/**
 * @author GRAHT
 */

public class CheesePizza implements Pizza{
    @Override
    public void prepare() {
        System.out.println("准备奶酪披萨");
    }
}
