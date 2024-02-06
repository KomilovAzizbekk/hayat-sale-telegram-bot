package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;

import java.util.UUID;

public interface CategoryService {
    ApiResult<Page<CategoryDTO>> getAll(int page, int size);

    ApiResult<CategoryDTO> getById(UUID id);

    ApiResult<?> add(CategoryDTO categoryDTO);

    ApiResult<?> edit(UUID id, CategoryDTO categoryDTO);

    ApiResult<?> delete(UUID id);

}
