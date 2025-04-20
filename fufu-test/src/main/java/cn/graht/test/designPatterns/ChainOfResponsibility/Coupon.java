package cn.graht.test.designPatterns.ChainOfResponsibility;

import lombok.Data;

@Data
public class Coupon {
    private String code;
    private boolean valid;
    
    public Coupon(String code, boolean valid) {
        this.code = code;
        this.valid = valid;
    }
    
    public boolean isValid() { return valid; }
}