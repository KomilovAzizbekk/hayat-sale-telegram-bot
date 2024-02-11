package uz.mediasolutions.saleservicebot.service.abs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.mediasolutions.saleservicebot.manual.ApiResult;


public interface FileService {

    ApiResult<?> saveFile(MultipartFile file);

    ResponseEntity<byte[]> getFile(Long id);
}
