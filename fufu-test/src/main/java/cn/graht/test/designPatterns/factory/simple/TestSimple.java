package cn.graht.test.designPatterns.factory.simple;

/**
 * @author GRAHT
 */

public class TestSimple {
    public static void main(String[] args) {
        PizzaFactory pizzaFactory = new NYPizzaStore();
        pizzaFactory.orderPizza("cheese");
    }
}
