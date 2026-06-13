package com.jodio.biliagent.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jodio.biliagent.auth.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    default UserEntity findByUsername(String username) {
        return selectOne(new LambdaQueryWrapper<UserEntity>()
            .eq(UserEntity::getUsername, username)
            .last("limit 1"));
    }
}
