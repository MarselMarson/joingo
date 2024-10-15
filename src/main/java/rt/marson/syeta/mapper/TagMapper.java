package rt.marson.syeta.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.Tag;
import rt.marson.syeta.dto.TagDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagDto toDto(Tag tag);

    Tag toEntity(TagDto dto);

    default List<TagDto> toTagDtos(List<Tag> tags) {
        return tags.stream()
                .map(this::toDto)
                .toList();
    }
}
