package gwkim.smartorder.option.repository;

import gwkim.smartorder.option.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
