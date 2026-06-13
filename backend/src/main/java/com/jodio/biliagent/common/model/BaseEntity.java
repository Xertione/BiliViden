package com.jodio.biliagent.common.model;

import java.time.LocalDateTime;

public class BaseEntity {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
