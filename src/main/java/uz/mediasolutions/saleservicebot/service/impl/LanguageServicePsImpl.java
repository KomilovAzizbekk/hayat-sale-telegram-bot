package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.entity.LanguagePs;
import uz.mediasolutions.saleservicebot.entity.LanguageSourcePs;
import uz.mediasolutions.saleservicebot.enums.LanguageEnum;
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
        Optional<LanguagePs> byId = languageRepository.findById(dto.getId());
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
                    } else {
                        languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Uz", dto.getTextUz()));
                    }
                    if (allByIdId.get(i).getLanguage().equals("Ru")) {
                        LanguageSourcePs ru = allByIdId.get(i);
                        ru.setTranslation(dto.getTextRu() != null ? dto.getTextRu() : ru.getTranslation());
                        ru.setLanguage("Ru");
                        languageSourceRepositoryPs.save(ru);
                    } else {
                        languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Ru", dto.getTextRu()));
                    }
                }
            } else {
                languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Uz", dto.getTextUz()));
                languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Ru", dto.getTextRu()));
            }
        }
        return ApiResult.success("SAVED SUCCESSFULLY");
    }

    @Override
    public ApiResult<Page<LanguagePs>> getAllPaginated(int page, int size, String key) {
        Pageable pageable = PageRequest.of(page, size);
        if (key.equals("null")) {
            return ApiResult.success(languageRepository.findAll(pageable));
        }
        return ApiResult.success(languageRepository.findAllByKeyContainingIgnoreCase(pageable, key));
    }

    @Override
    public ApiResult<Map<String, String>> getAllByLanguage(String language) {
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
        return ApiResult.success(languageSourceMap);
    }

}

