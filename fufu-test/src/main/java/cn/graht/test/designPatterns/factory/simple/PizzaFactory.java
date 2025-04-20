package cn.graht.test.designPatterns.factory.simple;

/**
 * @author GRAHT
 */


public abstract class PizzaFactory {
    abstract Pizza createPizza(String type);
    void orderPizza(String type) {
        Pizza pizza = createPizza(type);
        pizza.prepare();
    }
}
