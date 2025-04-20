package cn.graht.test.designPatterns.mergeStrategyAndTemplate.pay;

/**
 * @author GRAHT
 */

public class PayStrategyContext {
    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    public void pay() {
        payStrategy.pay();
    }
}
