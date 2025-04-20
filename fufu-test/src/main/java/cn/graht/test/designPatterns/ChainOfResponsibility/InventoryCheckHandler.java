package cn.graht.test.designPatterns.ChainOfResponsibility;

import java.util.Map;

public class InventoryCheckHandler extends OrderHandler {
    @Override
    public void handle(Order order) throws OrderProcessingException {
        System.out.println("[InventoryCheck] 检查商品库存...");
        
        // 模拟库存检查（实际应该查数据库）
        boolean allInStock = order.getItems().stream()
            .allMatch(item -> checkStock(item.getSku(), item.getQuantity()));
            
        if (!allInStock) {
            throw new OrderProcessingException("部分商品库存不足");
        }
        
        passToNext(order);
    }
    
    private boolean checkStock(String sku, int required) {
        // 模拟库存数据
        Map<String, Integer> inventory = Map.of(
            "SKU001", 10,
            "SKU002", 5,
            "SKU003", 2
        );
        return inventory.getOrDefault(sku, 0) >= required;
    }
}