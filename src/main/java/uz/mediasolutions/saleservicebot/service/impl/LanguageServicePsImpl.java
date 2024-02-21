package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.entity.LanguagePs;
import uz.mediasolutions.saleservicebot.entity.LanguageSourcePs;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.TranslateDto;
import uz.mediasolutions.saleservicebot.repository.LanguageRepositoryPs;
import uz.mediasolutions.saleservicebot.repository.LanguageSourceRepositoryPs;
import uz.mediasolutions.saleservicebot.service.abs.LanguageServicePs;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LanguageServicePsImpl implements LanguageServicePs {

    private final LanguageRepositoryPs languageRepository;

    private final LanguageSourceRepositoryPs languageSourceRepositoryPs;

    @Value("${languages.primaryLang}")
    String primaryLang;


    @Override
    public ApiResult<?> createKey(HashMap<String, String> dto) {
        String newWord = "";
        for (Map.Entry<String, String> entry : dto.entrySet()) {
            newWord = entry.getValue();
        }
        if (!languageRepository.existsByKey(newWord)) {
            LanguagePs languagePs = LanguagePs.builder()
                    .key(newWord)
                    .primaryLang(primaryLang)
                    .build();
            languageRepository.save(languagePs);
            return ApiResult.success("CREATED SUCCESSFULLY");
        } else
            throw RestException.restThrow("KEY ALREADY EXISTS", HttpStatus.BAD_REQUEST);
    }


    @Override
    public ApiResult<?> createMainText(List<TranslateDto> dtos) {
        for (TranslateDto dto : dtos) {
            if (!languageRepository.existsByKey(dto.getKey())) {
                LanguagePs languagePs = LanguagePs.builder()
                        .key(dto.getKey())
                        .primaryLang(primaryLang)
                        .build();
                LanguagePs savedLanguagePs = languageRepository.save(languagePs);
                LanguageSourcePs uz = LanguageSourcePs.builder()
                        .languagePs(savedLanguagePs)
                        .language("Uz")
                        .translation(dto.getTextUz())
                        .build();
                LanguageSourcePs ru = LanguageSourcePs.builder()
                        .languagePs(savedLanguagePs)
                        .language("Ru")
                        .translation(dto.getTextRu())
                        .build();
                languageSourceRepositoryPs.saveAll(List.of(uz, ru));
            }
        }
        return ApiResult.success("CREATED SUCCESSFULLY");
    }

    @Override
    public ApiResult<?> createTranslation(TranslateDto dto) {
        Optional<LanguagePs> byId = languageRepository.findByIdAndKey(dto.getId(), dto.getKey());
        if (byId.isPresent()) {
            LanguagePs languagePs = byId.get();
            List<LanguageSourcePs> allByIdId = languageSourceRepositoryPs.findAllByLanguagePs_Id(languagePs.getId());
            if (!allByIdId.isEmpty()) {
                for (int i = 0; i < allByIdId.size(); i++) {
                    if (allByIdId.get(i).getLanguage().equals("Uz")) {
                        LanguageSourcePs uz = allByIdId.get(i);
                        uz.setTranslation(dto.getTextUz() != null ? dto.getTextUz() : uz.getTranslation());
                        uz.setLanguage("Uz");
                        languageSourceRepositoryPs.save(uz);
                    }

                    if (allByIdId.get(i).getLanguage().equals("Ru")) {
                        LanguageSourcePs ru = allByIdId.get(i);
                        ru.setTranslation(dto.getTextRu() != null ? dto.getTextRu() : ru.getTranslation());
                        ru.setLanguage("Ru");
                        languageSourceRepositoryPs.save(ru);
                    }
                    if (!languageSourceRepositoryPs.existsByLanguageAndLanguagePsId("Uz", languagePs.getId()))
                        languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Uz", dto.getTextUz()));

                    if (!languageSourceRepositoryPs.existsByLanguageAndLanguagePsId("Ru", languagePs.getId()))
                        languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Ru", dto.getTextRu()));
                }
            } else {
                languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Uz", dto.getTextUz()));
                languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Ru", dto.getTextRu()));
            }
            return ApiResult.success("SAVED SUCCESSFULLY");
        } else
            throw RestException.restThrow("ID NOT FOUND OR INCORRECT KEY FOR ID", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ApiResult<Page<LanguagePs>> getAllPaginated(int page, int size, String key) {
        Pageable pageable = PageRequest.of(page, size);
        if (key.equals("null")) {
            return ApiResult.success(languageRepository.findAll(pageable));
        }
        return ApiResult.success(languageRepository.findAllByKeyAndTranslations(pageable, key));
    }

    @Override
    public ResponseEntity<Map<String, String>> getAllByLanguage(String language) {
        List<LanguagePs> allByLanguage = languageRepository.findAll();
        Map<String, String> languageSourceMap = new HashMap<>();
        if (!allByLanguage.isEmpty()) {
            for (LanguagePs languagePs : allByLanguage) {
                for (LanguageSourcePs languageSourceP : languagePs.getLanguageSourcePs()) {
                    if (languageSourceP.getTranslation() != null && languageSourceP.getLanguage().equals(language)) {
                        languageSourceMap.put(languagePs.getKey(), languageSourceP.getTranslation());
                    }
//                    else if (languageSourceP.getTranslation() ==null ) {
//                            languageSourceMap.put(languagePs.getText() , null);
//                    }
                }
            }
        }
        return new ResponseEntity<>(languageSourceMap, HttpStatus.OK);
    }

}

