package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.saleservicebot.controller.abs.ProductController;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;
import uz.mediasolutions.saleservicebot.payload.ProductResDTO;
import uz.mediasolutions.saleservicebot.service.abs.ProductService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;

    @Override
    public ApiResult<Page<ProductResDTO>> getAll(int page, int size, String name) {
        return productService.getAll(page, size, name);
    }

    @Override
    public ApiResult<Page<ProductResDTO>> getAllByCategory(UUID cId, int page, int size, String name) {
        return productService.getAllByCategory(cId, page, size, name);
    }

    @Override
    public ApiResult<ProductResDTO> getById(UUID id) {
        return productService.getById(id);
    }

    @Override
    public ApiResult<?> add(ProductDTO dto) {
        return productService.add(dto);
    }

    @Override
    public ApiResult<?> edit(UUID id, ProductDTO dto) {
        return productService.edit(id, dto);
    }

    @Override
    public ApiResult<?> delete(UUID id) {
        return productService.delete(id);
    }

}
