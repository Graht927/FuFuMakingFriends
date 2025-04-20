package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class CheesePizza implements Pizza{
    @Override
    public void prepare() {
        System.out.println("准备制作奶酪披萨 + ");
    }
}
