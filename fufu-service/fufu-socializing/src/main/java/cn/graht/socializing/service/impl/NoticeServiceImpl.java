package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.enums.NoticeType;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.pojos.DynamicNotice;
import cn.graht.model.socializing.pojos.SystemNotice;
import cn.graht.model.socializing.vos.NoticeVo;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.DynamicVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.mapper.SystemNoticeMapper;
import cn.graht.socializing.service.DynamicNoticeService;
import cn.graht.socializing.service.NoticeService;
import cn.graht.socializing.service.SystemNoticeService;
import cn.graht.socializing.utils.UserToolUtils;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author GRAHT
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Resource
    private SystemNoticeMapper systemNoticeMapper;
    @Resource
    private SystemNoticeService systemNoticeService;
    @Resource
    private DynamicNoticeService dynamicNoticeService;
    @Resource
    private UserToolUtils userToolUtils;
    @Resource
    private UserFeignApi userFeignApi;
    @Override
    public List<NoticeVo> getAllNotice(String uid, PageQuery pageQuery) {
        Long index = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        List<NoticeVo> noticeVos = systemNoticeMapper.selectAllByPage(uid, index);
        return noticeVos.stream().map(noticeVo -> {
            UserVo userVo = userToolUtils.getUserFromCacheOrFeign(noticeVo.getUserId2());
            noticeVo.setAvatarUrl(userVo.getAvatarUrl());
            noticeVo.setNickname(userVo.getNickname());
                Map<String,String> bean = JSONUtil.toBean(noticeVo.getContent(), Map.class);
            if (NoticeType.FOCUS.getValue().equals(noticeVo.getType())) {
                noticeVo.setContent(bean.get("message"));
                return noticeVo;
            }
            if (NoticeType.THUMBS_UP.getValue().equals(noticeVo.getType())) {
                noticeVo.setContent(bean.get("message"));
                noticeVo.setDynamicId(bean.get("dynamicId"));
                ResultApi<DynamicVo> dynamicById = userFeignApi.getDynamicById(Long.parseLong(noticeVo.getDynamicId()));
                ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicById) || ObjectUtils.isEmpty(dynamicById.getData()), ErrorCode.PARAMS_ERROR);
                noticeVo.setCoverImage(dynamicById.getData().getImage());
                return noticeVo;
            }
            return noticeVo;
        }).toList();
    }
    @Override
    public Integer getCountPage(String uid, Long pageSize) {
        Long c1 = dynamicNoticeService.lambdaQuery().eq(DynamicNotice::getUserId, uid).count();
        Long c2 = systemNoticeService.lambdaQuery().eq(SystemNotice::getUserId, uid).count();
        if (c1 + c2 == 0) {
            return 0;
        }
        Long total = c1 + c2;
        Integer count = (int) Math.ceil((double) total /pageSize);
        return count;
    }
}
