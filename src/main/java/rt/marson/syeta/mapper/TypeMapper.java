package rt.marson.syeta.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.Type;
import rt.marson.syeta.dto.TypeDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TypeMapper {
    TypeMapper INSTANCE = Mappers.getMapper(TypeMapper.class);

    TypeDto toDto(Type type);

    Type toEntity(TypeDto dto);

    default List<TypeDto> toTypeDtos(List<Type> types) {
        return types.stream()
                .map(this::toDto)
                .toList();
    }
}
