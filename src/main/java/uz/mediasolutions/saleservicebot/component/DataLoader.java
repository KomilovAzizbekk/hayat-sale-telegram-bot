package uz.mediasolutions.saleservicebot.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.mediasolutions.saleservicebot.entity.*;
import uz.mediasolutions.saleservicebot.enums.RoleName;
import uz.mediasolutions.saleservicebot.enums.StatusName;
import uz.mediasolutions.saleservicebot.repository.*;
import uz.mediasolutions.saleservicebot.service.TgService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ApplicationContext applicationContext;
    private final MarketRepository marketRepository;
    private final LanguageRepositoryPs languageRepositoryPs;
    private final LanguageSourceRepositoryPs languageSourceRepositoryPs;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StatusRepository statusRepository;

    @Value("${spring.sql.init.mode}")
    private String mode;

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            TgService tgService = applicationContext.getBean(TgService.class);
            telegramBotsApi.registerBot(tgService);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

        if (mode.equals("always")) {
            addMarket();
            addLangValues();
            addRole();
            addAdmin();
            addStatus();
        }

    }

    public void addStatus() {
        Status pending = Status.builder().name(StatusName.PENDING).build();
        Status accepted = Status.builder().name(StatusName.ACCEPTED).build();
        Status rejected = Status.builder().name(StatusName.REJECTED).build();
        Status delivered = Status.builder().name(StatusName.DELIVERED).build();
        statusRepository.saveAll(List.of(pending, accepted, rejected, delivered));
    }

    public void addRole() {
        Role admin = Role.builder()
                .name(RoleName.ROLE_ADMIN)
                .build();
        roleRepository.save(admin);
    }

    public void addAdmin() {
        User admin = User.builder()
                .role(roleRepository.findByName(RoleName.ROLE_ADMIN))
                .username("admin")
                .password(passwordEncoder.encode("Qwerty123@"))
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .build();
        userRepository.save(admin);
    }

    public void addMarket() {
        Market кўйликБозори = Market.builder()
                .nameUz("Кўйлик бозори")
                .build();
        Market жомъеБозори = Market.builder()
                .nameUz("Жомъе бозори")
                .build();
        Market паркентскийБозори = Market.builder()
                .nameUz("Паркентский бозори")
                .build();
        marketRepository.saveAll(List.of(кўйликБозори, жомъеБозори, паркентскийБозори));
    }

    public void addLangValues() {
        LanguagePs chooseLang = LanguagePs.builder().key("lang.same.for.2.lang").primaryLang("Uz").build();
        LanguageSourcePs chooseLangUz = LanguageSourcePs.builder().languagePs(chooseLang)
                .language("Uz")
                .translation("Хизмат тилини танланг/Выберите язык обслуживания")
                .build();

        LanguagePs uzbek = LanguagePs.builder().key("uzbek").primaryLang("Uz").build();
        LanguageSourcePs uzbekUz = LanguageSourcePs.builder().languagePs(uzbek)
                .language("Uz")
                .translation("\uD83C\uDDFA\uD83C\uDDFF Узбекча")
                .build();

        LanguagePs russian = LanguagePs.builder().key("russian").primaryLang("Uz").build();
        LanguageSourcePs russianUz = LanguageSourcePs.builder().languagePs(russian)
                .language("Uz")
                .translation("\uD83C\uDDF7\uD83C\uDDFA Русский")
                .build();

        LanguagePs enterName = LanguagePs.builder().key("enter.name").primaryLang("Uz").build();
        LanguageSourcePs enterNameUz = LanguageSourcePs.builder().languagePs(enterName)
                .language("Uz")
                .translation("Исмингизни киритинг:")
                .build();

        LanguagePs enterPhoneNumber = LanguagePs.builder().key("enter.phone.number").primaryLang("Uz").build();
        LanguageSourcePs enterPhoneNumberUz = LanguageSourcePs.builder().languagePs(enterPhoneNumber)
                .language("Uz")
                .translation("Телефон рақамингизни +998XXYYYYYYY кўринишида бизга юборинг еки қуйидаги тугма ўрқали биз билан улашинг.")
                .build();

        LanguagePs sharePhoneNumber = LanguagePs.builder().key("share.phone.number").primaryLang("Uz").build();
        LanguageSourcePs sharePhoneNumberUz = LanguageSourcePs.builder().languagePs(sharePhoneNumber)
                .language("Uz")
                .translation("\uD83D\uDCDEТелефон рақамини юбориш")
                .build();

        LanguagePs chooseMarket = LanguagePs.builder().key("choose.market").primaryLang("Uz").build();
        LanguageSourcePs chooseMarketUz = LanguageSourcePs.builder().languagePs(chooseMarket)
                .language("Uz")
                .translation("Дўконингиз жойлашган бозорни танланг.")
                .build();

        languageRepositoryPs.saveAll(List.of(chooseLang, uzbek, russian, enterName,
                enterPhoneNumber, sharePhoneNumber, chooseMarket));
        languageSourceRepositoryPs.saveAll(List.of(chooseLangUz, uzbekUz, russianUz,
                enterNameUz, enterPhoneNumberUz, sharePhoneNumberUz, chooseMarketUz));
    }
}
