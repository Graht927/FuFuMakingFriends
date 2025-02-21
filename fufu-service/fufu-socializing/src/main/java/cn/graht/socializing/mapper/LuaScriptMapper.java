package cn.graht.socializing.mapper;

import cn.graht.model.luaScript.pojos.LuaScript;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【luaScript(lua脚本)】的数据库操作Mapper
* @createDate 2025-02-20 09:43:32
* @Entity generator.domain.LuaScript
*/
@DS("app")
public interface LuaScriptMapper extends BaseMapper<LuaScript> {

    List<LuaScript> getAll();

}




