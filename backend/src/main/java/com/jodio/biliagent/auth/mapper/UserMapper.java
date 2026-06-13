package com.jodio.biliagent.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jodio.biliagent.auth.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
