package com.jodio.biliagent.video.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jodio.biliagent.video.model.UserVideoSourceEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserVideoSourceMapper extends BaseMapper<UserVideoSourceEntity> {

    default List<UserVideoSourceEntity> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<UserVideoSourceEntity>()
            .eq(UserVideoSourceEntity::getUserId, userId)
            .orderByDesc(UserVideoSourceEntity::getSourceTime));
    }
}
