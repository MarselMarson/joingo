package rt.marson.syeta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Language;

@Repository
public interface LanguageRepo extends JpaRepository<Language, Long> {
}
