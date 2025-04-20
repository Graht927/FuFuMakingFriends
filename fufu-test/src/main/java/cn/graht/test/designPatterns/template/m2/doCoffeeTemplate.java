package cn.graht.test.designPatterns.template.m2;

/**
 * @author GRAHT
 */

public class doCoffeeTemplate implements coffee{
    @Override
    public void shopping() {
        order();
        pay();
        make();
        commit();
    }
    public void order(){
        System.out.println("下单");
    }
    public void pay(){
        System.out.println("支付");
    }
    public void make(){
        System.out.println("制作: 默认手工");
    }
    public void commit(){
        System.out.println("提交");
    }
}
