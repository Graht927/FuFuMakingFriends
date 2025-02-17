package cn.graht.user.service;

import cn.graht.model.user.dtos.CreateDynamicDto;
import cn.graht.model.user.dtos.GetDynamicByUidDto;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.DynamicVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【dynamic(动态)】的数据库操作Service
* @createDate 2025-02-17 10:04:57
*/
public interface DynamicService extends IService<Dynamic> {
    List<Dynamic> getDynamicsByUserId(GetDynamicByUidDto getDynamicByUidDto);

    DynamicVo createDynamic(CreateDynamicDto createDynamicDto);
}
