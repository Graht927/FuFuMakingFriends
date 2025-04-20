package cn.graht.test.designPatterns.template.m1;


/**
 * @author GRAHT
 */

public class MachineOrderTemplate extends OrderTemplate {
    @Override
    void doCoffee() {
        System.out.println("机器制作");
    }
}
