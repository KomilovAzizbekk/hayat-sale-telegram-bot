package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;
import uz.mediasolutions.saleservicebot.payload.ProductResDTO;

import java.util.UUID;

public interface ProductService {
    ApiResult<Page<ProductResDTO>> getAllByCategory(UUID cId, int page, int size, String name);

    ApiResult<Page<ProductResDTO>> getAll(int page, int size, String name);

    ApiResult<ProductResDTO> getById(UUID id);

    ApiResult<?> add(ProductDTO dto);

    ApiResult<?> edit(UUID id, ProductDTO dto);

    ApiResult<?> delete(UUID id);

}
