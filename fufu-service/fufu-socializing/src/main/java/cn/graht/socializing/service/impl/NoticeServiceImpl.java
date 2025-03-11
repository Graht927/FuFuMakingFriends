package cn.graht.socializing.service.impl;

import cn.graht.model.socializing.pojos.Notice;
import cn.graht.socializing.service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.graht.socializing.mapper.NoticeMapper;

/**
* @author Graht
* @description 针对表【notice(通知表)】的数据库操作Service实现
* @createDate 2025-03-11 09:55:40
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService {

}




