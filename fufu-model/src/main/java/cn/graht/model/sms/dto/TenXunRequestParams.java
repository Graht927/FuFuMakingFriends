package cn.graht.model.sms.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author GRAHT
 */
@Data
public class TenXunRequestParams {
    private String u;
    private Map<String,String> params;
}
