package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> { }
