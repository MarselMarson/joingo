package rt.marson.syeta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Type;

@Repository
public interface TypeRepo extends JpaRepository<Type, Short> {
}
