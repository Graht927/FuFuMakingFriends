package cn.graht.organizeBureau.mapper;

import cn.graht.model.organizeBureau.pojos.Activity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author graht
* @description 针对表【team(队伍)】的数据库操作Mapper
*/
public interface ActivityMapper extends BaseMapper<Activity> {
    /**
     *  获取当前用户加入的所有team
     * @param userId
     * @return
     */
    List<Activity> getAddTeamByUserId(String userId);
}




