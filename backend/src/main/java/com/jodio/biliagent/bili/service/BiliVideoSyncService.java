package com.jodio.biliagent.bili.service;

import com.jodio.biliagent.bili.client.BiliRemoteClient;
import com.jodio.biliagent.video.model.VideoEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BiliVideoSyncService {

    private final BiliRemoteClient remoteClient;

    public BiliVideoSyncService(BiliRemoteClient remoteClient) {
        this.remoteClient = remoteClient;
    }

    public SyncResult syncAllSources(Long userId, String cookieSnapshot) {
        Map<String, VideoEntity> videosByBvid = new LinkedHashMap<>();
        List<SourceRecord> sourceRecords = new ArrayList<>();

        int historyCount = appendSource(
            userId,
            "history",
            remoteClient.fetchHistory(cookieSnapshot),
            videosByBvid,
            sourceRecords
        );
        int favoritesCount = appendSource(
            userId,
            "favorites",
            remoteClient.fetchFavorites(cookieSnapshot),
            videosByBvid,
            sourceRecords
        );
        int watchLaterCount = appendSource(
            userId,
            "watchLater",
            remoteClient.fetchWatchLater(cookieSnapshot),
            videosByBvid,
            sourceRecords
        );

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
