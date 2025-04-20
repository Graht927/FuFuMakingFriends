package cn.graht.test.designPatterns.template.m1;

/**
 * @author GRAHT
 */

public class ArtificialOrderTemplate extends OrderTemplate{
    @Override
    void doCoffee() {
        System.out.println("人工制作");
    }
}
