package cn.graht.test.designPatterns.factory.abstractFactory;

/**
 * @author GRAHT
 */


public interface PizzaKitchen {
    Pizza makePizza();
    Sauce makeSauce();
}
