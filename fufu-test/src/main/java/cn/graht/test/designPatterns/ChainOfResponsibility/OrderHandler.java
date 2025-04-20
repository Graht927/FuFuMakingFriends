package cn.graht.test.designPatterns.ChainOfResponsibility;

public abstract class OrderHandler {
    protected OrderHandler next;
    
    // 设置下一个处理器（返回next便于链式调用）
    public OrderHandler setNext(OrderHandler next) {
        this.next = next;
        return next;
    }
    
    // 处理订单的抽象方法
    public abstract void handle(Order order) throws OrderProcessingException;
    
    // 传递给下一个处理器
    protected void passToNext(Order order) throws OrderProcessingException {
        if (next != null) {
            next.handle(order);
        }
    }
}