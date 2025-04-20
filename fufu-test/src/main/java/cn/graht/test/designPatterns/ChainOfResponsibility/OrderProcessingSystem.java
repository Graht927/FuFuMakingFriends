package cn.graht.test.designPatterns.ChainOfResponsibility;

public class OrderProcessingSystem {
    public static void main(String[] args) {
        // 1. 构建责任链
        OrderHandler handlerChain = new InventoryCheckHandler();
        handlerChain
            .setNext(new CouponCheckHandler())
            .setNext(new RiskCheckHandler())
            .setNext(new PaymentHandler())
            .setNext(new ShippingHandler());
        // 2. 创建测试订单
        Order order = new Order("ORDER_123", "USER100");
        order.addItem("SKU001", 2);  // 添加商品
        order.addItem("SKU002", 1);
        order.setCoupon(new Coupon("SUMMER2023", true));  // 设置有效优惠券
        
        // 3. 处理订单
        try {
            handlerChain.handle(order);
        } catch (OrderProcessingException e) {
            System.err.println("订单处理失败: " + e.getMessage());
        }
    }
}