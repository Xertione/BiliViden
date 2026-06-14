package com.jodio.biliagent.bili.client;

import java.time.LocalDateTime;
import java.util.List;

public interface BiliRemoteClient {

    List<RemoteVideoItem> fetchHistory(String cookieSnapshot);

    List<RemoteVideoItem> fetchFavorites(String cookieSnapshot);

    List<RemoteVideoItem> fetchWatchLater(String cookieSnapshot);

    record RemoteVideoItem(
        String bvid,
        String title,
        String authorName,
        String coverUrl,
        String intro,
        LocalDateTime publishTime,
        Integer durationSeconds
    ) {
    }
}
