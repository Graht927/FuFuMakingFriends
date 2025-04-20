package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class GarlicSauce implements Sauce{
    @Override
    public void addSauce() {
        System.out.println("蒜香酱");
    }
}
