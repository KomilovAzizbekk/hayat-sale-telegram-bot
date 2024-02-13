package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.saleservicebot.controller.abs.LanguageController;
import uz.mediasolutions.saleservicebot.entity.LanguagePs;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.TranslateDto;
import uz.mediasolutions.saleservicebot.service.abs.LanguageServicePs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LanguageControllerImpl implements LanguageController {

    private final LanguageServicePs languageServicePs;

    @Override
    public ApiResult<Page<LanguagePs>> getAllPageable(int page, int size, String key) {
        return languageServicePs.getAllPaginated(page, size, key);
    }

    @Override
    public ApiResult<Map<String, String>> getAllByLang(String language) {
        return languageServicePs.getAllByLanguage(language);
    }

    @Override
    public ApiResult<?> createTranslation(TranslateDto dto) {
        return languageServicePs.createTranslation(dto);
    }

    @Override
    public ApiResult<?> createMainKey(List<TranslateDto> dtos) {
        return languageServicePs.createMainText(dtos);
    }

    @Override
    public ApiResult<?> createKey(HashMap<String, String> dto) {
        return languageServicePs.createKey(dto);
    }
}
