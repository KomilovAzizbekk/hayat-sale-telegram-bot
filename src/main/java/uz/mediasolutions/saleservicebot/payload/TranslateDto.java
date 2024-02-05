package uz.mediasolutions.saleservicebot.payload;

import lombok.Data;
import uz.mediasolutions.saleservicebot.enums.LanguageEnum;

import java.util.HashMap;

@Data
public class TranslateDto {

    private Long id;

    private String key;

    private HashMap<LanguageEnum, String> translations;
}