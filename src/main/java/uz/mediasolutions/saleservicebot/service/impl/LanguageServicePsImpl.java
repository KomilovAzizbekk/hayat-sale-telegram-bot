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
    public ApiResult<?> createMainText(HashMap<String, String> dto) {
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
                LanguageSourcePs uz = allByIdId.get(0);
                uz.setTranslation(dto.getTranslations().get(LanguageEnum.Uz) != null ? dto.getTranslations().get(LanguageEnum.Uz) : uz.getTranslation());
                uz.setLanguage("Uz");

                LanguageSourcePs ru = allByIdId.get(1);
                ru.setTranslation(dto.getTranslations().get(LanguageEnum.Ru) != null ? dto.getTranslations().get(LanguageEnum.Ru) : ru.getTranslation());
                ru.setLanguage("Ru");

                languageSourceRepositoryPs.saveAll(allByIdId);
            } else {
                HashMap<LanguageEnum, String> translations = dto.getTranslations();

                languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Uz", translations.get(LanguageEnum.Uz) != null ? translations.get(LanguageEnum.Uz) : null));
                languageSourceRepositoryPs.save(new LanguageSourcePs(languagePs, "Ru", translations.get(LanguageEnum.Ru) != null ? translations.get(LanguageEnum.Ru) : null));
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

