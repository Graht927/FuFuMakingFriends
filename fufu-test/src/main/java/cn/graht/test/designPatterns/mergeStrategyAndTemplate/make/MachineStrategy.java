package cn.graht.test.designPatterns.mergeStrategyAndTemplate.make;

/**
 * @author GRAHT
 */

public class MachineStrategy implements MakeStrategy{
    @Override
    public void make() {
        System.out.println("MachineStrategy");
    }
}
