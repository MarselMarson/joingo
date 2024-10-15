package rt.marson.syeta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.File;

import java.util.Optional;

@Repository
public interface FileRepo  extends JpaRepository<File, Long> {
    Optional<File> findByName(String name);
}
