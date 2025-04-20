package cn.graht.test.designPatterns.ChainOfResponsibility;

import java.util.Set;

public class RiskCheckHandler extends OrderHandler {
    @Override
    public void handle(Order order) throws OrderProcessingException {
        System.out.println("[RiskCheck] 执行风控检查...");
        
        // 模拟风控检查
        if (isHighRiskUser(order.getUserId())) {
            throw new OrderProcessingException("风控校验不通过");
        }
        
        passToNext(order);
    }
    
    private boolean isHighRiskUser(String userId) {
        // 模拟高风险用户列表
        Set<String> highRiskUsers = Set.of("USER666", "USER999");
        return highRiskUsers.contains(userId);
    }
}