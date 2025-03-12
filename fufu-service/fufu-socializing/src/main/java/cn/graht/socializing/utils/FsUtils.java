package cn.graht.socializing.utils;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.exception.ThrowUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * @author GRAHT
 */

public class FsUtils {
    public static <T> void checkParam(T param) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(param), ErrorCode.PARAMS_ERROR);
        Class<?> clazz = param.getClass();
        check(clazz, param, false, null);
        if (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Long.class)
                || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Short.class)
                || clazz.equals(Byte.class) || clazz.equals(Boolean.class) || clazz.equals(Character.class)) return;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (!type.isPrimitive()){
                ThrowUtils.throwIf(ObjectUtils.isEmpty(field), ErrorCode.PARAMS_ERROR);
            }
            field.setAccessible(true);
            Object o = null;
            try {
                o = field.get(param);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            check(type, o, true, field.getName());
        }
    }

    private static void check(Class<?> type, Object o, Boolean b, String name) {
        if (type.equals(String.class)) {
            String str = (String) o;
            ThrowUtils.throwIf(StringUtils.isBlank(str), ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Integer.class)) {
            Integer integer = (Integer) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(integer) || integer < 0, ErrorCode.PARAMS_ERROR);
            if (StringUtils.isNotBlank(name) && (name.contains("id") || name.contains("Id"))) {
                ThrowUtils.throwIf(ObjectUtils.isEmpty(integer) || integer <= 0, ErrorCode.PARAMS_ERROR);
            }
        } else if (type.equals(Long.class)) {
            Long aLong = (Long) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(aLong) || aLong < 0L, ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Double.class)) {
            Double aDouble = (Double) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(aDouble) || aDouble < 0.0, ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Float.class)) {
            Float aFloat = (Float) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(aFloat) || aFloat < 0.0, ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Short.class)) {
            Short aShort = (Short) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(aShort) || aShort < 0, ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Byte.class)) {
            Byte aByte = (Byte) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(aByte) || aByte < 0, ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Boolean.class)) {
            Boolean aBoolean = (Boolean) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(aBoolean), ErrorCode.PARAMS_ERROR);
        } else if (type.equals(Character.class)) {
            Character character = (Character) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(character), ErrorCode.PARAMS_ERROR);
        } else if (type.equals(List.class)) {
            return;
        } else if (type.equals(Object[].class)) {
            Object[] objects = (Object[]) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(objects), ErrorCode.PARAMS_ERROR);
            for (Object object : objects) {
                checkParam(object);
            }
        } else if (type.equals(Date.class)) {
            Date date = (Date) o;
            ThrowUtils.throwIf(ObjectUtils.isEmpty(date), ErrorCode.PARAMS_ERROR);
        } else if (b) checkParam(o);
    }
}
