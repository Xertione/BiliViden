package com.jodio.biliagent.bili.service;

import com.jodio.biliagent.bili.mapper.UserBiliAccountMapper;
import com.jodio.biliagent.bili.model.UserBiliAccountEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BiliBindingService {

    private final UserBiliAccountMapper accountMapper;

    public BiliBindingService(UserBiliAccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Transactional
    public BindingResult bind(Long userId, String biliUid, String cookieSnapshot) {
        if (biliUid == null || biliUid.isBlank()) {
            throw new IllegalArgumentException("biliUid must not be blank");
        }
        if (cookieSnapshot == null || cookieSnapshot.isBlank()) {
            throw new IllegalArgumentException("cookieSnapshot must not be blank");
        }

        UserBiliAccountEntity existing = accountMapper.findByUserId(userId);
        if (existing != null) {
            existing.setBiliUid(biliUid);
            existing.setCookieSnapshot(cookieSnapshot);
            existing.setBindStatus("BOUND");
            accountMapper.updateById(existing);
            return new BindingResult(userId, biliUid, "BOUND");
        }

        UserBiliAccountEntity entity = new UserBiliAccountEntity();
        entity.setUserId(userId);
        entity.setBiliUid(biliUid);
        entity.setCookieSnapshot(cookieSnapshot);
        entity.setBindStatus("BOUND");
        accountMapper.insert(entity);
        return new BindingResult(userId, biliUid, "BOUND");
    }

    public BindingRecord requireBinding(Long userId) {
        UserBiliAccountEntity entity = accountMapper.findByUserId(userId);
        if (entity == null) {
            throw new IllegalArgumentException("bili account not bound");
        }
        return new BindingRecord(
            entity.getUserId(),
            entity.getBiliUid(),
            entity.getCookieSnapshot(),
            entity.getBindStatus(),
            entity.getLastSyncTime()
        );
    }

    @Transactional
    public void markSynced(Long userId, LocalDateTime syncedAt) {
        UserBiliAccountEntity entity = accountMapper.findByUserId(userId);
        if (entity == null) {
            throw new IllegalArgumentException("bili account not bound");
        }
        entity.setLastSyncTime(syncedAt);
        accountMapper.updateById(entity);
    }

    public record BindingResult(Long userId, String biliUid, String bindStatus) {
    }

    public record BindingRecord(
        Long userId,
        String biliUid,
        String cookieSnapshot,
        String bindStatus,
        LocalDateTime lastSyncTime
    ) {
    }
}
