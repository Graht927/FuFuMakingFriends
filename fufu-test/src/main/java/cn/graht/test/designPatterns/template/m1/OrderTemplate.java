package cn.graht.test.designPatterns.template.m1;

/**
 * @author GRAHT
 */

public abstract class OrderTemplate {
    final void doCoffeeTemplate(){
        order();
        pay();
        doCoffee();
        commit();
    }
    protected void order(){
        System.out.println("下单");
    }
    abstract void doCoffee();
    protected void pay(){
        System.out.println("支付");
    }
    protected void commit(){
        System.out.println("提交");
    }
}
