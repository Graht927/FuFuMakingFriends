package cn.graht.test.designPatterns.mergeStrategyAndTemplate;

/**
 * @author GRAHT
 */

public abstract class doCoffee implements coffee{
    @Override
    public void shopping() {
        order();
        pay();
        make();
        commit();
    }
    protected void order(){
        System.out.println("下单");
    }
    abstract void pay();
    abstract void make();
    protected void commit(){
        System.out.println("提交");
    }
}
