package cn.graht.test.designPatterns.Decorator;

/**
 * @author GRAHT
 */

public class SimpleCoffee implements Coffee{
    @Override
    public double getPrice() {
        return 2.0;
    }

    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
}
