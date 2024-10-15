package rt.marson.syeta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Country;

import java.util.Optional;

@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name);
}
