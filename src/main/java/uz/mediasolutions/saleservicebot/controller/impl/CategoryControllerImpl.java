package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.saleservicebot.controller.abs.CategoryController;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;
import uz.mediasolutions.saleservicebot.service.abs.CategoryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {

    private final CategoryService categoryService;

    @Override
    public ApiResult<Page<CategoryDTO>> getAllPage(int page, int size) {
        return categoryService.getAll(page, size);
    }

    @Override
    public ApiResult<CategoryDTO> getById(UUID id) {
        return categoryService.getById(id);
    }

    @Override
    public ApiResult<?> add(CategoryDTO dto) {
        return categoryService.add(dto);
    }

    @Override
    public ApiResult<?> edit(UUID id, CategoryDTO dto) {
        return categoryService.edit(id, dto);
    }

    @Override
    public ApiResult<?> delete(UUID id) {
        return categoryService.delete(id);
    }
}
