package cn.graht.test.designPatterns.mergeStrategyAndTemplate;

/**
 * @author GRAHT
 */
import cn.graht.test.designPatterns.mergeStrategyAndTemplate.make.ArtificialMake;
import cn.graht.test.designPatterns.mergeStrategyAndTemplate.make.MachineStrategy;
import cn.graht.test.designPatterns.mergeStrategyAndTemplate.make.MakeStrategyContext;
import cn.graht.test.designPatterns.mergeStrategyAndTemplate.pay.PayStrategyContext;
import cn.graht.test.designPatterns.mergeStrategyAndTemplate.pay.WeChatPayStrategy;

public class ShoppingTest extends doCoffee{
    public static void main(String[] args) {
        ShoppingTest shoppingTest = new ShoppingTest();
        shoppingTest.shopping();
    }

    @Override
    void pay() {
        PayStrategyContext payStrategyContext = new PayStrategyContext(new WeChatPayStrategy());
        payStrategyContext.pay();
    }

    @Override
    void make() {
        MakeStrategyContext makeStrategy = new MakeStrategyContext(new ArtificialMake());
        makeStrategy.make();
    }

}
