package cn.graht.test.designPatterns.Decorator;

/**
 * @author GRAHT
 */

public class SugarDecorator extends CoffeeDecorator{
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 0.2;
    }

    @Override
    public String getDescription() {
        return super.getDescription() +", with sugar";
    }

}
