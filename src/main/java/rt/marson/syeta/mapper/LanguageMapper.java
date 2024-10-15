package rt.marson.syeta.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.Language;
import rt.marson.syeta.dto.LanguageDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LanguageMapper {
    LanguageMapper INSTANCE = Mappers.getMapper(LanguageMapper.class);

    LanguageDto toDto(Language lang);

    Language toEntity(LanguageDto dto);

    default List<LanguageDto> toLanguageDtos(List<Language> languages) {
        return languages.stream()
                .map(this::toDto)
                .toList();
    }
}
