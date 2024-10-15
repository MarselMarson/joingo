package rt.marson.syeta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.dto.event.EventWithDistanceDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByIdAndIsActiveTrue(Long id);
    Optional<Event> findByIdAndIsActiveFalse(Long id);

    @Query("""
            SELECT new rt.marson.syeta.dto.event.EventWithDistanceDto(
                e.id,
                trunc(ST_Distance(e.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326))/1000, 1)
            )
            FROM Event e WHERE e.id IN :eventIds
            """)
    List<EventWithDistanceDto> findEventsWithDistance(
            @Param("eventIds") List<Long> eventIds,
            @Param("lon") Double userLon,
            @Param("lat") Double userLat);

    @Query("""
            SELECT new rt.marson.syeta.dto.event.EventWithDistanceDto(
                e.id,
                trunc(ST_Distance(e.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326))/1000, 1)
            )
            FROM Event e WHERE e.id = :eventId
            """)
    EventWithDistanceDto findEventWithDistance(
            @Param("eventId") Long eventId,
            @Param("lon") Double userLon,
            @Param("lat") Double userLat);
}
