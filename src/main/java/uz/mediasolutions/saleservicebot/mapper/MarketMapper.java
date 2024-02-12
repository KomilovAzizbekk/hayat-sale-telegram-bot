package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uz.mediasolutions.saleservicebot.entity.Market;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarketMapper {

    MarketMapper INSTANCE = Mappers.getMapper(MarketMapper.class);

    List<MarketDTO> toDTOList(List<Market> markets);

    MarketDTO toDTO(Market market);

    Market toEntity(MarketDTO marketDTO);

}
