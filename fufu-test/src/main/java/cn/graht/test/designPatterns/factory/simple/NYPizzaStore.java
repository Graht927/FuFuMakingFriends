package cn.graht.test.designPatterns.factory.simple;

/**
 * @author GRAHT
 */

public class NYPizzaStore extends PizzaFactory{
    @Override
    Pizza createPizza(String type) {
        return switch (type){
            case "cheese" -> new CheesePizza();
            case "veggie" -> new VeggiePizza();
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }
}
