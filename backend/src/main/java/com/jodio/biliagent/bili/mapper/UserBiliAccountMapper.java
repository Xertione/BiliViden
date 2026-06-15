package com.jodio.biliagent.bili.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jodio.biliagent.bili.model.UserBiliAccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserBiliAccountMapper extends BaseMapper<UserBiliAccountEntity> {

    default UserBiliAccountEntity findByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapper<UserBiliAccountEntity>()
            .eq(UserBiliAccountEntity::getUserId, userId)
            .last("limit 1"));
    }
}
