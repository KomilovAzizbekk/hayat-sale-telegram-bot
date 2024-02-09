package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.saleservicebot.controller.abs.MarketController;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;
import uz.mediasolutions.saleservicebot.service.abs.MarketService;

@RestController
@RequiredArgsConstructor
public class MarketControllerImpl implements MarketController {

    private final MarketService marketService;

    @Override
    public ApiResult<Page<MarketDTO>> getAll(int page, int size, String name) {
        return marketService.getAll(page, size, name);
    }

    @Override
    public ApiResult<MarketDTO> getById(Long id) {
        return marketService.getById(id);
    }

    @Override
    public ApiResult<?> add(MarketDTO dto) {
        return marketService.add(dto);
    }

    @Override
    public ApiResult<?> edit(Long id, MarketDTO dto) {
        return marketService.edit(id, dto);
    }

    @Override
    public ApiResult<?> delete(Long id) {
        return marketService.delete(id);
    }
}
