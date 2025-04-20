package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class CheesePizzaKitchen implements PizzaKitchen{
    @Override
    public Pizza makePizza() {
        return new CheesePizza();
    }

    @Override
    public Sauce makeSauce() {
        return new TomatoSauce();
    }
}
