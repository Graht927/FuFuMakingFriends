package cn.graht.test.designPatterns.ChainOfResponsibility;

public class PaymentHandler extends OrderHandler {
    @Override
    public void handle(Order order) throws OrderProcessingException {
        System.out.println("[Payment] 处理支付...");
        
        // 模拟支付处理
        boolean paymentSuccess = processPayment(order);
        if (!paymentSuccess) {
            throw new OrderProcessingException("支付失败");
        }
        
        passToNext(order);
    }
    
    private boolean processPayment(Order order) {
        // 模拟80%成功率
        return Math.random() > 0.2;
    }
}