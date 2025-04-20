package cn.graht.test.designPatterns.ChainOfResponsibility;

public class CouponCheckHandler extends OrderHandler {
    @Override
    public void handle(Order order) throws OrderProcessingException {
        System.out.println("[CouponCheck] 校验优惠券...");
        
        Coupon coupon = order.getCoupon();
        if (coupon != null && !coupon.isValid()) {
            throw new OrderProcessingException("优惠券无效: " + coupon.getCode());
        }
        
        passToNext(order);
    }
}