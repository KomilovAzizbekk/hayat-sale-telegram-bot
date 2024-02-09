package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;

public interface MarketService {
    ApiResult<Page<MarketDTO>> getAll(int page, int size, String name);

    ApiResult<MarketDTO> getById(Long id);

    ApiResult<?> add(MarketDTO dto);

    ApiResult<?> edit(Long id, MarketDTO dto);

    ApiResult<?> delete(Long id);
}
