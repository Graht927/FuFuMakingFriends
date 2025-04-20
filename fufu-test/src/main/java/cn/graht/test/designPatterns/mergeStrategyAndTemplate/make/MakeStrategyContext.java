package cn.graht.test.designPatterns.mergeStrategyAndTemplate.make;

import cn.graht.test.designPatterns.mergeStrategyAndTemplate.doCoffee;

/**
 * @author GRAHT
 */

public class MakeStrategyContext {
    private MakeStrategy makeStrategy;

    public MakeStrategyContext(MakeStrategy makeStrategy) {
        this.makeStrategy = makeStrategy;
    }
    public void make() {
        makeStrategy.make();
    }
}
