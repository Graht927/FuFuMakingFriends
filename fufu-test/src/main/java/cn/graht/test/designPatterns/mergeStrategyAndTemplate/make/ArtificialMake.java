package cn.graht.test.designPatterns.mergeStrategyAndTemplate.make;

/**
 * @author GRAHT
 */

public class ArtificialMake implements MakeStrategy{
    @Override
    public void make() {
        System.out.println("ArtificialMake");
    }
}
