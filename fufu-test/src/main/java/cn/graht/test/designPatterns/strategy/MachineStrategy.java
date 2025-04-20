package cn.graht.test.designPatterns.strategy;

/**
 * @author GRAHT
 */

public class MachineStrategy implements coffeeStrategy{
    @Override
    public void doCoffee() {
        System.out.println("机器做咖啡");
    }
}
