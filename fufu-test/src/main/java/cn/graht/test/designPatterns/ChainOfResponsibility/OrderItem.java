package cn.graht.test.designPatterns.ChainOfResponsibility;

public class OrderItem {
    private String sku;
    private int quantity;
    
    public OrderItem(String sku, int quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }
    
    // getters
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
}