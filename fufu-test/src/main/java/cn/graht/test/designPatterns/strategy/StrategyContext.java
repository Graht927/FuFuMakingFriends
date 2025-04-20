package cn.graht.test.designPatterns.strategy;

/**
 * @author GRAHT
 */

public class StrategyContext {
    private coffeeStrategy strategy;
    public StrategyContext(coffeeStrategy strategy) {
        this.strategy = strategy;
    }
    public void doCoffee() {
        strategy.doCoffee();
    }
}
