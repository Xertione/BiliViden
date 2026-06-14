package com.jodio.biliagent.video.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jodio.biliagent.common.model.BaseEntity;
import java.time.LocalDateTime;

@TableName("user_video_source")
public class UserVideoSourceEntity extends BaseEntity {
    @TableField("video_id")
    private Long videoId;
    @TableField(exist = false)
    private String bvid;
    @TableField("source_type")
    private String sourceType;
    @TableField("source_time")
    private LocalDateTime sourceTime;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getBvid() {
        return bvid;
    }

    public void setBvid(String bvid) {
        this.bvid = bvid;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public LocalDateTime getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(LocalDateTime sourceTime) {
        this.sourceTime = sourceTime;
    }
}
