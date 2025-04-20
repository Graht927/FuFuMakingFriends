package cn.graht.test.designPatterns.mergeStrategyAndTemplate.pay;

/**
 * @author GRAHT
 */

public class AliPayStrategy implements PayStrategy{
    @Override
    public void pay() {
        System.out.println("支付宝支付");
    }
}
