package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.entity.Market;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarketMapper {

    List<MarketDTO> toDTOList(List<Market> markets);

    MarketDTO toDTO(Market market);

    Market toEntity(MarketDTO marketDTO);

}
