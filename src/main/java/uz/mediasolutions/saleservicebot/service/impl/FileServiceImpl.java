package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.mediasolutions.saleservicebot.entity.FileEntity;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.repository.FileRepository;
import uz.mediasolutions.saleservicebot.service.abs.FileService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public ApiResult<?> saveFile(MultipartFile file) {
        String contentType = file.getContentType();
        if ("application/pdf".equals(contentType)
                || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)
                || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            FileEntity fileEntity = new FileEntity();
            if (!fileRepository.findAll().isEmpty()) {
                fileEntity = fileRepository.findAll().get(0);
            }
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setType(file.getContentType());
            try {
                fileEntity.setData(file.getBytes());
                fileRepository.save(fileEntity);
            } catch (Exception e) {
                throw RestException.restThrow("FILE SAVING ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ApiResult.success("SAVED SUCCESSFULLY");
        } else {
            throw RestException.restThrow("FILE FORMAT SHOULD BE pdf, word, excel", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<byte[]> getFile(Long id) {
        FileEntity fileEntity = fileRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        if (fileEntity != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileEntity.getFileName());
            return new ResponseEntity<>(fileEntity.getData(), headers, HttpStatus.OK);
        } else {
            throw RestException.restThrow("FILE NOT FOUND", HttpStatus.BAD_REQUEST);
        }
    }

    public InputStream getFileContentAsStream() {
        List<FileEntity> fileEntities = fileRepository.findAll();
        FileEntity fileEntity = fileEntities.get(fileEntities.size() - 1);
        return (fileEntity != null) ? new ByteArrayInputStream(fileEntity.getData()) : null;
    }
}
