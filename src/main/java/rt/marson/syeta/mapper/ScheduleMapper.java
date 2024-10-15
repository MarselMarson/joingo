package rt.marson.syeta.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.Schedule;
import rt.marson.syeta.dto.ScheduleDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper {
    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    ScheduleDto toDto(Schedule type);

    Schedule toEntity(ScheduleDto userDto);
}
