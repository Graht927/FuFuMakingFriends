package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class VeggiePizza implements Pizza{
    @Override
    public void prepare() {
        System.out.println("准备蔬菜披萨 +");
    }
}
