package cn.graht.utils.tenXunMapApi;

/**
 * @author GRAHT
 */
public enum TenXunApiConstant {

    /**
     * 地址转换
     */
    TX_ADDRESS_CONVERSION("/ws/coord/v1/translate"),

    /**
     * 行政区域划分
     */
    TX_ADMINISTRATIVE_DIVISIONS("/ws/district/v1/list"),

    /**
     * 行政区划子级查询
     */
    TX_ADMINISTRATIVE_DIVISION_SUB_LEVEL_QUERY("/ws/district/v1/getchildren"),

    /**
     * 逆地址解析
     */
    TX_REVERSE_ADDRESS_RESOLUTION("/ws/geocoder/v1"),

    /**
     * IP定位
     */
    TX_IP_ADDRESS("/ws/location/v1/ip"),

    /**
     * 地点搜索
     */
    TX_ADDRESS_SEARCH("/ws/place/v1/search"),

    /**
     * 关键词输入提示
     */
    TX_KEYWORD_INPUT_HINTS("/ws/place/v1/suggestion"),

    /**
     * 静态地图
     */
    TX_STATIC("/ws/staticmap/v2"),

    /**
     * 行政区划搜索
     */
    TX_ADMINISTRATIVE_SEARCH("/ws/district/v1/search"),

    /**
     * 驾车路线规划
     */
    TX_DRIVING_ROUTE_PLANNING("/ws/direction/v1/driving");

    private final String value;

    TenXunApiConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String getTenXunApiConstant(String str) {
        try {
            return TenXunApiConstant.valueOf(str).getValue();
        } catch (IllegalArgumentException e) {
            return str;
        }
    }
}
