package com.jodio.biliagent.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jodio.biliagent.video.model.VideoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper extends BaseMapper<VideoEntity> {
}
