package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(ProductController.PRODUCT)
public interface ProductController {

    String PRODUCT = Rest.BASE_PATH + "product/";
    String GET_ALL = "get-all";
    String GET_BY_CATEGORY_PAGE = "get-by-cat/{cId}";
    String GET_BY_ID = "get-by-id/{id}";
    String ADD = "add";
    String EDIT = "edit/{id}";
    String DELETE = "delete/{id}";

    @GetMapping(GET_ALL)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Page<ProductDTO>> getAll(@RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                       @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size,
                                       @RequestParam(defaultValue = "null") String name);

    @GetMapping(GET_BY_CATEGORY_PAGE)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Page<ProductDTO>> getAllByCategory(@PathVariable UUID cId,
                                                 @RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                                 @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size,
                                                 @RequestParam(defaultValue = "null") String name);

    @GetMapping(GET_BY_ID)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<ProductDTO> getById(@PathVariable UUID id);

    @PostMapping(ADD)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> add(@RequestBody @Valid ProductDTO dto);

    @PutMapping(EDIT)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> edit(@PathVariable UUID id, @RequestBody @Valid ProductDTO dto);

    @DeleteMapping(DELETE)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> delete(@PathVariable UUID id);

}
