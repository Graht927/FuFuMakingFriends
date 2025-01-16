package cn.graht.model.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * @author GRAHT
 */
@Data
@Schema(description = "腾讯地图Api参数")
public class TenXunRequestParams {
    @Schema(description = "自定义的EnumVal")
    private String u;
    @Schema(description = "请求参数map")
    private Map<String,String> params;
}
