package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.entity.TgUser;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.mapper.UserMapper;
import uz.mediasolutions.saleservicebot.payload.UserDTO;
import uz.mediasolutions.saleservicebot.repository.TgUserRepository;
import uz.mediasolutions.saleservicebot.service.abs.TgUserService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TgUserServiceImpl implements TgUserService {

    private final TgUserRepository tgUserRepository;
    private final UserMapper userMapper;

    @Override
    public ApiResult<Page<UserDTO>> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (!search.equals("null")) {
            Page<TgUser> users = tgUserRepository
                    .findAllByNameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase(pageable, search, search);
            Page<UserDTO> map = users.map(userMapper::toDTO);
            return ApiResult.success(map);
        }
        Page<TgUser> users = tgUserRepository.findAll(pageable);
        Page<UserDTO> map = users.map(userMapper::toDTO);
        return ApiResult.success(map);
    }

    @Override
    public ApiResult<UserDTO> getById(Long id) {
        TgUser user = tgUserRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        return ApiResult.success(userMapper.toDTO(user));
    }

    @Override
    public ApiResult<?> block(Long id) {
        TgUser user = tgUserRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        user.setBlocked(true);
        tgUserRepository.save(user);
        return ApiResult.success("BLOCKED SUCCESSFULLY");
    }

    @Override
    public ApiResult<?> unblockUser(Long id) {
        TgUser user = tgUserRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        user.setBlocked(false);
        tgUserRepository.save(user);
        return ApiResult.success("ACTIVATED SUCCESSFULLY");
    }
}
