package rt.marson.syeta.mapper;


import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.Event;
import rt.marson.syeta.entity.File;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.dto.event.EventCreateDto;
import rt.marson.syeta.dto.event.EventDto;
import rt.marson.syeta.dto.event.EventPatchDto;
import rt.marson.syeta.dto.google.places.LatLngDto;
import rt.marson.syeta.dto.user.UserForEventGetDto;
import rt.marson.syeta.util.GeometryUtil;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "locationCity", source = "city")
    @Mapping(target = "locationState", source = "state")
    @Mapping(target = "locationCountry", source = "country")
    @Mapping(target = "locationFullAddress", source = "fullAddress")
    @Mapping(target = "endDate", source = "finishDate")
    @Mapping(target = "createdDate", source = "createdAt")
    @Mapping(target = "isPublic", source = "publicEvent")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "inFavourite", constant = "false")
    @Mapping(target = "isParticipate", constant = "false")
    @Mapping(target = "participantsCount", ignore = true)
    @Mapping(target = "photo", source = "photo", qualifiedByName = "photosToUrls")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "toOwner")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "toParticipants")
    @Mapping(target = "confirmationParticipants", source = "confirmationParticipants", qualifiedByName = "toConfirmationParticipants")
    EventDto toDto(Event event);

    @Named("photosToUrls")
    default List<String> photosToUrls(List<File> photo) {
        return photo.stream()
                .map(File::getUrl)
                .toList();
    }

    @Named("toOwner")
    default UserForEventGetDto toOwner(User owner) {
        return UserMapper.INSTANCE.toEventUserDto(owner);
    }

    @Named("toParticipants")
    default List<UserForEventGetDto> toParticipants(Set<User> participants) {
        return participants.stream()
                .map(UserMapper.INSTANCE::toEventUserDto)
                .toList();
    }

    @Named("toConfirmationParticipants")
    default List<UserForEventGetDto> toConfirmationParticipants(Set<User> confirmationParticipants) {
        return confirmationParticipants.stream()
                .map(UserMapper.INSTANCE::toEventUserDto)
                .toList();
    }

    @AfterMapping
    default void calculateParticipantsCount(Event event, @MappingTarget EventDto.EventDtoBuilder builder) {
        builder.participantsCount(event.getParticipants().size());
    }

    @Mapping(source = "locationCity", target = "city")
    @Mapping(source = "locationState", target = "state")
    @Mapping(source = "locationCountry", target = "country")
    @Mapping(source = "locationFullAddress", target = "fullAddress")
    @Mapping(source = "endDate", target = "finishDate")
    @Mapping(source = "isPublic", target = "publicEvent")
    @Mapping(source = "type", target = "type.id")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "location", source = "locationLatLng", qualifiedByName = "stringToPoint")
    Event createEntity(EventCreateDto eventCreateDto);

    @Mapping(source = "locationCity", target = "city")
    @Mapping(source = "locationState", target = "state")
    @Mapping(source = "locationCountry", target = "country")
    @Mapping(source = "locationFullAddress", target = "fullAddress")
    @Mapping(source = "endDate", target = "finishDate")
    @Mapping(source = "isPublic", target = "publicEvent")
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "location", source = "locationLatLng", qualifiedByName = "stringToPoint")
    void updateEntity(EventPatchDto dto, @MappingTarget Event entity);

    @Named("stringToPoint")
    default Point stringToPoint(String locationLatLng) throws ParseException {
        LatLngDto latLngDto = GeometryUtil.stringToLatLng(locationLatLng);
        return GeometryUtil.parseLocation(latLngDto.lon(), latLngDto.lat());
    }
}
