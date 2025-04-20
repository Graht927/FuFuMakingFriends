package cn.graht.test.designPatterns.Decorator;

/**
 * @author GRAHT
 */

public class MilkDecorator extends CoffeeDecorator{
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getPrice() {
        return super.getPrice() + 0.5;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ",With Milk";
    }

}
