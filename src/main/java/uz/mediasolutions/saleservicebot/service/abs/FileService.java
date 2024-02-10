package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.web.multipart.MultipartFile;
import uz.mediasolutions.saleservicebot.manual.ApiResult;


public interface FileService {

    ApiResult<?> saveFile(MultipartFile file);

    ApiResult<byte[]> getFile(Long id);
}
