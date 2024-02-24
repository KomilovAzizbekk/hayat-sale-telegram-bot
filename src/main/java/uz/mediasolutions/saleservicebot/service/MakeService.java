package uz.mediasolutions.saleservicebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.saleservicebot.entity.*;
import uz.mediasolutions.saleservicebot.enums.StatusName;
import uz.mediasolutions.saleservicebot.manual.BotState;
import uz.mediasolutions.saleservicebot.repository.*;
import uz.mediasolutions.saleservicebot.utills.constants.Message;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;


@Service
@RequiredArgsConstructor
public class MakeService {

    public String format;
    public Integer messageId;
    public final String CHANNEL_ID_APP = "-1002046346230";
    public final String CHANNEL_ID_SUG_COMP = "-1001998679932";
    public final String CHANNEL_ID_ORDER = "-1001997761469";
    public final String CHAT_ID_1 = "285710521";
    public final String CHAT_ID_2 = "6931160281";
    public final String CHAT_ID_3 = "1302908674";

    private final TgUserRepository tgUserRepository;
    private final FileRepository fileRepository;
    private final MarketRepository marketRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SuggestsComplaintsRepo suggestsComplaintsRepo;
    private final LanguageRepositoryPs languageRepository;
    private final BasketRepository basketRepository;
    private final ChosenProductRepository chosenProductRepository;
    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;

    //FOR USER STATE
    private final Map<String, BotState> userStates = new HashMap<>();

    public void setUserState(String chatId, BotState state) {
        userStates.put(chatId, state);
    }

    public BotState getUserState(String chatId) {
        return userStates.getOrDefault(chatId, BotState.START);
    }


    public String getUserLanguage(String chatId) {
        if (tgUserRepository.existsByChatId(chatId)) {
            TgUser tgUser = tgUserRepository.findByChatId(chatId);
            return tgUser.getLang();
        } else
            return UZ;
    }

    public List<String> numbersUpTo() {
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            numbers.add(i, String.valueOf(i + 1));
        }
        return numbers;
    }

    public String getChosenProductsNameAndCount(String chatId, String languageCode) {
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        chosenProducts.sort(Comparator.comparingLong(ChosenProduct::getId));
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < chosenProducts.size(); i++) {
            if (languageCode.equals("Ru")) {
                text.append(i + 1).append(") <b>")
                        .append(chosenProducts.get(i).getProduct().getNameRu())
                        .append("</b> x ").append(chosenProducts.get(i).getCount())
                        .append(" ").append(getMessage(Message.COUNT_X, languageCode))
                        .append("\n\n");
            } else {
                text.append(i + 1).append(") <b>")
                        .append(chosenProducts.get(i).getProduct().getNameUz())
                        .append("</b> x ").append(chosenProducts.get(i).getCount())
                        .append(" ").append(getMessage(Message.COUNT_X, languageCode))
                        .append("\n\n");
            }
        }
        return text.toString();
    }

    public String getChosenProductsNameAndCountForOrder(String chatId, String languageCode) {
        List<Order> orders = orderRepository.findAllByTgUserChatId(chatId);
        Order order = orders.get(orders.size() - 1);
        List<ChosenProduct> chosenProducts = order.getChosenProducts();
        chosenProducts.sort(Comparator.comparingLong(ChosenProduct::getId));
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < chosenProducts.size(); i++) {
            if (languageCode.equals("Ru")) {
                text.append(i + 1).append(") <b>")
                        .append(chosenProducts.get(i).getProduct().getNameRu())
                        .append("</b> x ").append(chosenProducts.get(i).getCount())
                        .append(" ").append(getMessage(Message.COUNT_X, languageCode))
                        .append("\n\n");
            } else {
                text.append(i + 1).append(") <b>")
                        .append(chosenProducts.get(i).getProduct().getNameUz())
                        .append("</b> x ").append(chosenProducts.get(i).getCount())
                        .append(" ").append(getMessage(Message.COUNT_X, languageCode))
                        .append("\n\n");
            }
        }
        return text.toString();
    }

    public List<String> getCategoryName(String languageCode) {
        List<Category> categories = categoryRepository.findAll();
        List<String> uz = new ArrayList<>();
        List<String> ru = new ArrayList<>();
        boolean isRu = languageCode.equals("Ru");
        for (Category category : categories) {
            if (isRu)
                ru.add(category.getNameRu());
            else
                uz.add(category.getNameUz());
        }
        if (isRu)
            return ru;
        else
            return uz;
    }

    public List<String> getProductName(String languageCode) {
        List<Product> products = productRepository.findAll();
        List<String> uz = new ArrayList<>();
        List<String> ru = new ArrayList<>();
        boolean isRu = languageCode.equals("Ru");
        for (Product product : products) {
            if (isRu)
                ru.add(product.getNameRu());
            else
                uz.add(product.getNameUz());
        }
        if (isRu)
            return ru;
        else
            return uz;
    }

    public String getOrderStatusName(String chatId) {
        String language = getUserLanguage(chatId);
        List<Order> orders = orderRepository.findAllByTgUserChatId(chatId);
        Order order = orders.get(orders.size() - 1);
        if (order.getStatus().getName().equals(StatusName.PENDING)) {
            return getMessage(Message.PENDING_ORDER, language);
        } else if (order.getStatus().getName().equals(StatusName.ACCEPTED)) {
            return getMessage(Message.ACCEPTED_ORDER, language);
        } else if (order.getStatus().getName().equals(StatusName.REJECTED)) {
            return getMessage(Message.REJECTED_ORDER, language);
        } else if (order.getStatus().getName().equals(StatusName.DELIVERED)) {
            return getMessage(Message.DELIVERED_ORDER, language);
        }
        return null;
    }

    public String getCategoryNameByProduct(Product product, String languageCode) {
        if (languageCode.equals("Ru")) {
            return product.getCategory().getNameRu();
        } else
            return product.getCategory().getNameUz();
    }

    public String getProductNameByProduct(Product product, String languageCode) {
        if (languageCode.equals("Ru")) {
            return product.getNameRu();
        } else
            return product.getNameUz();
    }

    public Category getCategoryByName(String name, String languageCode) {
        if (languageCode.equals("Ru"))
            return categoryRepository.findByNameRu(name);
        else
            return categoryRepository.findByNameUz(name);
    }

    public Product getProductByName(String name, String languageCode) {
        if (languageCode.equals("Ru"))
            return productRepository.findByNameRu(name);
        else
            return productRepository.findByNameUz(name);
    }

    private static final String BUNDLE_BASE_NAME = "messages";
    private static final String DOMAIN_ORDER = "https://hayat.medias.uz/order/";
    private static final String UZ = "Uz";
    private static final String RU = "Ru";

    public String getMessage(String key, String language) {
        List<LanguagePs> allByLanguage = languageRepository.findAll();
        if (!allByLanguage.isEmpty()) {
            for (LanguagePs languagePs : allByLanguage) {
                for (LanguageSourcePs languageSourceP : languagePs.getLanguageSourcePs()) {
                    if (languageSourceP.getTranslation() != null &&
                            languageSourceP.getLanguage().equals(language) &&
                            languagePs.getKey().equals(key)) {
                        return languageSourceP.getTranslation();
                    }
                }
            }
        }
        return null;
    }

    public String getMarketNameByUser(String chatId, String languageCode) {
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        if (languageCode.equals("Uz"))
            return tgUser.getMarket().getNameUz();
        else
            return tgUser.getMarket().getNameRu();
    }

    public Market getMarketByNameFromRepo(String name, String languageCode) {
        if (languageCode.equals("Ru")) {
            return marketRepository.findByNameRu(name);
        } else {
            return marketRepository.findByNameUz(name);
        }
    }

