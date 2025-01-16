package cn.graht.sms.controller.v1;

import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.model.sms.dto.TenXunRequestParams;
import cn.graht.sms.utils.RestTemplateUtils;
import cn.graht.utils.tenXunMapApi.TenXunApiConstant;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "腾讯地图",description = "腾讯地图Controller")
public class TenXunApiController {

    @PostMapping("/tx")
    @Operation(summary = "调用api")
    @ApiResponse(responseCode = "200",description = "响应结果")
    public ResultApi<Object> tx(@RequestBody TenXunRequestParams params) {
        return ResultUtil.ok(
                RestTemplateUtils.GateRestUrl(TenXunApiConstant.getTenXunApiConstant(params.getU()),params.getParams())
        );
    }

}
