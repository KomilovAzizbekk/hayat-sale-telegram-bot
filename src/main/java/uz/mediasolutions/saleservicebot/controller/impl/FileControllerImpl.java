package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.mediasolutions.saleservicebot.controller.abs.FileController;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.service.abs.FileService;

@RestController
@RequiredArgsConstructor
public class FileControllerImpl implements FileController {

    private final FileService fileService;

    @Override
    public ApiResult<byte[]> get(Long id) {
        return fileService.getFile(id);
    }

    @Override
    public ApiResult<?> uploadFile(MultipartFile file) {
        return fileService.saveFile(file);
    }
}
