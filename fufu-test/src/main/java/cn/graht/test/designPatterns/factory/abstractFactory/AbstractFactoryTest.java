package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class AbstractFactoryTest {
    public static void main(String[] args) {
        PizzaKitchen pizzaKitchen = new CheesePizzaKitchen();
        Pizza cheesePizza = pizzaKitchen.makePizza();
        Sauce sauce = pizzaKitchen.makeSauce();
        cheesePizza.prepare();
        sauce.addSauce();
    }
}
