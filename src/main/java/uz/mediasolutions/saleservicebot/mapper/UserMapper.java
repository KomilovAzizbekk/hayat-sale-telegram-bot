package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.entity.TgUser;
import uz.mediasolutions.saleservicebot.payload.UserDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(TgUser user);

    List<UserDTO> toDTOList(List<TgUser> users);

}
