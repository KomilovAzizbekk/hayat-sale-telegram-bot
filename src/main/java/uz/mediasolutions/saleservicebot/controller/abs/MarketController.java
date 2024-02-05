package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.MarketDTO;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

@RequestMapping(MarketController.MARKET)
public interface MarketController {

    String MARKET = Rest.BASE_PATH + "market/";
    String GET_ALL = "get-all";
    String GET_BY_ID = "get-by-id/{id}";
    String ADD = "add";
    String EDIT = "edit/{id}";
    String DELETE = "delete/{id}";

    @GetMapping(GET_ALL)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Page<MarketDTO>> getAll(@RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                      @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size);


    @GetMapping(GET_BY_ID)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<MarketDTO> getById(@PathVariable Long id);

    @PostMapping(ADD)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> add(@RequestBody MarketDTO dto);

    @PutMapping(EDIT)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> edit(@PathVariable Long id, @RequestBody MarketDTO dto);

    @DeleteMapping(DELETE)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> delete(@PathVariable Long id);
}
