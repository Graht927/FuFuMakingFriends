package cn.graht.organizeBureau.service;

import cn.graht.common.commons.PageQuery;
import cn.graht.model.organizeBureau.dtos.TeamQuery;
import cn.graht.model.organizeBureau.dtos.TeamQuitRequest;
import cn.graht.model.organizeBureau.dtos.TeamSendJoinRequest;
import cn.graht.model.organizeBureau.dtos.TeamUpdateRequest;
import cn.graht.model.organizeBureau.pojos.Activity;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import cn.graht.model.user.pojos.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author graht
* @description 针对表【team(队伍)】的数据库操作Service
*/
public interface ActivityService extends IService<Activity> {

    /**
     *  创建队伍
     * @param activity
     * @param user
     * @return
     */
    long addTeam(Activity activity, User user);

    /**
     *  分页查询队伍
     * @param teamQuery
     * @return
     */
    List<ActivityUserVo> listTeamByPage(TeamQuery teamQuery);

    /**
     *  修改队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     *  发送加入队伍申请  这里直接加入
     *  //todo 使用异步mq进行发送消息Netty建立长连接达到消费和生产消息
     * @param teamSendJoinRequest
     * @param loginUser
     * @return
     */
    Boolean sendJoin(TeamSendJoinRequest teamSendJoinRequest, User loginUser);

    /**
     *  退出队伍
     * @param teamQuitRequest
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     *  解散队伍
     * @param tid
     * @param loginUser
     * @return
     */
    Boolean deleteTeam(long tid, User loginUser);

    /**
     *  获取当前用户创建的队伍  未过期的
     * @param loginUser
     * @return
     */
    List<ActivityUserVo> getCreateTeamByUser(User loginUser, PageQuery pageQuery);

    /**
     *  获取当前用户创建的队伍  已过期的
     * @param loginUser
     * @return
     */
    List<ActivityUserVo> getExpireTeamByUser(User loginUser);

    /**
     * 获取当前用户已加入的队伍
     * @param loginUser
     * @return
     */
    List<ActivityUserVo> getAddTeamByUser(User loginUser, PageQuery pageQuery);

    /**
     * 通过id来获取队伍详细信息
     * @param teamId
     * @param loginUser
     * @return
     */
    ActivityUserVo getTeamInfoByTid(long teamId, User loginUser);
    ActivityUserVo getTeamInfoByTid(long teamId, String uid);

    /**
     * 删除已过期的房间
     * @param tid
     * @param loginUser
     * @return
     */
    Boolean delExpireTeam(long tid, User loginUser);
}
