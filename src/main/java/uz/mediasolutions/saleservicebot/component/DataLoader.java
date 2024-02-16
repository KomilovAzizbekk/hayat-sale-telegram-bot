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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

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
            addRole();
            addAdmin();
            addStatus();
            addUzLangValues();
            addRuLangValues();
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

//    public void addMarket() {
//        Market кўйликБозори = Market.builder()
//                .nameUz("Кўйлик бозори")
//                .build();
//
//        Market жомъеБозори = Market.builder()
//                .nameUz("Жомъе бозори")
//                .build();
//
//        Market паркентскийБозори = Market.builder()
//                .nameUz("Паркентский бозори")
//                .build();
//        marketRepository.saveAll(List.of(кўйликБозори, жомъеБозори, паркентскийБозори));
//    }

    public void addUzLangValues() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = DataLoader.class.getClassLoader()
                .getResourceAsStream("messages_uz.properties")) {
            properties.load(input);
        }
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            LanguagePs ps = LanguagePs.builder().primaryLang("Uz").key(key).build();
            LanguagePs save = languageRepositoryPs.save(ps);
            LanguageSourcePs sourcePs = LanguageSourcePs.builder()
                    .languagePs(save).language("Uz").translation(value).build();
            languageSourceRepositoryPs.save(sourcePs);
        }
    }

    public void addRuLangValues() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = DataLoader.class.getClassLoader()
                .getResourceAsStream("messages_ru.properties")) {
            properties.load(input);
        }
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            LanguagePs languagePs = languageRepositoryPs.findByKey(key);
            LanguageSourcePs sourcePs = LanguageSourcePs.builder()
                    .languagePs(languagePs).language("Ru").translation(value).build();
            languageSourceRepositoryPs.save(sourcePs);
        }
    }
}
