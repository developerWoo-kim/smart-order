package gwkim.smartorder.redis.repository;

import gwkim.smartorder.redis.domain.AvailablePoint;
import org.springframework.data.repository.CrudRepository;

public interface AvailablePointRedisRepository extends CrudRepository<AvailablePoint, String> {
}
