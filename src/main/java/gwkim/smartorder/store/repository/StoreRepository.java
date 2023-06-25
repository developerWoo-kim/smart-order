package gwkim.smartorder.store.repository;

import gwkim.smartorder.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
