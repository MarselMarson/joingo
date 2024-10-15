package rt.marson.syeta.mapper.chat;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import rt.marson.syeta.entity.chat.ChatMessage;
import rt.marson.syeta.dto.chat.ChatMessageDto;
import rt.marson.syeta.util.DateUtil;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMessageMapper {
    ChatMessageMapper INSTANCE = Mappers.getMapper(ChatMessageMapper.class);

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "recipient.id", target = "chatPartnerId")
    @Mapping(target = "createdDate", source = "message", qualifiedByName = "localDateToString")
    ChatMessageDto toDto(ChatMessage message);

    @Named("localDateToString")
    default String localDateToString(ChatMessage message) {
        return DateUtil.offsetToString(message.getCreatedAt());
    }
}
