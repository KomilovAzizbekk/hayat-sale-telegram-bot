package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(CategoryController.CATEGORY)
public interface CategoryController {

    String CATEGORY = Rest.BASE_PATH + "category/";
    String GET_ALL_PAGE = "get-all";
    String GET_BY_ID = "get-by-id/{id}";
    String ADD = "add";
    String EDIT = "edit/{id}";
    String DELETE = "delete/{id}";


    @GetMapping(GET_ALL_PAGE)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Page<CategoryDTO>> getAllPage(@RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                            @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size,
                                            @RequestParam(defaultValue = "null") String name);

    @GetMapping(GET_BY_ID)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<CategoryDTO> getById(@PathVariable UUID id);

    @PostMapping(ADD)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> add(@RequestBody @Valid CategoryDTO dto);

    @PutMapping(EDIT)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> edit(@PathVariable UUID id, @RequestBody @Valid CategoryDTO dto);

    @DeleteMapping(DELETE)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> delete(@PathVariable UUID id);
}
