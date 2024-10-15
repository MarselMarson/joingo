package rt.marson.syeta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Tag;

import java.util.List;

@Repository
public interface TagRepo extends JpaRepository<Tag, Integer> {
    List<Tag> findAllByTypesId(Short id);
}
