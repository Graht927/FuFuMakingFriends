package cn.graht.socializing.mapper;

import cn.graht.common.commons.PageQuery;
import cn.graht.model.socializing.pojos.SystemNotice;
import cn.graht.model.socializing.vos.NoticeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【system_notice(系统通知表)】的数据库操作Mapper
* @createDate 2025-03-12 10:46:09
*/
public interface SystemNoticeMapper extends BaseMapper<SystemNotice> {

    List<NoticeVo> selectAllByPage(String uid, Long index);
}




