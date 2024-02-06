package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;

import java.util.UUID;

public interface ProductService {
    ApiResult<Page<ProductDTO>> getAllByCategory(UUID cId, int page, int size);

    ApiResult<ProductDTO> getById(UUID id);

    ApiResult<?> add(ProductDTO dto);

    ApiResult<?> edit(UUID id, ProductDTO dto);

    ApiResult<?> delete(UUID id);

}
