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
import uz.mediasolutions.saleservicebot.mapper.ProductMapper;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;
import uz.mediasolutions.saleservicebot.payload.ProductResDTO;
import uz.mediasolutions.saleservicebot.repository.CategoryRepository;
import uz.mediasolutions.saleservicebot.repository.ProductRepository;
import uz.mediasolutions.saleservicebot.service.abs.ProductService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public ApiResult<Page<ProductResDTO>> getAllByCategory(UUID cId, int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size);
        if (!name.equals("null")) {
            Page<Product> products = productRepository
                    .findAllByCategoryIdAndNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseAndDeletedIsFalseOrderByNumberAsc(pageable, cId, name, name);
            Page<ProductResDTO> map = products.map(productMapper::toResDTO);
            return ApiResult.success(map);
        }
        Page<Product> products = productRepository.findAllByCategoryIdAndDeletedIsFalseOrderByCreatedAtDesc(pageable, cId);
        Page<ProductResDTO> map = products.map(productMapper::toResDTO);
        return ApiResult.success(map);
    }

    @Override
    public ApiResult<Page<ProductResDTO>> getAll(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size);
        if (!name.equals("null")) {
            Page<Product> products = productRepository
                    .findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseAndDeletedIsFalseOrderByNumberAsc(pageable, name, name);
            Page<ProductResDTO> map = products.map(productMapper::toResDTO);
            return ApiResult.success(map);
        }
        Page<Product> products = productRepository.findAllByDeletedIsFalseOrderByNumberAsc(pageable);
        Page<ProductResDTO> map = products.map(productMapper::toResDTO);
        return ApiResult.success(map);
    }

    @Override
    public ApiResult<ProductResDTO> getById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        return ApiResult.success(productMapper.toResDTO(product));
    }

    @Override
    public ApiResult<?> add(ProductDTO dto) {
        if (productRepository.existsByNumberAndCategoryIdAndDeletedIsFalse(dto.getNumber(), dto.getCategoryId()))
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        else {
            Product product = toEntity(dto);
            productRepository.save(product);
            return ApiResult.success("ADDED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> edit(UUID id, ProductDTO dto) {
        if (productRepository.existsByNumberAndCategoryIdAndDeletedIsFalse(dto.getNumber(), dto.getCategoryId()) &&
                !productRepository.existsByNumberAndCategoryIdAndIdAndDeletedIsFalse(dto.getNumber(), dto.getCategoryId(), id))
            throw RestException.restThrow("NUMBER MUST ME UNIQUE", HttpStatus.BAD_REQUEST);
        else {
            Product product = productRepository.findById(id).orElseThrow(
                    () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));

            Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(
                    () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));

            product.setNameUz(dto.getNameUz());
            product.setNameRu(dto.getNameRu());
            product.setNumber(dto.getNumber());
            product.setCategory(category);
            productRepository.save(product);
            return ApiResult.success("EDITED SUCCESSFULLY");
        }
    }

    @Override
    public ApiResult<?> delete(UUID id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow("CANNOT DELETE", HttpStatus.CONFLICT);
        }
        return ApiResult.success("DELETED SUCCESSFULLY");
    }


    private Product toEntity(ProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));

        return Product.builder()
                .nameUz(dto.getNameUz().trim())
                .nameRu(dto.getNameRu().trim())
                .number(dto.getNumber())
                .category(category)
                .build();
    }

}
