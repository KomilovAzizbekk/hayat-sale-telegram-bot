package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.mediasolutions.saleservicebot.entity.LanguagePs;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.TranslateDto;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

import java.util.HashMap;
import java.util.Map;

@RequestMapping(LanguageController.LANGUAGE)
public interface LanguageController {

    String LANGUAGE = Rest.BASE_PATH + "lang/";
    String ALL = "all";
    String ALL_BY_LANG = "by-lang";
    String CREATE = "create";
    String CREATE_MAIN_KEY = "create-main-key";

    @GetMapping(ALL)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Page<LanguagePs>> getAllPageable(@RequestParam(defaultValue = Rest.DEFAULT_PAGE_NUMBER) int page,
                                               @RequestParam(defaultValue = Rest.DEFAULT_PAGE_SIZE) int size,
                                               @RequestParam String key);

    @GetMapping(ALL_BY_LANG)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<Map<String, String>> getAllByLang(@RequestParam(defaultValue = "Uz") String lang);

    @PostMapping(CREATE)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> createTranslation(@RequestBody TranslateDto dto);

    @PostMapping(CREATE_MAIN_KEY)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    ApiResult<?> createMainKey(@RequestBody HashMap<String, String> dto);


}
