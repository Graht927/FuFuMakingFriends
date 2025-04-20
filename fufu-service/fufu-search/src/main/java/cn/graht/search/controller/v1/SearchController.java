package cn.graht.search.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.feignApi.organizeBureau.ActivityFeignApi;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.organizeBureau.dtos.TeamQuery;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import cn.graht.model.search.vo.FindVo;
import cn.graht.model.user.dtos.GetDynamicByUidDto;
import cn.graht.model.user.dtos.RandomGetUserDto;
import cn.graht.model.user.vos.DynamicVo;
import cn.graht.model.user.vos.UserVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1/search")
@Tag(name = "搜索|匹配|发现", description = "搜索|匹配|发现Controller")
@Slf4j
public class SearchController {

    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private SocializingFeignApi socializingFeignApi;
    @Resource
    private ActivityFeignApi activityFeignApi;

    @PostMapping("/mate")
    public ResultApi<List<UserVo>> mate(@RequestBody PageQuery pageQuery) {
        // 根据自己年龄来匹配用户
        String loginId = (String) StpUtil.getLoginId();
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(loginId);
        Date beginDate = null;
        Date endDate = null;

        if (ObjectUtils.isNotEmpty(userInfo)) {
            UserVo loginUser = userInfo.getData();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(loginUser.getBirthday());

            calendar.add(Calendar.YEAR, -5);
            beginDate = calendar.getTime();

            calendar.setTime(loginUser.getBirthday());
            calendar.add(Calendar.YEAR, +5);
            endDate = calendar.getTime();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -23);
            beginDate = calendar.getTime();

            calendar.add(Calendar.YEAR, 5); // 从当前年份减去23年，再加上5年，得到减去18年的日期
            endDate = calendar.getTime();
        }

        String beginBirthday = recoverDate(beginDate);
        String endBirthday = recoverDate(endDate);
        log.info("begin:{}, end:{}", beginBirthday, endBirthday);

        RandomGetUserDto randomGetUserDto = new RandomGetUserDto();
        randomGetUserDto.setBeginBirthday(beginBirthday);
        randomGetUserDto.setEndBirthday(endBirthday);
        randomGetUserDto.setPageNum((int) pageQuery.getPageNum());
        randomGetUserDto.setPageSize((int) pageQuery.getPageSize());
        ResultApi<List<UserVo>> listResultApi = userFeignApi.randomGetUserVo(randomGetUserDto);
        return listResultApi;
    }

    private static String recoverDate(Date date) {
        date.setMonth(0);
        date.setDate(1);
        date.setMinutes(0);
        date.setHours(0);
        date.setSeconds(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    @PostMapping("/discover")
    public ResultApi<List<FindVo>> discover(@RequestBody PageQuery pageQuery) {
        String loginId = (String) StpUtil.getLoginId();
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(loginId);
        //分页获取动态
        GetDynamicByUidDto getDynamicByUidDto = new GetDynamicByUidDto();
        getDynamicByUidDto.setPageNum((int) pageQuery.getPageNum());
        getDynamicByUidDto.setPageSize((int) pageQuery.getPageSize());
        List<DynamicVo>  dynamicVos = List.of();
        List<ActivityUserVo>  activityVos = List.of();
        ResultApi<List<DynamicVo>> dynamicsByUserId = userFeignApi.getDynamicsByUserId(getDynamicByUidDto);
        if (ObjectUtils.isNotEmpty(dynamicsByUserId)){
            dynamicVos = dynamicsByUserId.getData();
        }
        List<DynamicVo> dynamicVoList = dynamicVos.stream().filter(dynamicVo -> !dynamicVo.getUserId().equals(loginId)).toList();
        //分页获取活动
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setPageNum((int) pageQuery.getPageNum());
        teamQuery.setPageSize((int) pageQuery.getPageSize());
        ResultApi<List<ActivityUserVo>> activityUserVoResultApi = activityFeignApi.listTeamByPage(teamQuery);
        if (ObjectUtils.isNotEmpty(activityUserVoResultApi)){
            activityVos = activityUserVoResultApi.getData();
        }
        List<ActivityUserVo> activityUserVoList = activityVos.stream().filter(activityUserVo -> !activityUserVo.getUserId().equals(loginId)).limit(6).toList();
        if (ObjectUtils.isNotEmpty(dynamicVoList)){
            dynamicVoList = new ArrayList<>(dynamicVoList);
        }
        if (ObjectUtils.isNotEmpty(activityUserVoList)){
            activityUserVoList = new ArrayList<>(activityUserVoList);
        }
        //组合打乱 返回
        List<FindVo> res = new ArrayList<>();
        activityUserVoList.forEach(activityUserVo -> {
            FindVo findVo = new FindVo();
            findVo.setModel("group");
            BeanUtils.copyProperties(activityUserVo,findVo);
            res.add(findVo);
        });
        dynamicVoList.forEach(dynamicVo -> {
            FindVo findVo = new FindVo();
            findVo.setModel("dynamic");
            BeanUtils.copyProperties(dynamicVo,findVo);
            res.add(findVo);
        });
        Collections.shuffle(res);
        return ResultUtil.ok(res);
    }
}
