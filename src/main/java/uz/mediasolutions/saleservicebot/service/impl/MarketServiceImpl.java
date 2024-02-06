package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.entity.Market;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.mapper.MarketMapper;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;
import uz.mediasolutions.saleservicebot.repository.MarketRepository;
import uz.mediasolutions.saleservicebot.service.abs.MarketService;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {

    private final MarketRepository marketRepository;
    private final MarketMapper marketMapper;

    @Override
    public ApiResult<Page<MarketDTO>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Market> markets = marketRepository.findAll(pageable);
        Page<MarketDTO> dtoPage = new PageImpl<>(marketMapper.toDTOPage(markets));
        return ApiResult.success(dtoPage);
    }

    @Override
    public ApiResult<MarketDTO> getById(Long id) {
        Market market = marketRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        return ApiResult.success(marketMapper.toDTO(market));
    }

    @Override
    public ApiResult<?> add(MarketDTO dto) {
        Market market = marketMapper.toEntity(dto);
        marketRepository.save(market);
        return ApiResult.success("SAVED SUCCESSFULLY");
    }

    @Override
    public ApiResult<?> edit(Long id, MarketDTO dto) {
        Market market = marketRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        market.setNameRu(dto.getNameRu());
        market.setNameUz(dto.getNameUz());
        marketRepository.save(market);
        return ApiResult.success("EDITED SUCCESSFULLY");
    }

    @Override
    public ApiResult<?> delete(Long id) {
        try {
            marketRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow("CANNOT DELETE", HttpStatus.CONFLICT);
        }
        return ApiResult.success("DELETED SUCCESSFULLY");
    }
}
