package cn.graht.test.designPatterns.template.m1;

/**
 * @author GRAHT
 */

public class TestTemplate {
    public static void main(String[] args) {
        OrderTemplate machineOrderTemplate = new MachineOrderTemplate();
        machineOrderTemplate.doCoffeeTemplate();
        OrderTemplate artificialOrderTemplate = new ArtificialOrderTemplate();
        artificialOrderTemplate.doCoffeeTemplate();
    }
}
