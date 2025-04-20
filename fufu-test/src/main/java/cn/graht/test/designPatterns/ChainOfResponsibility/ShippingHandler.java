package cn.graht.test.designPatterns.ChainOfResponsibility;

public class ShippingHandler extends OrderHandler {
    @Override
    public void handle(Order order) throws OrderProcessingException {
        System.out.println("[Shipping] 安排物流...");
        
        // 模拟物流调度
        String trackingNumber = generateTrackingNumber();
        System.out.println("已生成物流单号: " + trackingNumber);
        System.out.println("订单处理完成！");
    }
    
    private String generateTrackingNumber() {
        return "TN" + System.currentTimeMillis();
    }
}