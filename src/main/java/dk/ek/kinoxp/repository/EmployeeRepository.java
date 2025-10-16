package dk.ek.kinoxp.repository;

import dk.ek.kinoxp.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> { }
