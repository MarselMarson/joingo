package rt.marson.syeta.specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.data.jpa.domain.Specification;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.dto.google.places.LatLngDto;
import rt.marson.syeta.util.GeometryUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EventSpecification {
    public static final int WGS84_SRID = 4326;

    String name;
    LocalDateTime dateStartMoreThan;
    LocalDateTime dateStartLessThan;
    boolean isActive;
    Boolean isPublic;
    Long ownerId;
    String userLocation;
    User participant;
    User favourite;

    public Specification<Event> filterByCriteria() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isActive"), this.isActive));

            if (isPublic != null) {
                predicates.add(criteriaBuilder.equal(root.get("publicEvent"), this.isPublic));
            }

            if (this.userLocation != null && !this.userLocation.isBlank()) {
                Expression<Geometry> eventLocation = root.get("location");

                LatLngDto latLng = GeometryUtil.stringToLatLng(this.userLocation);

                try {
                    predicates.add(criteriaBuilder.isTrue(
                            criteriaBuilder.function(
                                    "ST_DWithin",
                                    Boolean.class,
                                    criteriaBuilder.function("geography", Geometry.class, eventLocation),
                                    criteriaBuilder.function(
                                            "ST_GeographyFromText",
                                            Geometry.class,
                                            criteriaBuilder.literal(
                                                    new WKTWriter(2)
                                                            .write(GeometryUtil.parseLocation(latLng.lon(), latLng.lat())))),
                                    criteriaBuilder.literal(GeometryUtil.RADIUS)
                            )
                    ));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            if (this.name != null && !this.name.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), "%" + this.name.toLowerCase() + "%"));
            }

            if (this.dateStartMoreThan != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), this.dateStartMoreThan));
            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("finishDate"), LocalDateTime.now()));
            }

            if (this.dateStartLessThan != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), this.dateStartLessThan));
            }

            if (this.ownerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), this.ownerId));
            }

            if (this.participant != null) {
                Predicate inParticipants = criteriaBuilder.isMember(this.participant, root.get("participants"));
                Predicate inConfirmationParticipants = criteriaBuilder.isMember(this.participant, root.get("confirmationParticipants"));
                predicates.add(criteriaBuilder.or(inParticipants, inConfirmationParticipants));
            }

            if (this.favourite != null) {
                predicates.add(criteriaBuilder.isMember(this.favourite, root.get("favouriteUsers")));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
