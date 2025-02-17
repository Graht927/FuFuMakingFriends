package cn.graht.user.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.user.dtos.CreateDynamicDto;
import cn.graht.model.user.dtos.GetDynamicByUidDto;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.DynamicVo;
import cn.graht.user.mapper.DynamicMapper;
import cn.graht.user.service.DynamicService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author GRAHT
* @description 针对表【dynamic(动态)】的数据库操作Service实现
* @createDate 2025-02-17 10:04:57
*/
@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic>
    implements DynamicService {
    @Resource
    private DynamicMapper dynamicMapper;

    @Override
    public List<Dynamic> getDynamicsByUserId(GetDynamicByUidDto getDynamicByUidDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getDynamicByUidDto), ErrorCode.PARAMS_ERROR);
        Page<Dynamic> dynamicPage = new Page<>( getDynamicByUidDto.getPageSize(),getDynamicByUidDto.getPageNum());
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", getDynamicByUidDto.getUid());
        Page<Dynamic> page = page(dynamicPage, queryWrapper);
        return page.getRecords();
    }

    @Override
    public DynamicVo createDynamic(CreateDynamicDto createDynamicDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(createDynamicDto), ErrorCode.PARAMS_ERROR);
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(createDynamicDto, dynamic);
        List<String> images = createDynamicDto.getImages();
        if (ObjectUtils.isNotEmpty(images)) {
            dynamic.setImages(JSONUtil.toJsonStr(images));
        }
        int insert = dynamicMapper.insert(dynamic);
        ThrowUtils.throwIf(insert!=1, ErrorCode.SYSTEM_ERROR);
        DynamicVo dynamicVo = new DynamicVo();
        BeanUtils.copyProperties(dynamic, dynamicVo);
        dynamicVo.setCreateTime(new Date());
        return dynamicVo;
    }
}




