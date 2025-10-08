package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Long> {}