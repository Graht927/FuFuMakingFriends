package cn.graht.test.designPatterns.ChainOfResponsibility;

public class OrderProcessingException extends Exception {
    public OrderProcessingException(String message) {
        super(message);
    }
}