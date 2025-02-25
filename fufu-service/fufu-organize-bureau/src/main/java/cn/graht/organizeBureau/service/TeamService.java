package cn.graht.organizeBureau.service;

import cn.graht.model.organizeBureau.dtos.TeamQuery;
import cn.graht.model.organizeBureau.dtos.TeamQuitRequest;
import cn.graht.model.organizeBureau.dtos.TeamSendJoinRequest;
import cn.graht.model.organizeBureau.dtos.TeamUpdateRequest;
import cn.graht.model.organizeBureau.pojos.Team;
import cn.graht.model.organizeBureau.vos.TeamUserVo;
import cn.graht.model.user.pojos.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author graht
* @description 针对表【team(队伍)】的数据库操作Service
*/
public interface TeamService extends IService<Team> {

    /**
     *  创建队伍
     * @param team
     * @param user
     * @return
     */
    long addTeam(Team team, User user);

    /**
     *  分页查询队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVo> listTeamByPage(TeamQuery teamQuery, HttpServletRequest request);

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
    List<TeamUserVo> getCreateTeamByUser(User loginUser);

    /**
     *  获取当前用户创建的队伍  已过期的
     * @param loginUser
     * @return
     */
    List<TeamUserVo> getExpireTeamByUser(User loginUser);

    /**
     * 获取当前用户已加入的队伍
     * @param loginUser
     * @return
     */
    List<TeamUserVo> getAddTeamByUser(User loginUser);

    /**
     * 通过id来获取队伍详细信息
     * @param teamId
     * @param loginUser
     * @return
     */
    TeamUserVo getTeamInfoByTid(long teamId,User loginUser);

    /**
     * 删除已过期的房间
     * @param tid
     * @param loginUser
     * @return
     */
    Boolean delExpireTeam(long tid, User loginUser);
}
