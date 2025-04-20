package cn.graht.test.designPatterns.template.m2;

/**
 * @author GRAHT
 */

public class TestM2Template extends doCoffeeTemplate {
    public static void main(String[] args) {
        TestM2Template testM2Template = new TestM2Template();
        testM2Template.shopping();
    }
    @Override
    public void make(){
        System.out.println("制作: 机器制作");
    }
}
