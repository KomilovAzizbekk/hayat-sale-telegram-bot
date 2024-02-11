package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.UserDTO;

public interface TgUserService {
    ApiResult<Page<UserDTO>> getAll(int page, int size, String search);

    ApiResult<UserDTO> getById(Long id);

    ApiResult<?> block(Long id);

    ApiResult<?> unblockUser(Long id);
}
