package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.entity.Category;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.mapper.CategoryMapper;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;
import uz.mediasolutions.saleservicebot.repository.CategoryRepository;
import uz.mediasolutions.saleservicebot.service.abs.CategoryService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public ApiResult<Page<CategoryDTO>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryDTO> dtoPage = new PageImpl<>(categoryMapper.toDTOPage(categories));
        return ApiResult.success(dtoPage);
    }

    @Override
    public ApiResult<CategoryDTO> getById(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        return ApiResult.success(categoryMapper.toDTO(category));
    }

    @Override
    public ApiResult<?> add(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNumber(categoryDTO.getNumber()))
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        else {
            categoryRepository.save(categoryMapper.toEntity(categoryDTO));
            return ApiResult.success("SAVED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> edit(UUID id, CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNumber(categoryDTO.getNumber()))
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        else {
            Category category = categoryRepository.findById(id).orElseThrow(
                    () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
            category.setNameUz(categoryDTO.getNameUz());
            category.setNameRu(category.getNameRu());
            category.setNumber(categoryDTO.getNumber());
            categoryRepository.save(category);
            return ApiResult.success("EDITED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> delete(UUID id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow("CANNOT DELETE", HttpStatus.CONFLICT);
        }
        return ApiResult.success("DELETED SUCCESSFULLY");
    }
}
