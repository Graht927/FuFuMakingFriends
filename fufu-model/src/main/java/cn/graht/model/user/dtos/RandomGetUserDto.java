package cn.graht.model.user.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class RandomGetUserDto {
    Integer pageNum;
    Integer pageSize;
    String beginBirthday;
    String endBirthday;
}
