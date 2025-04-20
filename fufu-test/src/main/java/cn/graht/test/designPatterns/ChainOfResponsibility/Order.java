package cn.graht.test.designPatterns.ChainOfResponsibility;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> items = new ArrayList<>();
    private Coupon coupon;
    
    // 构造方法
    public Order(String orderId, String userId) {
        this.orderId = orderId;
        this.userId = userId;
    }
    
    // 添加商品项
    public void addItem(String sku, int quantity) {
        items.add(new OrderItem(sku, quantity));
    }
    
    // getters 和 setters
    public List<OrderItem> getItems() { return items; }
    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }
    public String getUserId() { return userId; }
}