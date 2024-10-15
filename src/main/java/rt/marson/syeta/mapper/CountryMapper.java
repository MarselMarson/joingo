package rt.marson.syeta.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.Country;
import rt.marson.syeta.dto.CountryDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {
    CountryMapper INSTANCE = Mappers.getMapper(CountryMapper.class);

    CountryDto toDto(Country country);
    Country toEntity(CountryDto countryDto);

    default List<CountryDto> toUserDots(List<Country> countries) {
        return countries.stream()
                .map(this::toDto)
                .toList();
    }
}
