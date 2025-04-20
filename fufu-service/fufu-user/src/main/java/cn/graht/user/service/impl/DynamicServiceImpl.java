package cn.graht.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.user.dtos.CreateDynamicDto;
import cn.graht.model.user.dtos.GetDynamicByUidDto;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.DynamicVo;
import cn.graht.model.user.vos.UserDynamicVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.mapper.DynamicMapper;
import cn.graht.user.service.DynamicService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private SocializingFeignApi socializingFeignApi;

    @Override
    public List<Dynamic> getDynamicsByUserId(GetDynamicByUidDto getDynamicByUidDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getDynamicByUidDto), ErrorCode.PARAMS_ERROR);
        IPage<Dynamic> dynamicPage = new Page<>( getDynamicByUidDto.getPageNum(),getDynamicByUidDto.getPageSize());
        QueryWrapper<Dynamic> queryWrapper = new QueryWrapper<>();
        if (ObjectUtils.isNotEmpty(getDynamicByUidDto.getUid())){
            queryWrapper.eq("userId", getDynamicByUidDto.getUid());
        }
        queryWrapper.orderByDesc("id");
        List<Dynamic> dynamics = dynamicMapper.selectList(dynamicPage, queryWrapper);
        return dynamics;
    }

    @Override
    public DynamicVo createDynamic(CreateDynamicDto createDynamicDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(createDynamicDto), ErrorCode.PARAMS_ERROR);
         String loginId = (String) StpUtil.getLoginId();
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(createDynamicDto, dynamic);
        List<String> images = createDynamicDto.getImages();
        if (ObjectUtils.isNotEmpty(images)) {
            dynamic.setImages(JSONUtil.toJsonStr(images));
        }
        int insert = dynamicMapper.insert(dynamic);
        ThrowUtils.throwIf(insert!=1, ErrorCode.SYSTEM_ERROR);
        DynamicVo dynamicVo = new DynamicVo();
        dynamicVo.setImages(createDynamicDto.getImages());
        BeanUtils.copyProperties(dynamic, dynamicVo);
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(dynamic.getUserId());
        if (ObjectUtils.isNotEmpty(userInfo)) {
            UserDynamicVo userDynamicVo = new UserDynamicVo();
            userDynamicVo.setAvatar(userInfo.getData().getAvatarUrl());
            userDynamicVo.setName(userInfo.getData().getNickname());
            dynamicVo.setAuthor(userDynamicVo);
        }
        ResultApi<Boolean> thumbsup = socializingFeignApi.isThumbsup(dynamic.getId(), loginId);
        dynamicVo.setIsLike(thumbsup.getData());
        dynamicVo.setCreateTime(new Date());
        return dynamicVo;
    }
}




