package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.pojos.Thumbsup;
import cn.graht.model.socializing.vos.ThumbsupVo;
import cn.graht.socializing.mapper.ThumbsupMapper;
import cn.graht.socializing.service.ThumbsupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GRAHT
 * @description 针对表【thumbsUp(点赞)】的数据库操作Service实现
 * @createDate 2025-02-17 16:18:14
 */
@Service
public class ThumbsupServiceImpl extends ServiceImpl<ThumbsupMapper, Thumbsup>
        implements ThumbsupService {
    @Resource
    private ThumbsupMapper thumbsupMapper;
    @Override
    public List<ThumbsupVo> getThubmsUpByCid(Long dynamicId, PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicId) || dynamicId < 0L, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(pageQuery), ErrorCode.PARAMS_ERROR);
        Page<Thumbsup> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<Thumbsup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Thumbsup::getDynamicId, dynamicId);
        Page<Thumbsup> res = thumbsupMapper.selectPage(page, queryWrapper);
        List<Thumbsup> records = res.getRecords();
        if (ObjectUtils.isNotEmpty(records)) {
            return records.stream().map(t->{
                ThumbsupVo thumbsupVo = new ThumbsupVo();
                BeanUtils.copyProperties(t, thumbsupVo);
             return thumbsupVo;
            }).toList();
        }
        return List.of();
    }
}




