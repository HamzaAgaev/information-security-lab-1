package info.sec.lab1.repository;

import info.sec.lab1.entity.CalculationResult;
import info.sec.lab1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculationResultRepository extends JpaRepository<CalculationResult, Long> {
    List<CalculationResult> findAllByUser(User user);
}
