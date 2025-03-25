package cn.graht.utils.task.model;

import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class Cat {
    private String catName;
    public Cat setName(String catName) {
        this.catName = catName;
        return this;
    }
}
