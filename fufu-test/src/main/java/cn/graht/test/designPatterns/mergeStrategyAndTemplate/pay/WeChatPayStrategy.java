package cn.graht.test.designPatterns.mergeStrategyAndTemplate.pay;

/**
 * @author GRAHT
 */

public class WeChatPayStrategy implements PayStrategy {
    @Override
    public void pay() {
        System.out.println("微信支付");
    }
}
