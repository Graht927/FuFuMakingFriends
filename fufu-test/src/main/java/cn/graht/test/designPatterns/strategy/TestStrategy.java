package cn.graht.test.designPatterns.strategy;

/**
 * @author GRAHT
 */

public class TestStrategy {
    public static void main(String[] args) {
        StrategyContext strategyContext = new StrategyContext(new ArtificialStrategy());
        strategyContext.doCoffee();
        strategyContext = new StrategyContext(new MachineStrategy());
        strategyContext.doCoffee();

    }
}
