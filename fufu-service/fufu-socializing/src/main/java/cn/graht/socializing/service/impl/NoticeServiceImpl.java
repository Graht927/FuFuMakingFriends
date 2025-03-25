package cn.graht.socializing.service.impl;

import cn.graht.common.commons.PageQuery;
import cn.graht.model.socializing.vos.NoticeVo;
import cn.graht.socializing.service.DynamicNoticeService;
import cn.graht.socializing.service.NoticeService;
import cn.graht.socializing.service.SystemNoticeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GRAHT
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Resource
    private DynamicNoticeService dynamicNoticeService;
    @Resource
    private SystemNoticeService systemNoticeService;
    @Override
    public List<NoticeVo> getAllNotice(String uid, PageQuery pageQuery) {

        return List.of();
    }
}
