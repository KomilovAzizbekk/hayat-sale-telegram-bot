package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.saleservicebot.controller.abs.TgUserController;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.TokenDTO;
import uz.mediasolutions.saleservicebot.payload.UserDTO;
import uz.mediasolutions.saleservicebot.service.abs.TgUserService;

@RestController
@RequiredArgsConstructor
public class TgUserControllerImpl implements TgUserController {

    private final TgUserService userService;

    @Override
    public ApiResult<Page<UserDTO>> getAllPageable(int page, int size, String search) {
        return userService.getAll(page, size, search);
    }

    @Override
    public ApiResult<UserDTO> getById(Long id) {
        return userService.getById(id);
    }

    @Override
    public ApiResult<?> blockUser(Long id) {
        return userService.block(id);
    }

    @Override
    public ApiResult<?> unblockUser(Long id) {
        return userService.unblockUser(id);
    }
}
