package rt.marson.syeta.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.dto.user.UserDto;
import rt.marson.syeta.dto.user.UserForEventGetDto;
import rt.marson.syeta.dto.user.UserPatchDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "photoUrl", source = "photo.url")
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    @Mapping(target = "photoUrl", source = "photo.url")
    @Mapping(target = "createdDate", source = "createdAt")
    UserForEventGetDto toEventUserDto(User user);

    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    void updateEntity(UserPatchDto dto, @MappingTarget User entity);

    default List<UserDto> toUserDots(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }
    default List<UserForEventGetDto> toEventUserDots(List<User> users) {
        return users.stream()
                .map(this::toEventUserDto)
                .toList();
    }
}
