package com.jodio.biliagent.bili.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jodio.biliagent.common.model.BaseEntity;
import java.time.LocalDateTime;

@TableName("user_bili_account")
public class UserBiliAccountEntity extends BaseEntity {

    @TableField("bili_uid")
    private String biliUid;

    @TableField("cookie_snapshot")
    private String cookieSnapshot;

    @TableField("bind_status")
    private String bindStatus;

    @TableField("last_sync_time")
    private LocalDateTime lastSyncTime;

    public String getBiliUid() {
        return biliUid;
    }

    public void setBiliUid(String biliUid) {
        this.biliUid = biliUid;
    }

    public String getCookieSnapshot() {
        return cookieSnapshot;
    }

    public void setCookieSnapshot(String cookieSnapshot) {
        this.cookieSnapshot = cookieSnapshot;
    }

    public String getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(String bindStatus) {
        this.bindStatus = bindStatus;
    }

    public LocalDateTime getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(LocalDateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
}