//    public String getMessage(String key, String languageCode) {
//        Locale locale = new Locale(languageCode);
//        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale, new UTF8Control());
//
//        return bundle.containsKey(key) ? bundle.getString(key) : "Message not found";
//    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "\\+998[1-9]\\d{8}";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    public String getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        return "";
    }

    public SendMessage whenRerun(Update update) {
        String chatId = getChatId(update);
        return new SendMessage(chatId, getMessage(Message.CLICK_START, getUserLanguage(chatId)));
    }

    //THESE 2 METHODS WORK WHEN /START BUTTON CLICKED
    public SendMessage whenStart(Update update) {
        String chatId = getChatId(update);
        if (tgUserRepository.existsByChatId(chatId) &&
                tgUserRepository.findByChatId(chatId).getName() != null &&
                tgUserRepository.findByChatId(chatId).getPhoneNumber() != null &&
                tgUserRepository.findByChatId(chatId).getMarket() != null &&
                tgUserRepository.findByChatId(chatId).isAccepted() &&
                !tgUserRepository.findByChatId(chatId).isBlocked() &&
                !tgUserRepository.findByChatId(chatId).isRejected()) {
            return whenMenuForExistedUser(update);
//        } else if (tgUserRepository.existsByChatId(chatId) &&
//                tgUserRepository.findByChatId(chatId).getName() != null &&
//                tgUserRepository.findByChatId(chatId).getPhoneNumber() != null &&
//                tgUserRepository.findByChatId(chatId).getMarket() != null &&
//                !tgUserRepository.findByChatId(chatId).isAccepted() &&
//                !tgUserRepository.findByChatId(chatId).isRejected()) {
//            return new SendMessage(chatId, getMessage(Message.PLEASE_WAIT, getUserLanguage(chatId)));
        } else {
            SendMessage sendMessage = new SendMessage(chatId,
                    getMessage(Message.LANG_SAME_FOR_2_LANG, getUserLanguage(chatId)));
            sendMessage.setReplyMarkup(forStart());
            setUserState(chatId, BotState.CHOOSE_LANG);
            return sendMessage;
        }
    }

    private ReplyKeyboardMarkup forStart() {
        String chatId = getChatId(new Update());

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();
        KeyboardButton button2 = new KeyboardButton();

        button1.setText(getMessage(Message.UZBEK, getUserLanguage(chatId)));
        button2.setText(getMessage(Message.RUSSIAN, getUserLanguage(chatId)));

        row1.add(button1);
        row1.add(button2);

        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    //THESE 3 METHODS WORK WHEN USER CHOOSES LANGUAGE
    public SendMessage whenUz(Update update) {
        return getSendMessage(update, UZ);
    }

    public SendMessage whenRu(Update update) {
        return getSendMessage(update, RU);
    }

    private SendMessage getSendMessage(Update update, String langCode) {
        String chatId = getChatId(update);
        if (!tgUserRepository.existsByChatId(chatId)) {
            TgUser tgUser = TgUser.builder().chatId(chatId)
                    .lang(langCode)
                    .isAccepted(true)
                    .isBlocked(false)
                    .build();
            tgUserRepository.save(tgUser);
        } else {
            TgUser tgUser = tgUserRepository.findByChatId(chatId);
            tgUser.setLang(langCode);
            tgUser.setAccepted(true);
            tgUser.setBlocked(false);
            tgUserRepository.save(tgUser);
        }
        SendMessage sendMessage = new SendMessage(getChatId(update),
                getMessage(Message.ENTER_NAME, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        setUserState(chatId, BotState.ENTER_PHONE_NUMBER);
        return sendMessage;
    }

    //THESE 2 METHODS WORK WHEN USER ENTERS NAME
    public SendMessage whenPhoneNumber(Update update) {
        String chatId = getChatId(update);
        String name = update.getMessage().getText();

        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setName(name);
        tgUserRepository.save(tgUser);

        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.ENTER_PHONE_NUMBER,
                getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forPhoneNumber(update));
        setUserState(chatId, BotState.CHOOSE_MARKET);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forPhoneNumber(Update update) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();

        button1.setText(getMessage(Message.SHARE_PHONE_NUMBER, getUserLanguage(getChatId(update))));
        button1.setRequestContact(true);

        row1.add(button1);

        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    //THIS METHOD WORKS WHEN PHONE NUMBER FORMAT IS INCORRECT
    public SendMessage whenIncorrectPhoneFormat(Update update) {
        return whenChooseMarket(update);
    }

    //THESE 3 METHODS WORK WHEN USER ENTERS PHONE NUMBER
    public SendMessage whenChooseMarket(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        if (update.getMessage().hasText()) {
            if (isValidPhoneNumber(update.getMessage().getText())) {
                String phoneNumber = update.getMessage().getText();
                tgUser.setPhoneNumber(phoneNumber);
                tgUserRepository.save(tgUser);
                return executeChooseMarket(update);
            } else {
                SendMessage sendMessage = new SendMessage(getChatId(update),
                        getMessage(Message.INCORRECT_PHONE_FORMAT, getUserLanguage(chatId)));
                sendMessage.setReplyMarkup(forPhoneNumber(update));
                setUserState(chatId, BotState.INCORRECT_PHONE_FORMAT);
                return sendMessage;
            }
        } else {
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            phoneNumber = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
            tgUser.setPhoneNumber(phoneNumber);
            tgUserRepository.save(tgUser);
            return executeChooseMarket(update);
        }
    }

    private SendMessage executeChooseMarket(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.CHOOSE_MARKET,
                getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forChooseMarket(update));
        setUserState(chatId, BotState.MENU);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forChooseMarket(Update update) {
        String language = getUserLanguage(getChatId(update));

        Sort sort = Sort.by(Sort.Order.asc("createdAt"));
        List<Market> markets = marketRepository.findAll(sort);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardButton> keyboardButtons = new ArrayList<>();

        if (language.equals("Uz")) {
            for (Market market : markets)
                keyboardButtons.add(new KeyboardButton(market.getNameUz()));
        } else {
            for (Market market : markets)
                keyboardButtons.add(new KeyboardButton(market.getNameRu()));
        }

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (KeyboardButton keyboardButton : keyboardButtons) {
            row.add(keyboardButton);
            if (row.size() == 2) {
                keyboardRows.add(row);
                row = new KeyboardRow();
            }
        }

        if (!row.isEmpty()) {
            keyboardRows.add(row);
        }

        markup.setKeyboard(keyboardRows);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

//    public SendMessage whenPending(Update update) {
//        String chatId = getChatId(update);

//        Market market = getMarketByNameFromRepo(update.getMessage().getText(), getUserLanguage(chatId));
//        TgUser tgUser = tgUserRepository.findByChatId(chatId);
//        tgUser.setMarket(market);
//        tgUserRepository.save(tgUser);
//
//        setUserState(chatId, BotState.IN_REVIEW);
//        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.IN_REVIEW_MESSAGE, getUserLanguage(chatId)));
//        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
//        return sendMessage;
//    }

//    public SendMessage whenSendAppToChannel(Update update) {
//        String chatId = getChatId(update);
//
//        Market market = getMarketByNameFromRepo(update.getMessage().getText(), getUserLanguage(chatId));
//        TgUser tgUser = tgUserRepository.findByChatId(chatId);
//        tgUser.setMarket(market);
//        tgUserRepository.save(tgUser);
//        SendMessage sendMessage = new SendMessage(CHANNEL_ID_APP,
//                String.format(getMessage(Message.APPLICATION, getUserLanguage(chatId)),
//                        tgUser.getId(),
//                        tgUser.getName(),
//                        tgUser.getPhoneNumber(),
//                        getMarketNameByUser(chatId, getUserLanguage(chatId))));
//        sendMessage.setReplyMarkup(forSendAppToChannel(update));
//        sendMessage.enableHtml(true);
//        setUserState(chatId, BotState.IN_REVIEW);
//        return sendMessage;
//    }

//    private InlineKeyboardMarkup forSendAppToChannel(Update update) {
//        String chatId = getChatId(update);
//
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//
//        InlineKeyboardButton button1 = new InlineKeyboardButton();
//        InlineKeyboardButton button2 = new InlineKeyboardButton();
//
//        button1.setText(getMessage(Message.ACCEPT, getUserLanguage(chatId)));
//        button2.setText(getMessage(Message.REJECT, getUserLanguage(chatId)));
//
//        button1.setCallbackData("accept" + update.getMessage().getChatId());
//        button2.setCallbackData("reject" + update.getMessage().getChatId());
//
//        List<InlineKeyboardButton> row1 = new ArrayList<>();
//
//        row1.add(button1);
//        row1.add(button2);
//
//        rowsInline.add(row1);
//
//        markupInline.setKeyboard(rowsInline);
//
//        return markupInline;
//    }

//    public EditMessageText acceptUser(Update update) {
//        String chatId = update.getCallbackQuery().getData().substring(6);
//        TgUser tgUser = tgUserRepository.findByChatId(chatId);
//        tgUser.setAccepted(true);
//        tgUser.setRejected(false);
//        tgUserRepository.save(tgUser);
//
//        EditMessageText editMessageText = new EditMessageText();
//        editMessageText.setChatId(CHANNEL_ID_APP);
//        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//        editMessageText.setText(
//                String.format(getMessage(Message.ACCEPTED_APPLICATION, getUserLanguage(chatId)),
//                        tgUser.getId(),
//                        tgUser.getName(),
//                        tgUser.getPhoneNumber(),
//                        getMarketNameByUser(chatId, getUserLanguage(chatId))));
//        editMessageText.enableHtml(true);
//        return editMessageText;
//    }

//    public SendMessage whenAcceptSendMessageToUser(Update update) {
//        String chatId = update.getCallbackQuery().getData().substring(6);
//        setUserState(chatId, BotState.MENU);
//        return new SendMessage(chatId, getMessage(Message.ACCEPTED_USER_MSG, getUserLanguage(chatId)));
//    }

//    public EditMessageText rejectUser(Update update) {
//        String chatId = update.getCallbackQuery().getData().substring(6);
//        TgUser tgUser = tgUserRepository.findByChatId(chatId);
//
//        EditMessageText editMessageText = new EditMessageText();
//        editMessageText.setChatId(CHANNEL_ID_APP);
//        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
//        editMessageText.setText(
//                String.format(getMessage(Message.REJECTED_APPLICATION, getUserLanguage(chatId)),
//                        tgUser.getId(),
//                        tgUser.getName(),
//                        tgUser.getPhoneNumber(),
//                        getMarketNameByUser(chatId, getUserLanguage(chatId))));
//        editMessageText.enableHtml(true);
//        return editMessageText;
//    }

//    public SendMessage whenRejectSendMessageToUser(Update update) {
//        String chatId = update.getCallbackQuery().getData().substring(6);
//        TgUser tgUser = tgUserRepository.findByChatId(chatId);
//        tgUser.setRejected(true);
//        tgUserRepository.save(tgUser);
//        return new SendMessage(chatId, getMessage(Message.REJECTED_USER_MSG, getUserLanguage(chatId)));
//    }

    public SendMessage whenMenu(Update update) {
        String chatId = getChatId(update);

        Market market = getMarketByNameFromRepo(update.getMessage().getText(), getUserLanguage(chatId));
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setMarket(market);
        tgUserRepository.save(tgUser);

        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.MENU_MSG, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public SendMessage whenMenuForExistedUser(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.MENU_MSG, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public ReplyKeyboardMarkup forMenu(String chatId) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();
        KeyboardButton button2 = new KeyboardButton();
        KeyboardButton button3 = new KeyboardButton();
        KeyboardButton button4 = new KeyboardButton();
        KeyboardButton button5 = new KeyboardButton();

        button1.setText(getMessage(Message.MENU_ORDER, getUserLanguage(chatId)));
        button2.setText(getMessage(Message.MENU_PRICE_LIST, getUserLanguage(chatId)));
        button3.setText(getMessage(Message.MENU_SUG_COMP, getUserLanguage(chatId)));
        button4.setText(getMessage(Message.MENU_ABOUT_US, getUserLanguage(chatId)));
        button5.setText(getMessage(Message.MENU_SETTINGS, getUserLanguage(chatId)));

        row1.add(button1);
        row1.add(button2);
        row2.add(button3);
        row3.add(button4);
        row3.add(button5);

        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);

        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenSuggestAndComplaint(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.SEND_SUG_COMP, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        setUserState(chatId, BotState.SEND_SUGGESTION_COMPLAINT);
        return sendMessage;
    }

    public SendMessage whenReceiveSuggestAndComplaint(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        SuggestionsComplaints suggestionsComplaints = SuggestionsComplaints.builder()
                .text(update.getMessage().getText())
                .tgUser(tgUser)
                .build();
        SuggestionsComplaints complaints = suggestsComplaintsRepo.save(suggestionsComplaints);

        SendMessage sendMessage = new SendMessage(CHANNEL_ID_SUG_COMP,
                String.format(getMessage(Message.SUGGEST_COMPLAINT, getUserLanguage(chatId)),
                        complaints.getId(),
                        tgUser.getName(),
                        tgUser.getPhoneNumber(),
                        getMarketNameByUser(chatId, getUserLanguage(chatId)),
                        complaints.getText()));
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public SendMessage whenResponseToSugComp(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.RESPONSE_SUG_COMP, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public SendMessage whenSettings1(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(getMessage(Message.SETTINGS, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    public SendMessage whenSettings2(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(
                String.format(getMessage(Message.USER_INFO, getUserLanguage(chatId)),
                        tgUser.getName(),
                        tgUser.getPhoneNumber(),
                        getMarketNameByUser(chatId, getUserLanguage(chatId))));
        sendMessage.setReplyMarkup(forSettings(update));
        return sendMessage;
    }

    private InlineKeyboardMarkup forSettings(Update update) {
        String chatId = getChatId(update);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();

        button1.setText(getMessage(Message.CHANGE_NAME, getUserLanguage(chatId)));
        button2.setText(getMessage(Message.CHANGE_PHONE_NUMBER, getUserLanguage(chatId)));
        button3.setText(getMessage(Message.CHANGE_MARKET, getUserLanguage(chatId)));
        button4.setText(getMessage(Message.CHANGE_LANGUAGE, getUserLanguage(chatId)));

        button1.setCallbackData("changeName");
        button2.setCallbackData("changePhone");
        button3.setCallbackData("changeMarket");
        button4.setCallbackData("changeLanguage");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();

        row1.add(button1);
        row2.add(button2);
        row3.add(button3);
        row4.add(button4);

        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);
        rowsInline.add(row4);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public EditMessageText whenChangeName1(Update update) {
        String chatId = getChatId(update);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setText(getMessage(Message.ENTER_NAME, getUserLanguage(chatId)));
        setUserState(chatId, BotState.CHANGE_NAME);
        return editMessageText;
    }

    public SendMessage whenChangeName2(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setName(update.getMessage().getText());
        tgUserRepository.save(tgUser);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(getMessage(Message.NAME_CHANGED, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public DeleteMessage deleteMessageForCallback(Update update) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(getChatId(update));
        deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return deleteMessage;
    }

    public SendMessage whenChangePhoneNumber1(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.ENTER_PHONE_NUMBER, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forPhoneNumber(update));
        setUserState(chatId, BotState.CHANGE_PHONE_NUMBER);
        return sendMessage;
    }

    public SendMessage whenChangePhoneNumber2(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);

        if (update.getMessage().hasText()) {
            if (isValidPhoneNumber(update.getMessage().getText())) {
                String phoneNumber = update.getMessage().getText();
                tgUser.setPhoneNumber(phoneNumber);
                tgUserRepository.save(tgUser);
                return executeChangePhoneNumber(update);
            } else {
                SendMessage sendMessage = new SendMessage(getChatId(update),
                        getMessage(Message.INCORRECT_PHONE_FORMAT, getUserLanguage(chatId)));
                sendMessage.setReplyMarkup(forPhoneNumber(update));
                setUserState(chatId, BotState.INCORRECT_PHONE_FORMAT);
                return sendMessage;
            }
        } else {
            String phoneNumber = update.getMessage().getContact().getPhoneNumber();
            phoneNumber = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
            tgUser.setPhoneNumber(phoneNumber);
            tgUserRepository.save(tgUser);
            return executeChangePhoneNumber(update);
        }
    }

    private SendMessage executeChangePhoneNumber(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.PHONE_NUMBER_CHANGED,
                getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public SendMessage whenChangeMarket1(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.CHOOSE_MARKET,
                getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forChooseMarket(update));
        setUserState(chatId, BotState.CHANGE_MARKET);
        return sendMessage;
    }

    public SendMessage whenChangeMarket2(Update update) {
        String chatId = getChatId(update);

        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        Market market = getMarketByNameFromRepo(update.getMessage().getText(), getUserLanguage(chatId));
        tgUser.setMarket(market);
        tgUserRepository.save(tgUser);

        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.MARKET_CHANGED, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public SendMessage whenChangeLanguage1(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.LANG_SAME_FOR_2_LANG, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forStart());
        setUserState(chatId, BotState.CHANGE_LANGUAGE);
        return sendMessage;
    }

    public SendMessage whenChangeLanguage2(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);

        if (update.getMessage().getText().equals(
                getMessage(Message.UZBEK, getUserLanguage(chatId)))) {
            tgUser.setLang(UZ);
        } else if (update.getMessage().getText().equals(
                getMessage(Message.RUSSIAN, getUserLanguage(chatId)))) {
            tgUser.setLang(RU);
        }

        tgUserRepository.save(tgUser);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.LANGUAGE_CHANGED, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(chatId));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    public SendMessage whenOrder(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);

        if (!basketRepository.existsByTgUserChatId(chatId)) {
            Basket basket = Basket.builder().tgUser(tgUser).build();
            basketRepository.save(basket);
        }

        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.CHOOSE_CATEGORY, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forOrder(update));
        setUserState(chatId, BotState.CHOOSE_CATEGORY);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forOrder(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        int count = 0;
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        if (basket != null) {
            List<ChosenProduct> chosenProducts = basket.getChosenProducts();
            for (ChosenProduct chosenProduct : chosenProducts) {
                if (chosenProduct.getProduct() != null && chosenProduct.getCount() != null)
                    count++;
            }
        }

        Sort sort = Sort.by(Sort.Order.asc("number"));
        List<Category> categories = categoryRepository.findAll(sort);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardButton> keyboardButtons = new ArrayList<>();

        if (language.equals("Uz")) {
            for (Category category : categories)
                keyboardButtons.add(new KeyboardButton(category.getNameUz()));
        } else {
            for (Category category : categories)
                keyboardButtons.add(new KeyboardButton(category.getNameRu()));
        }

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardButton button1 = new KeyboardButton(getMessage(Message.BACK, language));
        KeyboardButton button2 = new KeyboardButton(
                getMessage(Message.BASKET, language) + "(" + count + ")");

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(button1);
        keyboardRow1.add(button2);

        keyboardRows.add(keyboardRow1);
        KeyboardRow row = new KeyboardRow();

        for (KeyboardButton keyboardButton : keyboardButtons) {
            row.add(keyboardButton);
            if (row.size() == 2) {
                keyboardRows.add(row);
                row = new KeyboardRow();
            }
        }

        if (!row.isEmpty()) {
            keyboardRows.add(row);
        }

        markup.setKeyboard(keyboardRows);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenChosenCategory(Update update, String text) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                String.format(getMessage(Message.CATEGORY, getUserLanguage(chatId)),
                        text));
        sendMessage.setReplyMarkup(forChosenCategory(update, text));
        sendMessage.enableHtml(true);
        setUserState(chatId, BotState.CHOOSE_PRODUCT);
        return sendMessage;
    }

    public SendMessage whenChosenCategory2(Update update) {
        String chatId = getChatId(update);
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        Product product = chosenProducts.get(chosenProducts.size() - 1).getProduct();
        String category = getCategoryNameByProduct(product, getUserLanguage(chatId));
        SendMessage sendMessage = new SendMessage(chatId,
                String.format(getMessage(Message.CATEGORY, getUserLanguage(chatId)),
                        category));
        sendMessage.setReplyMarkup(forChosenCategory(update, category));
        sendMessage.enableHtml(true);
        setUserState(chatId, BotState.CHOOSE_PRODUCT);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forChosenCategory(Update update, String text) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);
        Category category = getCategoryByName(text, language);

        int count = 0;
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        if (basket != null) {
            List<ChosenProduct> chosenProducts = basket.getChosenProducts();
            for (ChosenProduct chosenProduct : chosenProducts) {
                if (chosenProduct.getProduct() != null && chosenProduct.getCount() != null)
                    count++;
            }
        }

        Sort sort = Sort.by(Sort.Order.asc("number"));
        List<Product> products = productRepository.findAllByCategoryId(category.getId(), sort);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardButton> keyboardButtons = new ArrayList<>();

        if (language.equals("Uz")) {
            for (Product product : products)
                keyboardButtons.add(new KeyboardButton(product.getNameUz()));
        } else {
            for (Product product : products)
                keyboardButtons.add(new KeyboardButton(product.getNameRu()));
        }

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardButton button1 = new KeyboardButton(getMessage(Message.BACK, language));
        KeyboardButton button2 = new KeyboardButton(
                getMessage(Message.BASKET, language) + "(" + count + ")");

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(button1);
        keyboardRow1.add(button2);

        keyboardRows.add(keyboardRow1);
        KeyboardRow row = new KeyboardRow();

        for (KeyboardButton keyboardButton : keyboardButtons) {
            row.add(keyboardButton);
            if (row.size() == 1) {
                keyboardRows.add(row);
                row = new KeyboardRow();
            }
        }

        if (!row.isEmpty()) {
            keyboardRows.add(row);
        }

        markup.setKeyboard(keyboardRows);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenChosenProduct(Update update, String text, String s) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);
        Product product = getProductByName(text, language);
        String category = getCategoryNameByProduct(product, language);
        boolean a = false;

        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        if (!chosenProducts.isEmpty()) {
            for (ChosenProduct value : chosenProducts) {
                if (getProductNameByProduct(value.getProduct(), language)
                        .equals(getProductNameByProduct(product, language))) {
                    value.setTurn(true);
                    chosenProductRepository.save(value);
                    a = true;
                }

            }
            if (!a) {
                ChosenProduct chosenProduct = ChosenProduct.builder().product(product).turn(true).build();
                ChosenProduct saved = chosenProductRepository.save(chosenProduct);
                basket.getChosenProducts().add(saved);
            }
        } else {
            ChosenProduct chosenProduct = ChosenProduct.builder().product(product).turn(true).build();
            ChosenProduct saved = chosenProductRepository.save(chosenProduct);
            basket.getChosenProducts().add(saved);
        }
        basketRepository.save(basket);

        SendMessage sendMessage = new SendMessage(chatId,
                String.format(getMessage(Message.CHOSEN_PRODUCT, language),
                        category,
                        text));
        sendMessage.setReplyMarkup(forChosenProduct(update, text, s));
        sendMessage.enableHtml(true);
        setUserState(chatId, BotState.PRODUCT_COUNT);
        return sendMessage;
    }

    List<Integer> selectedNumbers = new ArrayList<>();

    public EditMessageText whenChosenProduct1(Update update, String text, String s) {
        String x = "0";
        if (!s.equals("❌")) {
            if (!(selectedNumbers.isEmpty() && s.equals("0"))) {
                selectedNumbers.add(Integer.parseInt(s));

                StringBuilder combinedNumbers = new StringBuilder();
                for (int number : selectedNumbers) {
                    combinedNumbers.append(number);
                }
                x = combinedNumbers.toString();
            }
        } else {
            selectedNumbers = new ArrayList<>();
        }

        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);
        Product product = getProductByName(text, language);
        String category = getCategoryNameByProduct(product, language);
        boolean a = false;

        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        if (!chosenProducts.isEmpty()) {
            for (ChosenProduct value : chosenProducts) {
                if (getProductNameByProduct(value.getProduct(), language)
                        .equals(getProductNameByProduct(product, language))) {
                    value.setTurn(true);
                    chosenProductRepository.save(value);
                    a = true;
                }

            }
            if (!a) {
                ChosenProduct chosenProduct = ChosenProduct.builder().product(product).turn(true).build();
                ChosenProduct saved = chosenProductRepository.save(chosenProduct);
                basket.getChosenProducts().add(saved);
            }
        } else {
            ChosenProduct chosenProduct = ChosenProduct.builder().product(product).turn(true).build();
            ChosenProduct saved = chosenProductRepository.save(chosenProduct);
            basket.getChosenProducts().add(saved);
        }
        basketRepository.save(basket);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(String.format(getMessage(Message.CHOSEN_PRODUCT, language),
                category, text));
        editMessageText.setChatId(chatId);
        editMessageText.setReplyMarkup(forChosenProduct(update, text, x));
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.enableHtml(true);
        setUserState(chatId, BotState.PRODUCT_COUNT);
        return editMessageText;
    }

    private InlineKeyboardMarkup forChosenProduct(Update update, String text, String s) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        int count = 0;
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        if (basket != null) {
            List<ChosenProduct> chosenProducts = basket.getChosenProducts();
            for (ChosenProduct chosenProduct : chosenProducts) {
                if (chosenProduct.getProduct() != null && chosenProduct.getCount() != null)
                    count++;
            }
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> rowX = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton buttonX = new InlineKeyboardButton();

        button1.setText(getMessage(Message.BACK, language));
        button2.setText(getMessage(Message.BASKET, language) + "(" + count + ")");

        button1.setCallbackData("back");
        button2.setCallbackData("basket");

        row.add(button1);
        row.add(button2);
        keyboardRows.add(row);

        buttonX.setText(String.format(getMessage(Message.COUNT, getUserLanguage(chatId)), s));
        buttonX.setCallbackData("xxx");
        rowX.add(buttonX);
        keyboardRows.add(rowX);

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData(i + text);
            button.setText(String.valueOf(i));
            row1.add(button);
            if (row1.size() == 3) {
                keyboardRows.add(row1);
                row1 = new ArrayList<>();
            }
        }

        if (!row1.isEmpty()) {
            keyboardRows.add(row1);
        }

        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();

        button3.setText("❌");
        button4.setText("0");
        button5.setText(getMessage(Message.CONTINUE, language));

        button3.setCallbackData("❌" + text);
        button4.setCallbackData("0" + text);
        button5.setCallbackData("continue" + s);

        row2.add(button3);
        row2.add(button4);
        row2.add(button5);
        keyboardRows.add(row2);

        markupInline.setKeyboard(keyboardRows);
        return markupInline;
    }

    public EditMessageText whenAddProductToBasket(Update update, String text) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        selectedNumbers = new ArrayList<>();

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        if (text.equals("0")) {
            editMessageText.setText(getMessage(Message.NOT_ZERO, getUserLanguage(chatId)));
            return editMessageText;
        } else {
            Basket basket = basketRepository.findByTgUserChatId(chatId);
            List<ChosenProduct> chosenProducts = basket.getChosenProducts();
            ChosenProduct chosenProduct = null;
            for (ChosenProduct product : chosenProducts) {
                if (product.isTurn()) {
                    chosenProduct = product;
                    product.setTurn(false);
                    chosenProductRepository.save(product);
                }
            }
            assert chosenProduct != null;
            Integer count = chosenProduct.getCount();
            if (count != null) {
                count += Integer.parseInt(text);
            } else {
                count = Integer.parseInt(text);
            }
            chosenProduct.setCount(count);
            chosenProductRepository.save(chosenProduct);

            editMessageText.setText(
                    String.format(getMessage(Message.ALL_INFO_CHOSEN_PRODUCT, language),
                            getProductNameByProduct(chosenProduct.getProduct(), language),
                            text));
            editMessageText.enableHtml(true);
            return editMessageText;
        }
    }

    public SendMessage whenBackInProductCount(Update update) {
        String chatId = getChatId(update);
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        ChosenProduct chosenProduct = null;
        for (ChosenProduct product : chosenProducts) {
            if (product.isTurn()) {
                chosenProduct = product;
                product.setTurn(false);
                chosenProductRepository.save(product);
            }
        }
        assert chosenProduct != null;
        Product product = chosenProduct.getProduct();
        String categoryName = getCategoryNameByProduct(product, getUserLanguage(chatId));

        return whenChosenCategory(update, categoryName);
    }

    public SendMessage whenBasket(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        Basket basket = basketRepository.findByTgUserChatId(chatId);
        if (!basket.getChosenProducts().isEmpty()) {
            List<ChosenProduct> chosenProducts = basket.getChosenProducts();
            for (int i = 0; i < chosenProducts.size(); i++) {
                if (chosenProducts.get(i).getCount() == null) {
                    try {
                        basketRepository.deleteChosenProductsFromBasket(chosenProducts.get(i).getId());
                    } catch (Exception ignored) {
                    }
                    chosenProductRepository.delete(chosenProducts.get(i));
                    chosenProducts.remove(chosenProducts.get(i));
                }
            }
        }
        SendMessage sendMessage;
        if (!basket.getChosenProducts().isEmpty()) {
            sendMessage = new SendMessage(chatId,
                    String.format(getMessage(Message.PRODUCTS_IN_BASKET, language),
                            getChosenProductsNameAndCount(chatId, language)));
            sendMessage.setReplyMarkup(forWhenBasketInline(update));
            sendMessage.enableHtml(true);
        } else {
            sendMessage = new SendMessage(chatId,
                    getMessage(Message.EMPTY_BASKET, getUserLanguage(chatId)));
            sendMessage.setReplyMarkup(forMenu(chatId));
            setUserState(chatId, BotState.CHOOSE_MENU);
        }
        return sendMessage;
    }

    public InlineKeyboardMarkup forWhenBasketInline(Update update) {
        String chatId = getChatId(update);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        chosenProducts.sort(Comparator.comparingLong(ChosenProduct::getId));

        for (ChosenProduct chosenProduct : chosenProducts) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton button1 = new InlineKeyboardButton();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            InlineKeyboardButton button3 = new InlineKeyboardButton();
            button1.setText("➖");
            button2.setText("❌");
            button3.setText("➕");

            button1.setCallbackData("minus" + chosenProduct.getId());
            button2.setCallbackData("delete" + chosenProduct.getId());
            button3.setCallbackData("plus" + chosenProduct.getId());

            row.add(button1);
            row.add(button2);
            row.add(button3);

            keyboardRows.add(row);
        }
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();

        button1.setText(getMessage(Message.CLEAR_BASKET, getUserLanguage(chatId)));
        button2.setText(getMessage(Message.OFFICIAL_ORDER, getUserLanguage(chatId)));
        button3.setText(getMessage(Message.BACK_TO_MENU, getUserLanguage(chatId)));

        button1.setCallbackData("clear");
        button2.setCallbackData("officialOrder");
        button3.setCallbackData("menu");

        row1.add(button1);
        row2.add(button2);
        row3.add(button3);

        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);

        markupInline.setKeyboard(keyboardRows);
        return markupInline;
    }

    private InlineKeyboardMarkup forWhenBasketBack(Update update) {
        String chatId = getChatId(update);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(getMessage(Message.BACK_TO_MENU, getUserLanguage(chatId)));
        button1.setCallbackData("menu");

        row1.add(button1);

        keyboardRows.add(row1);

        markupInline.setKeyboard(keyboardRows);
        return markupInline;
    }

    public EditMessageText edit(Update update) throws TelegramApiException {
        String chatId = getChatId(update);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(getMessage(Message.ONE_STEP_BACK,
                getUserLanguage(chatId)));
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessageText;
    }

    public EditMessageText whenClear(Update update) {
        String chatId = getChatId(update);
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        chosenProductRepository.deleteAll(basket.getChosenProducts());
        basket.setChosenProducts(null);
        basketRepository.save(basket);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(getMessage(Message.BASKET_CLEARED, getUserLanguage(chatId)));
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessageText;
    }

    public EditMessageText whenMinus(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        Long chosenProductId = Long.valueOf(update.getCallbackQuery().getData().substring(5));
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        for (int i = 0; i < chosenProducts.size(); i++) {
            if (Objects.equals(chosenProducts.get(i).getId(), chosenProductId)) {
                if (chosenProducts.get(i).getCount() > 1) {
                    chosenProducts.get(i).setCount(chosenProducts.get(i).getCount() - 1);
                    chosenProductRepository.save(chosenProducts.get(i));
                } else {
                    try {
                        basketRepository.deleteChosenProductsFromBasket(chosenProducts.get(i).getId());
                    } catch (Exception ignored) {
                    }
                    chosenProductRepository.delete(chosenProducts.get(i));
                    chosenProducts.remove(chosenProducts.get(i));
                }
            }
        }

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.enableHtml(true);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        if (!basket.getChosenProducts().isEmpty()) {
            editMessageText.setReplyMarkup(forWhenBasketInline(update));
            editMessageText.setText(
                    String.format(getMessage(Message.PRODUCTS_IN_BASKET, language),
                            getChosenProductsNameAndCount(chatId, language)));
        } else {
            editMessageText.setText(getMessage(Message.BASKET_CLEARED, language));
            editMessageText.setReplyMarkup(forWhenBasketBack(update));
        }
        return editMessageText;
    }

    public EditMessageText whenDelete(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        Long chosenProductId = Long.valueOf(update.getCallbackQuery().getData().substring(6));
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        for (int i = 0; i < chosenProducts.size(); i++) {
            if (Objects.equals(chosenProducts.get(i).getId(), chosenProductId)) {
                try {
                    basketRepository.deleteChosenProductsFromBasket(chosenProducts.get(i).getId());
                } catch (Exception ignored) {
                }
                chosenProductRepository.delete(chosenProducts.get(i));
                chosenProducts.remove(chosenProducts.get(i));
            }
        }

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.enableHtml(true);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        if (!basket.getChosenProducts().isEmpty()) {
            editMessageText.setReplyMarkup(forWhenBasketInline(update));
            editMessageText.setText(
                    String.format(getMessage(Message.PRODUCTS_IN_BASKET, language),
                            getChosenProductsNameAndCount(chatId, language)));
        } else {
            editMessageText.setText(getMessage(Message.BASKET_CLEARED, language));
            editMessageText.setReplyMarkup(forWhenBasketBack(update));
        }
        return editMessageText;
    }

    public EditMessageText whenPlus(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        Long chosenProductId = Long.valueOf(update.getCallbackQuery().getData().substring(4));
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        for (ChosenProduct chosenProduct : chosenProducts) {
            if (Objects.equals(chosenProduct.getId(), chosenProductId)) {
                chosenProduct.setCount(chosenProduct.getCount() + 1);
                chosenProductRepository.save(chosenProduct);
            }
        }

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setReplyMarkup(forWhenBasketInline(update));
        editMessageText.enableHtml(true);
        editMessageText.setText(
                String.format(getMessage(Message.PRODUCTS_IN_BASKET, language),
                        getChosenProductsNameAndCount(chatId, language)));
        return editMessageText;
    }

    public EditMessageText whenOfficialOrder1(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setText(
                String.format(getMessage(Message.PRODUCTS_IN_BASKET, language),
                        getChosenProductsNameAndCount(chatId, language)));
        editMessageText.enableHtml(true);
        return editMessageText;
    }

    public SendMessage whenOfficialOrder(Update update) {
        String chatId = getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        Basket basket = basketRepository.findByTgUserChatId(chatId);
        List<ChosenProduct> chosenProducts = basket.getChosenProducts();
        List<Long> ids = new ArrayList<>();
        for (ChosenProduct chosenProduct : chosenProducts) {
            ids.add(chosenProduct.getId());
        }
        List<ChosenProduct> allById = chosenProductRepository.findAllById(ids);
        Order order = Order.builder().tgUser(tgUser)
                .chosenProducts(allById)
                .status(statusRepository.findByName(StatusName.PENDING)).build();
        orderRepository.save(order);
        basket.setChosenProducts(new ArrayList<>());
        basketRepository.save(basket);

        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.SEND_LOCATION, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forSendLocation(update));
        setUserState(chatId, BotState.SEND_LOCATION);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forSendLocation(Update update) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();

        button1.setText(getMessage(Message.FOR_LOCATION, getUserLanguage(getChatId(update))));
        button1.setRequestLocation(true);

        row1.add(button1);

        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenComment(Update update) {
        String chatId = getChatId(update);
        List<Order> orders = orderRepository.findAllByTgUserChatId(chatId);
        Order order = orders.get(orders.size() - 1);
        Location location = update.getMessage().getLocation();
        order.setLan(location.getLongitude());
        order.setLat(location.getLatitude());
        orderRepository.save(order);

        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.SEND_COMMENT, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forSendComment(update));
        setUserState(chatId, BotState.WRITE_COMMENT);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forSendComment(Update update) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();

        button1.setText(getMessage(Message.SKIP_COMMENT, getUserLanguage(getChatId(update))));

        row1.add(button1);

        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenOrderCreated1(Update update) {
        String chatId = getChatId(update);

        List<Order> orders = orderRepository.findAllByTgUserChatId(chatId);
        Order order = orders.get(orders.size() - 1);
        if (update.getMessage().getText()
                .equals(getMessage(Message.SKIP_COMMENT, getUserLanguage(chatId)))) {
            order.setComment(null);
        } else {
            order.setComment(update.getMessage().getText());
        }
        order.setOrderedTime(LocalDateTime.now());
        orderRepository.save(order);

        SendMessage sendMessage = new SendMessage(chatId,
                String.format(getMessage(Message.ORDER_CREATED, getUserLanguage(chatId)), order.getNumber()));
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    public SendMessage whenOrderCreated2(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        List<Order> orders = orderRepository.findAllByTgUserChatId(chatId);
        Order order = orders.get(orders.size() - 1);
        String name = order.getTgUser().getName();
        String phoneNumber = order.getTgUser().getPhoneNumber();
        String date = order.getOrderedTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));
        String comment = order.getComment();

        SendMessage sendMessage = new SendMessage(CHANNEL_ID_ORDER,
                String.format(getMessage(Message.ORDER, language),
                        order.getNumber(),
                        name,
                        phoneNumber,
                        getMarketNameByUser(chatId, language),
                        getMessage(Message.LINK_COURIER, language),
                        date,
                        getChosenProductsNameAndCountForOrder(chatId, language),
                        returnComment(comment, language),
                        getOrderStatusName(chatId)));
        sendMessage.setReplyMarkup(forOrderCreated2(update, order.getId()));
        sendMessage.enableHtml(true);
        setUserState(chatId, BotState.ORDER_COMPLETE);
        return sendMessage;
    }

    public String returnComment(String comment, String languageCode) {
        return comment == null ? getMessage(Message.NO_COMMENT, languageCode) : comment;
    }

    private InlineKeyboardMarkup forOrderCreated2(Update update, UUID orderId) {
        String chatId = getChatId(update);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();

        button1.setText(getMessage(Message.ONE_THREE_HOUR, getUserLanguage(chatId)));
        button2.setText(getMessage(Message.ONE_DAY, getUserLanguage(chatId)));
        button3.setText(getMessage(Message.ONE_THREE_DAY, getUserLanguage(chatId)));
        button4.setText(getMessage(Message.REJECT, getUserLanguage(chatId)));

        button1.setCallbackData("111" + orderId);
        button2.setCallbackData("222" + orderId);
        button3.setCallbackData("333" + orderId);
        button4.setCallbackData("444" + orderId);

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row1.add(button1);
        row1.add(button2);
        row2.add(button3);
        row2.add(button4);

        rowsInline.add(row1);
        rowsInline.add(row2);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public SendMessage whenAcceptOrder1(Update update) {
        UUID orderId = UUID.fromString((update.getCallbackQuery().getData().substring(3)));
        Optional<Order> order = orderRepository.findById(orderId);
        SendMessage sendMessage = new SendMessage();
        if (order.isPresent()) {
            Order order1 = order.get();
            order1.setStatus(statusRepository.findByName(StatusName.ACCEPTED));
            orderRepository.save(order1);
            String chatId = order1.getTgUser().getChatId();
            sendMessage.setChatId(chatId);
            sendMessage.setText(String.format(
                    getMessage(Message.ORDER_ACCEPTED, getUserLanguage(chatId)), order1.getNumber(),
                    getMessage(Message.ONE_THREE_HOUR, getUserLanguage(chatId)).substring(1)));
            sendMessage.enableHtml(true);
            sendMessage.setReplyMarkup(forMenu(chatId));
            setUserState(chatId, BotState.CHOOSE_MENU);
        }
        return sendMessage;
    }

    public SendMessage whenAcceptOrder2(Update update) {
        UUID orderId = UUID.fromString((update.getCallbackQuery().getData().substring(3)));
        Optional<Order> order = orderRepository.findById(orderId);
        SendMessage sendMessage = new SendMessage();
        if (order.isPresent()) {
            Order order1 = order.get();
            order1.setStatus(statusRepository.findByName(StatusName.ACCEPTED));
            orderRepository.save(order1);
            String chatId = order1.getTgUser().getChatId();
            sendMessage.setChatId(chatId);
            sendMessage.setText(String.format(
                    getMessage(Message.ORDER_ACCEPTED, getUserLanguage(chatId)), order1.getNumber(),
                    getMessage(Message.ONE_DAY, getUserLanguage(chatId)).substring(1)));
            sendMessage.enableHtml(true);
            sendMessage.setReplyMarkup(forMenu(chatId));
            setUserState(chatId, BotState.CHOOSE_MENU);
        }
        return sendMessage;
    }

    public SendMessage whenAcceptOrder3(Update update) {
        UUID orderId = UUID.fromString((update.getCallbackQuery().getData().substring(3)));
        Optional<Order> order = orderRepository.findById(orderId);
        SendMessage sendMessage = new SendMessage();
        if (order.isPresent()) {
            Order order1 = order.get();
            order1.setStatus(statusRepository.findByName(StatusName.ACCEPTED));
            orderRepository.save(order1);
            String chatId = order1.getTgUser().getChatId();
            sendMessage.setChatId(chatId);
            sendMessage.setText(String.format(
                    getMessage(Message.ORDER_ACCEPTED, getUserLanguage(chatId)), order1.getNumber(),
                    getMessage(Message.ONE_THREE_DAY, getUserLanguage(chatId)).substring(1)));
            sendMessage.enableHtml(true);
            sendMessage.setReplyMarkup(forMenu(chatId));
            setUserState(chatId, BotState.CHOOSE_MENU);
        }
        return sendMessage;
    }

    public EditMessageText whenAcceptOrderForChannel(Update update) {
        UUID orderId = UUID.fromString((update.getCallbackQuery().getData().substring(3)));
        Optional<Order> order1 = orderRepository.findById(orderId);
        EditMessageText editMessageText = new EditMessageText();
        Order order = new Order();
        if (order1.isPresent()) {
            order = order1.get();
        }
        String chatId = order.getTgUser().getChatId();
        String language = getUserLanguage(chatId);
        String name = order.getTgUser().getName();
        String phoneNumber = order.getTgUser().getPhoneNumber();
        String date = order.getOrderedTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));
        String comment = order.getComment();
        String link = String.format("<a href='%s' target='_blank'>",
                DOMAIN_ORDER + order.getId()) +
                getMessage(Message.CLICK_COURIER, language) + "</a>";

        messageId = update.getCallbackQuery().getMessage().getMessageId();

        editMessageText.setText(String.format(getMessage(Message.ORDER, language),
                order.getNumber(),
                name,
                phoneNumber,
                getMarketNameByUser(chatId, language),
                link,
                date,
                getChosenProductsNameAndCountForOrder(chatId, language),
                returnComment(comment, language),
                getOrderStatusName(chatId)));
        editMessageText.enableHtml(true);
        editMessageText.setChatId(CHANNEL_ID_ORDER);
        editMessageText.setMessageId(messageId);
        return editMessageText;
    }

    public SendMessage whenRejectOrder1(Update update) {
        UUID orderId = UUID.fromString((update.getCallbackQuery().getData().substring(3)));
        Optional<Order> order = orderRepository.findById(orderId);
        SendMessage sendMessage = new SendMessage();
        if (order.isPresent()) {
            Order order1 = order.get();
            order1.setStatus(statusRepository.findByName(StatusName.REJECTED));
            orderRepository.save(order1);
            String chatId = order1.getTgUser().getChatId();
            sendMessage.setChatId(chatId);
            sendMessage.setText(String.format(
                    getMessage(Message.ORDER_REJECTED, getUserLanguage(chatId)), order1.getNumber()));
            sendMessage.enableHtml(true);
            sendMessage.setReplyMarkup(forMenu(chatId));
            setUserState(chatId, BotState.CHOOSE_MENU);
        }
        return sendMessage;
    }

    public EditMessageText whenRejectOrder2(Update update) {
        UUID orderId = UUID.fromString((update.getCallbackQuery().getData().substring(3)));
        Optional<Order> order1 = orderRepository.findById(orderId);
        EditMessageText editMessageText = new EditMessageText();
        Order order = new Order();
        if (order1.isPresent()) {
            order = order1.get();
        }
        String chatId = order.getTgUser().getChatId();
        String language = getUserLanguage(chatId);
        String name = order.getTgUser().getName();
        String phoneNumber = order.getTgUser().getPhoneNumber();
        String date = order.getOrderedTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));
        String comment = order.getComment();

        editMessageText.setText(
                String.format(getMessage(Message.ORDER, language),
                        order.getNumber(),
                        name,
                        phoneNumber,
                        getMarketNameByUser(chatId, language),
                        " ",
                        date,
                        getChosenProductsNameAndCountForOrder(chatId, language),
                        returnComment(comment, language),
                        getOrderStatusName(chatId))
        );
        editMessageText.enableHtml(true);
        editMessageText.setChatId(CHANNEL_ID_ORDER);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessageText;
    }

    public EditMessageText whenDeliveredEdit(UUID orderId) {
        Optional<Order> order1 = orderRepository.findById(orderId);

        Order order = new Order();
        if (order1.isPresent()) {
            order = order1.get();
        }
        String chatId = order.getTgUser().getChatId();
        String language = getUserLanguage(chatId);
        String name = order.getTgUser().getName();
        String phoneNumber = order.getTgUser().getPhoneNumber();
        String date = order.getOrderedTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));
        String comment = order.getComment();
        String link = String.format("<a href='%s' target='_blank'>",
                DOMAIN_ORDER + order.getId()) +
                getMessage(Message.CLICK_COURIER, language) + "</a>";

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(CHANNEL_ID_ORDER);
        editMessageText.setText(
                String.format(getMessage(Message.ORDER, language),
                        order.getNumber(),
                        name,
                        phoneNumber,
                        getMarketNameByUser(chatId, language),
                        link,
                        date,
                        getChosenProductsNameAndCountForOrder(chatId, language),
                        returnComment(comment, language),
                        getOrderStatusName(chatId)));
        editMessageText.enableHtml(true);
        return editMessageText;
    }

    @SneakyThrows
    public SendDocument sendFile(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        FileEntity file = fileRepository.findByName("price");

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(file.getFileId()));
        sendDocument.setCaption(
                String.format(getMessage(Message.ACTUAL_PRICE, language), file.getUploadedTime()));
        return sendDocument;
    }

    public SendMessage whenFileNotExists(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        return new SendMessage(chatId, getMessage(Message.FILE_NOT_EXISTS, language));
    }

    public SendMessage whenDelivered(String chatId, Order order) {
        String language = getUserLanguage(chatId);
        SendMessage sendMessage = new SendMessage(chatId,
                String.format(getMessage(Message.ORDER_DELIVERED, language), order.getNumber()));
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public SendMessage whenAboutUs(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.ABOUT_US, getUserLanguage(chatId)));
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public SendMessage whenUpload(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);
        if (Objects.equals(chatId, "285710521") || Objects.equals(chatId, "6931160281")
                || Objects.equals(chatId, "1302908674")) {
            setUserState(chatId, BotState.UPLOAD_FILE);
            return new SendMessage(chatId, getMessage(Message.UPLOAD_FILE, language));
        } else {
            return new SendMessage(chatId, getMessage(Message.CANNOT_SAVE_FILE, language));
        }
    }

    public SendMessage saveFile(Update update) {
        String chatId = getChatId(update);
        String language = getUserLanguage(chatId);

        if (Objects.equals(chatId, CHAT_ID_1) || Objects.equals(chatId, CHAT_ID_2)
                || Objects.equals(chatId, CHAT_ID_3)) {
            Document document = update.getMessage().getDocument();
            String uploadedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String fileId = document.getFileId();
            if (!fileRepository.existsByName("price")) {
                FileEntity file = FileEntity.builder()
                        .fileId(fileId)
                        .name("price")
                        .uploadedTime(uploadedTime).build();
                fileRepository.save(file);
            } else {
                FileEntity file = fileRepository.findByName("price");
                file.setFileId(fileId);
                file.setUploadedTime(uploadedTime);
                fileRepository.save(file);
            }

            return new SendMessage(chatId, getMessage(Message.FILE_SAVED, language));
        } else {
            return new SendMessage(chatId, getMessage(Message.CANNOT_SAVE_FILE, language));
        }
    }

    public SendMessage whenPost(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (Objects.equals(chatId, "285710521") || Objects.equals(chatId, "6931160281")
                || Objects.equals(chatId, "1302908674")) {
            setUserState(chatId, BotState.POST);
            sendMessage.setText(getMessage(Message.POST, getUserLanguage(chatId)));
            sendMessage.enableHtml(true);
            sendMessage.setReplyMarkup(forPost(update));
            return sendMessage;
        } else {
            sendMessage.setText(getMessage(Message.CANNOT_SAVE_FILE, getUserLanguage(chatId)));
            return sendMessage;
        }
    }

    private ReplyKeyboardMarkup forPost(Update update) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton();
        button1.setText(getMessage(Message.BACK, getUserLanguage(getChatId(update))));

        row1.add(button1);

        rowList.add(row1);
        markup.setKeyboard(rowList);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }
}
