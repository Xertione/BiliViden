package com.jodio.biliagent.bili.client;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StubBiliRemoteClient implements BiliRemoteClient {

    @Override
    public List<RemoteVideoItem> fetchHistory(String cookieSnapshot) {
        return List.of(
            new RemoteVideoItem(
                "BV1demo001",
                "AI 推荐系统拆解",
                "up-demo",
                "https://example.com/cover-1.jpg",
                "history-intro",
                LocalDateTime.of(2026, 6, 1, 10, 0),
                600
            ),
            new RemoteVideoItem(
                "BV1demo002",
                "Spring Boot 入门",
                "up-java",
                "https://example.com/cover-2.jpg",
                "history-intro-2",
                LocalDateTime.of(2026, 6, 2, 11, 0),
                720
            )
        );
    }

    @Override
    public List<RemoteVideoItem> fetchFavorites(String cookieSnapshot) {
        return List.of(
            new RemoteVideoItem(
                "BV1demo001",
                "AI 推荐系统拆解",
                "up-demo",
                "https://example.com/cover-1.jpg",
                "favorites-intro",
                LocalDateTime.of(2026, 6, 1, 10, 0),
                600
            )
        );
    }

    @Override
    public List<RemoteVideoItem> fetchWatchLater(String cookieSnapshot) {
        return List.of(
            new RemoteVideoItem(
                "BV1demo003",
                "Redis 限流实战",
                "up-backend",
                "https://example.com/cover-3.jpg",
                "watch-later-intro",
                LocalDateTime.of(2026, 6, 3, 12, 0),
                840
            )
        );
    }
}
