package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */

public class VeggiePizzaKitchen implements PizzaKitchen{
    @Override
    public Pizza makePizza() {
        return new VeggiePizza();
    }

    @Override
    public Sauce makeSauce() {
        return new GarlicSauce();
    }
}
