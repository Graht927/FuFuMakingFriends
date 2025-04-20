package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class TomatoSauce implements Sauce{
    @Override
    public void addSauce() {
        System.out.println("番茄酱");
    }
}
