package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.entity.Category;
import uz.mediasolutions.saleservicebot.entity.Product;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.mapper.CategoryMapper;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;
import uz.mediasolutions.saleservicebot.repository.CategoryRepository;
import uz.mediasolutions.saleservicebot.repository.ProductRepository;
import uz.mediasolutions.saleservicebot.service.abs.CategoryService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final ProductRepository productRepository;

    @Override
    public ApiResult<Page<CategoryDTO>> getAll(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size);
        if (!name.equals("null")) {
            Page<Category> categories = categoryRepository.
                    findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseOrderByNumberAsc(pageable, name, name);
            Page<CategoryDTO> map = categories.map(categoryMapper::toDTO);
            return ApiResult.success(map);
        }
        Page<Category> categories = categoryRepository.findAllByOrderByNumberAsc(pageable);
        Page<CategoryDTO> map = categories.map(categoryMapper::toDTO);
        return ApiResult.success(map);
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
            categoryRepository.save(toEntity(categoryDTO));
            return ApiResult.success("SAVED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> edit(UUID id, CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNumber(categoryDTO.getNumber()) &&
                !categoryRepository.existsByNumberAndId(categoryDTO.getNumber(), id))
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
            List<Product> all = productRepository.findAllByCategoryIdAndDeletedIsFalse(id);
            productRepository.deleteAll(all);
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow("CANNOT DELETE", HttpStatus.CONFLICT);
        }
        return ApiResult.success("DELETED SUCCESSFULLY");
    }

    private Category toEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .nameUz(categoryDTO.getNameUz().trim())
                .nameRu(categoryDTO.getNameRu().trim())
                .number(categoryDTO.getNumber())
                .build();
    }
}
