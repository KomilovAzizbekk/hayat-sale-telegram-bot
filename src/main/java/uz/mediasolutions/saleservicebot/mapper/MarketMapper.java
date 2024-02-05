package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.entity.Market;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;

@Mapper(componentModel = "spring")
public interface MarketMapper {

    Page<MarketDTO> toDTOPage(Page<Market> markets);

    MarketDTO toDTO(Market market);

    Market toEntity(MarketDTO marketDTO);

}
