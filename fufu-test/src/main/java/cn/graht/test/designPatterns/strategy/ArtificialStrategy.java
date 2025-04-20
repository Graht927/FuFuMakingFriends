package cn.graht.test.designPatterns.strategy;

/**
 * @author GRAHT
 */

public class ArtificialStrategy implements coffeeStrategy{

    @Override
    public void doCoffee() {
        System.out.println("人工制作");
    }
}
