package cn.graht.socializing.service;

import cn.graht.model.socializing.dtos.EditFocusDto;
import cn.graht.model.socializing.dtos.GetFansByUidDto;
import cn.graht.model.socializing.dtos.GetFocusByUidDto;
import cn.graht.model.socializing.pojos.Focus;
import cn.graht.model.user.vos.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【focus(关注)】的数据库操作Service
* @createDate 2025-02-17 16:18:14
*/
public interface FocusService extends IService<Focus> {

    List<UserVo> getFocusByUid(GetFocusByUidDto getFocusByUidDto);

    Boolean addFocus(EditFocusDto editFocusDto);

    Boolean delFocus(EditFocusDto editFocusDto);

    List<UserVo> getFansByUid(GetFansByUidDto getFansByUidDto);
}
