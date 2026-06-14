package com.jodio.biliagent.bili.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class BiliBindingService {

    private final Map<Long, BindingRecord> bindings = new ConcurrentHashMap<>();

    public BindingResult bind(Long userId, String biliUid, String cookieSnapshot) {
        if (biliUid == null || biliUid.isBlank()) {
            throw new IllegalArgumentException("biliUid must not be blank");
        }
        if (cookieSnapshot == null || cookieSnapshot.isBlank()) {
            throw new IllegalArgumentException("cookieSnapshot must not be blank");
        }

        BindingRecord record = new BindingRecord(userId, biliUid, cookieSnapshot, "BOUND", null);
        bindings.put(userId, record);
        return new BindingResult(userId, biliUid, "BOUND");
    }

    public BindingRecord requireBinding(Long userId) {
        BindingRecord record = bindings.get(userId);
        if (record == null) {
            throw new IllegalArgumentException("bili account not bound");
        }
        return record;
    }

    public void markSynced(Long userId, LocalDateTime syncedAt) {
        BindingRecord current = requireBinding(userId);
        bindings.put(
            userId,
            new BindingRecord(current.userId(), current.biliUid(), current.cookieSnapshot(), current.bindStatus(), syncedAt)
        );
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
