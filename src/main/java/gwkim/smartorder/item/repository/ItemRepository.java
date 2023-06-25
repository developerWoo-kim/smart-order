package gwkim.smartorder.item.repository;

import gwkim.smartorder.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
