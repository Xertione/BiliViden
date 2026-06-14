package com.jodio.biliagent.recommend.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    public List<String> generateReasons() {
        // Minimal rate-limit convention only:
        // redis key example: biliagent:recommend:rate-limit:user:{userId}:daily
        // This round does not introduce Redisson, Lua execution, or controller wiring.
        return List.of(
            "\u7ed3\u5408\u6700\u8fd1\u8f7b\u53cd\u9988\uff0c\u4f18\u5148\u8865\u5145\u4f60\u521a\u8868\u73b0\u51fa\u5174\u8da3\u7684\u4e3b\u9898\u3002",
            "\u4fdd\u7559\u4e00\u6761\u76f8\u90bb\u4e3b\u9898\u5185\u5bb9\uff0c\u907f\u514d\u63a8\u8350\u7ed3\u679c\u8fc7\u4e8e\u5355\u4e00\u3002",
            "\u52a0\u5165\u4e00\u6761\u57fa\u7840\u70ed\u95e8\u5185\u5bb9\uff0c\u4f5c\u4e3a\u51b7\u542f\u52a8\u515c\u5e95\u3002"
        );
    }
}
