package gwkim.smartorder.redis.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@ToString
@Getter
@RedisHash("redisStore")
public class RedisStore {
    @Id
    private String id;
    private Long point;
    private LocalDateTime refreshTime;

    @Builder
    public RedisStore(String id, Long point, LocalDateTime refreshTime) {
        this.id = id;
        this.point = point;
        this.refreshTime = refreshTime;
    }
}
