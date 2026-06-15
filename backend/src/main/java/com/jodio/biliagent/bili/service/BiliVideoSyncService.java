package com.jodio.biliagent.bili.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jodio.biliagent.bili.client.BiliRemoteClient;
import com.jodio.biliagent.video.mapper.UserVideoSourceMapper;
import com.jodio.biliagent.video.mapper.VideoMapper;
import com.jodio.biliagent.video.model.UserVideoSourceEntity;
import com.jodio.biliagent.video.model.VideoEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BiliVideoSyncService {

    private final BiliRemoteClient remoteClient;
    private final VideoMapper videoMapper;
    private final UserVideoSourceMapper sourceMapper;

    public BiliVideoSyncService(BiliRemoteClient remoteClient, VideoMapper videoMapper, UserVideoSourceMapper sourceMapper) {
        this.remoteClient = remoteClient;
        this.videoMapper = videoMapper;
        this.sourceMapper = sourceMapper;
    }

    @Transactional
    public SyncResult syncAllSources(Long userId, String cookieSnapshot) {
        Map<String, VideoEntity> videosByBvid = new LinkedHashMap<>();
        List<SourceRecord> sourceRecords = new ArrayList<>();

        int historyCount = appendSource(userId, "history", remoteClient.fetchHistory(cookieSnapshot), videosByBvid, sourceRecords);
        int favoritesCount = appendSource(userId, "favorites", remoteClient.fetchFavorites(cookieSnapshot), videosByBvid, sourceRecords);
        int watchLaterCount = appendSource(userId, "watchLater", remoteClient.fetchWatchLater(cookieSnapshot), videosByBvid, sourceRecords);

        // Persist video entities (insert if not exists by bvid)
        for (VideoEntity video : videosByBvid.values()) {
            VideoEntity existing = videoMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VideoEntity>()
                    .eq(VideoEntity::getBvid, video.getBvid())
                    .last("limit 1")
            );
            if (existing == null) {
                videoMapper.insert(video);
            }
        }

        // Persist source records (insert if not exists by user_id + video_id + source_type)
        for (SourceRecord record : sourceRecords) {
            // Re-query video id from DB
            VideoEntity video = videoMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VideoEntity>()
                    .eq(VideoEntity::getBvid, record.bvid())
                    .last("limit 1")
            );
            if (video == null) continue;

            UserVideoSourceEntity sourceEntity = new UserVideoSourceEntity();
            sourceEntity.setUserId(userId);
            sourceEntity.setVideoId(video.getId());
            sourceEntity.setBvid(record.bvid());
            sourceEntity.setSourceType(record.sourceType());
            sourceEntity.setSourceTime(record.sourceTime());
            sourceMapper.insert(sourceEntity);
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("historyCount", historyCount);
        counts.put("favoritesCount", favoritesCount);
        counts.put("watchLaterCount", watchLaterCount);
        counts.put("uniqueVideoCount", videosByBvid.size());
        counts.put("sourceRecordCount", sourceRecords.size());

        return new SyncResult(counts, List.copyOf(videosByBvid.values()), List.copyOf(sourceRecords));
    }

    private int appendSource(
        Long userId,
        String sourceType,
        List<BiliRemoteClient.RemoteVideoItem> items,
        Map<String, VideoEntity> videosByBvid,
        List<SourceRecord> sourceRecords
    ) {
        for (BiliRemoteClient.RemoteVideoItem item : items) {
            VideoEntity video = videosByBvid.computeIfAbsent(item.bvid(), ignored -> toVideoEntity(item));
            sourceRecords.add(new SourceRecord(userId, video.getBvid(), sourceType, item.publishTime()));
        }
        return items.size();
    }

    private VideoEntity toVideoEntity(BiliRemoteClient.RemoteVideoItem item) {
        VideoEntity video = new VideoEntity();
        video.setBvid(item.bvid());
        video.setTitle(item.title());
        video.setAuthorName(item.authorName());
        video.setCoverUrl(item.coverUrl());
        video.setIntro(item.intro());
        video.setPublishTime(item.publishTime());
        video.setDurationSeconds(item.durationSeconds());
        return video;
    }

    public record SyncResult(
        Map<String, Integer> counts,
        List<VideoEntity> videos,
        List<SourceRecord> sourceRecords
    ) {
    }

    public record SourceRecord(
        Long userId,
        String bvid,
        String sourceType,
        LocalDateTime sourceTime
    ) {
    }
}
