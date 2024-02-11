package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.UserDTO;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

@RequestMapping(TgUserController.USER)
public interface TgUserController {

    String USER = Rest.BASE_PATH + "user/";
    String GET_ME = "get-me";
    String GET = "get-all";
    String GET_BY_ID = "get-by-id/{id}";
    String BLOCK = "block/{id}";
    String UNBLOCK = "unblock/{id}";


    @GetMapping(GET)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Page<UserDTO>> getAllPageable(@RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                            @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size,
                                            @RequestParam(defaultValue = "null") String search);

    @GetMapping(GET_BY_ID)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<UserDTO> getById(@PathVariable Long id);

    @PostMapping(BLOCK)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> blockUser(@PathVariable Long id);

    @PostMapping(UNBLOCK)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> unblockUser(@PathVariable Long id);

}
