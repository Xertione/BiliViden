package com.jodio.biliagent.knowledge.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jodio.biliagent.knowledge.model.KnowledgeCardEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeCardMapper extends BaseMapper<KnowledgeCardEntity> {

    default List<KnowledgeCardEntity> findByUserId(Long userId) {
        return selectList(new LambdaQueryWrapper<KnowledgeCardEntity>()
            .eq(KnowledgeCardEntity::getUserId, userId)
            .orderByDesc(KnowledgeCardEntity::getCreatedAt));
    }
}
