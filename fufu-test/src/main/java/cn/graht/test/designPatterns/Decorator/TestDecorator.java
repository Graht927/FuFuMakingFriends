package cn.graht.test.designPatterns.Decorator;

/**
 * @author GRAHT
 */

public class TestDecorator {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + ":" + coffee.getPrice());
        coffee  = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + ":" + coffee.getPrice());
        coffee  = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + ":" + coffee.getPrice());
    }
}
