package rt.marson.syeta.service.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.entity.*;
import rt.marson.syeta.dto.LanguageDto;
import rt.marson.syeta.dto.TagDto;
import rt.marson.syeta.dto.event.EventCreateDto;
import rt.marson.syeta.dto.event.EventDto;
import rt.marson.syeta.dto.event.EventPatchDto;
import rt.marson.syeta.dto.event.EventWithDistanceDto;
import rt.marson.syeta.dto.google.places.LatLngDto;
import rt.marson.syeta.filter.EventFilter;
import rt.marson.syeta.mapper.EventMapper;
import rt.marson.syeta.repository.EventRepo;
import rt.marson.syeta.service.*;
import rt.marson.syeta.service.user.LanguageService;
import rt.marson.syeta.service.user.UserService;
import rt.marson.syeta.specification.EventSpecification;
import rt.marson.syeta.util.GeometryUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class EventService {
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;
    private final FileService fileService;
    private final EntityManager entityManager;
    @Lazy private final UserService userService;
    private final TypeService typeService;
    private final LanguageService languageService;
    private final TagService tagService;

    @Transactional
    public EventDto createEvent(EventCreateDto dto, Long authUserId) {
        Event event = eventMapper.createEntity(dto);
        event.setOwner(userService.findUserById(authUserId));

        List<File> newFiles = new ArrayList<>();
        if (dto.getPhoto() != null) {
            for (String url : dto.getPhoto()) {
                String fileName = fileService.getNameFromUrl(url);
                newFiles.add(fileService.getByFileName(fileName));
            }
        }
        event.setPhoto(newFiles);

        eventRepo.saveAndFlush(event);
        entityManager.refresh(event);
        return toEventDto(event);
    }

    public EventDto getActiveEventById(Long eventId) {
        return toEventDto(findActiveEventById(eventId));
    }

    public Event findActiveEventById(Long id) {
        return eventRepo.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("event id: " +
                        id + " is not found"));
    }

    public EventDto toEventDto(Event event) {
        User currentUser = userService.getCurrentUserOrNullIfAnonymous();

        return toEventDto(event, currentUser);
    }

    public EventDto toEventDto(Event event, User currentUser) {
        EventDto dto = eventMapper.toDto(event);
        if (currentUser != null) {
            dto.setInFavourite(currentUser.getFavouriteEvents().contains(event));
            if (currentUser.getParticipateEvents().contains(event)) {
                dto.setIsParticipate("true");
            } else {
                if (event.getConfirmationParticipants().contains(currentUser)) {
                    dto.setIsParticipate("wait");
                } else {
                    dto.setIsParticipate("false");
                }
            }
        }

        return dto;
    }

    public List<EventDto> toEventDtos(List<Event> events) {
        User currentUser = userService.getCurrentUserOrNullIfAnonymous();
        return events.stream()
                .map(event -> toEventDto(event, currentUser))
                .toList();
    }

    public Page<EventDto> toPageEventDtos(Page<Event> events) {
        User currentUser = userService.getCurrentUserOrNullIfAnonymous();
        return events.map(event -> toEventDto(event, currentUser));
    }

    public Page<EventDto> getAllEvents(EventFilter filter, Pageable pageable) {
        EventSpecification eventSpecification = getBuilderFromFilter(filter).build();
        Specification<Event> spec = eventSpecification.filterByCriteria();

        Page<Event> events = findAllEvents(spec, pageable);
        setDistanceToEvents(eventSpecification.getUserLocation(), events);

        return toPageEventDtos(events);
    }

    public void setDistanceToEvents(String location, Page<Event> events) {
        if (location != null && !location.isBlank()) {
            LatLngDto latLng = GeometryUtil.stringToLatLng(location);
            Map<Long, Double> distances =
                    eventRepo.findEventsWithDistance(
                                    events.get().map(Event::getId).toList(),
                                    latLng.lon(), latLng.lat())
                            .stream()
                            .collect(Collectors.toMap(EventWithDistanceDto::getEventId, EventWithDistanceDto::getDistance));
            events.forEach(event -> event.setDistanceToUser(distances.get(event.getId())));
        }
    }

    public void setDistanceToEvent(String location, Event event) {
        if (location != null && !location.isBlank()) {
            LatLngDto latLng = GeometryUtil.stringToLatLng(location);

            var distance = eventRepo.findEventWithDistance(event.getId(), latLng.lon(), latLng.lat());
            event.setDistanceToUser(distance.getDistance());
        }
    }

    public Page<Event> findAllEvents(Specification<Event> spec, Pageable pageable) {
        return eventRepo.findAll(spec, pageable);
    }

    public EventDto getNonActiveEventById(Long eventId) {
        return toEventDto(findNonActiveEventById(eventId));
    }

    public Event findNonActiveEventById(Long eventId) {
        return eventRepo.findByIdAndIsActiveFalse(eventId)
                .orElseThrow(() -> new EntityNotFoundException("The archived event id: " +
                        eventId + " is not found"));
    }

    @Transactional
    public EventDto updateEvent(Long authUserId, Long eventId, EventPatchDto dto) {
        Event event = findActiveEventById(eventId);
        userService.compareUsersIds(event.getOwner().getId(), authUserId);

        eventMapper.updateEntity(dto, event);
        setEventType(dto.getType(), event);
        setEventPhotos(dto.getPhoto(), event);
        setEventLanguages(dto.getLanguage(), event);
        setEventTags(dto.getTags(), event);

        eventRepo.saveAndFlush(event);
        entityManager.refresh(event);

        return getActiveEventById(eventId);
    }

    public void setEventTags(List<TagDto> tagsDtos, Event event) {
        Set<Tag> newTags = new HashSet<>();
        if (tagsDtos != null && !tagsDtos.isEmpty()) {
            for (TagDto tagDto : tagsDtos) {
                Tag tag = tagService.findById(tagDto.getId());
                newTags.add(tag);
            }
            event.getTags().removeIf(tag -> !newTags.contains(tag));
            event.getTags().addAll(newTags);
        } else {
            event.setTags(newTags);
        }
    }

    public void setEventLanguages(List<LanguageDto> langs, Event event) {
        Set<Language> newLangs = new HashSet<>();
        if (langs != null && !langs.isEmpty()) {
            for (LanguageDto langDto : langs) {
                Language language = languageService.findById(langDto.getId());
                newLangs.add(language);
            }
            event.getLanguage().removeIf(language -> !newLangs.contains(language));
            event.getLanguage().addAll(newLangs);
        } else {
            event.setLanguage(newLangs);
        }
    }

    public void setEventPhotos(List<String> photos, Event event) {
        if (photos != null && !photos.isEmpty()) {
            List<File> dtoFiles = new ArrayList<>();
            for (String url : photos) {
                String fileName = fileService.getNameFromUrl(url);
                dtoFiles.add(fileService.getByFileName(fileName));
            }
            event.setPhoto(dtoFiles);
        } else {
            event.setPhoto(new ArrayList<>());
        }
    }

    public void setEventType(Short id, Event event) {
        if (id != null) {
            if (id.compareTo(event.getType().getId()) != 0) {
                event.setType(typeService.findTypeById(id));
            }
        }
    }

    @Transactional
    public void deleteEvent(Long authUserId, Long eventId) {
        Event event = findActiveEventById(eventId);
        userService.compareUsersIds(event.getOwner().getId(), authUserId);
        event.setActive(false);
        event.setParticipants(new HashSet<>());
        event.setFavouriteUsers(new HashSet<>());
        eventRepo.saveAndFlush(event);
    }

    public EventSpecification.EventSpecificationBuilder getBuilderFromFilter(EventFilter filter) {
        return EventSpecification.builder()
                .isActive(filter.isActive())
                .isPublic(filter.getIsPublic())
                .name(filter.getName())
                .userLocation(filter.getUserLocation())
                .dateStartMoreThan(filter.getDateStartMoreThan())
                .dateStartLessThan(filter.getDateStartLessThan());
    }

    public Page<EventDto> getAllEventsByOwnerId(Long ownerId,
                                                EventFilter filter,
                                                Pageable pageable) {
        EventSpecification eventSpecification = getBuilderFromFilter(filter)
                .ownerId(ownerId)
                .build();
        Specification<Event> spec = eventSpecification.filterByCriteria();

        return toPageEventDtos(findAllEvents(spec, pageable));
    }

    public Page<EventDto> getAllEventsByFavouriteUserId(Long favouriteUserId,
                                                        EventFilter filter,
                                                        Pageable pageable) {
        EventSpecification eventSpecification = getBuilderFromFilter(filter)
                .favourite(userService.findUserById(favouriteUserId))
                .build();
        Specification<Event> spec = eventSpecification.filterByCriteria();

        return toPageEventDtos(findAllEvents(spec, pageable));
    }

    public Page<EventDto> getAllEventsByParticipantId(Long participantId,
                                                      EventFilter filter,
                                                    Pageable pageable) {
        EventSpecification eventSpecification = getBuilderFromFilter(filter)
                .participant(userService.findUserById(participantId))
                .build();
        Specification<Event> spec = eventSpecification.filterByCriteria();

        return toPageEventDtos(findAllEvents(spec, pageable));
    }

    public EventDto getEventById(Long eventId, String location) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                                "event with id " + eventId + " not found"));

        setDistanceToEvent(location, event);
        return toEventDto(event);
    }
}
