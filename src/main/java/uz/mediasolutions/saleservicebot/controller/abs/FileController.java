package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

@RequestMapping(FileController.FILE)
public interface FileController {

    String FILE = Rest.BASE_PATH + "files/";
    String GET = "get/{id}";
    String UPLOAD = "upload";


    @GetMapping(GET)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ResponseEntity<byte[]> get(@PathVariable Long id);

    @PostMapping(UPLOAD)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> uploadFile(@RequestParam("file")MultipartFile file);

}
