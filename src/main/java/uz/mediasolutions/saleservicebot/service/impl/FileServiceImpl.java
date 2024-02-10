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
            throw RestException.restThrow("FILE FORMAT SHOULD BE pdf, word, xlsx", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ApiResult<byte[]> getFile(Long id) {
        FileEntity fileEntity = fileRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.CONFLICT));
        if (fileEntity != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileEntity.getFileName()).build());
            return ApiResult.success(fileEntity.getData(), headers);
        } else {
            throw RestException.restThrow("NOT FOUND", HttpStatus.CONFLICT);
        }
    }

    public byte[] getFileData() {
        List<FileEntity> fileEntities = fileRepository.findAll();
        FileEntity fileEntity = fileEntities.get(fileEntities.size() - 1);
        return (fileEntity != null) ? fileEntity.getData() : null;
    }
}
