package uz.mediasolutions.saleservicebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.mediasolutions.saleservicebot.entity.*;
import uz.mediasolutions.saleservicebot.manual.BotState;
import uz.mediasolutions.saleservicebot.repository.*;
import uz.mediasolutions.saleservicebot.utills.UTF8Control;
import uz.mediasolutions.saleservicebot.utills.constants.Message;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MakeService {

    private static final String CHANNEL_ID = "-1001903287909";
    private final TgUserRepository tgUserRepository;
    private final MarketRepository marketRepository;
    private final SuggestsComplaintsRepo suggestsComplaintsRepo;
    private final LanguageRepositoryPs languageRepository;

    //FOR USER STATE
    private final Map<String, BotState> userStates = new HashMap<>();

    public void setUserState(String chatId, BotState state) {
        userStates.put(chatId, state);
    }

    public BotState getUserState(String chatId) {
        return userStates.getOrDefault(chatId, BotState.START);
    }

    //FOR USER LANGUAGE
    private final Map<String, String> userLanguage = new HashMap<>();

    public void setUserLanguage(String chatId, String languageCode) {
        userLanguage.put(chatId, languageCode);
    }

    public String getUserLanguage(String chatId) {
        return userLanguage.getOrDefault(chatId, UZ);
    }

    private static final String BUNDLE_BASE_NAME = "messages";
    private static final String UZ = "Uz";
    private static final String RU = "Ru";

//    public String getMessage(String key, String language) {
//        List<LanguagePs> allByLanguage = languageRepository.findAll();
//        if (!allByLanguage.isEmpty()) {
//            for (LanguagePs languagePs : allByLanguage) {
//                for (LanguageSourcePs languageSourceP : languagePs.getLanguageSourcePs()) {
//                    if (languageSourceP.getTranslation() != null &&
//                            languageSourceP.getLanguage().equals(language) &&
//                            languagePs.getKey().equals(key)) {
//                        return languageSourceP.getTranslation();
//                    }
//                }
//            }
//        }
//        return null;
//    }

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

    public String getMessage(String key, String languageCode) {
        Locale locale = new Locale(languageCode);
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale, new UTF8Control());

        return bundle.containsKey(key) ? bundle.getString(key) : "Message not found";
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "\\+998[1-9]\\d{8}";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    public static String getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId().toString();
        }
        return "";
    }

    //THESE 2 METHODS WORK WHEN /START BUTTON CLICKED
    public SendMessage whenStart(Update update) {
        String chatId = getChatId(update);
        if (tgUserRepository.existsByChatId(chatId) &&
                tgUserRepository.findByChatId(chatId).getName() != null &&
                tgUserRepository.findByChatId(chatId).getPhoneNumber() != null &&
                tgUserRepository.findByChatId(chatId).getMarket() != null &&
                tgUserRepository.findByChatId(chatId).isAccepted()) {
            return whenMenu(update);
        } else if (tgUserRepository.existsByChatId(chatId) &&
                tgUserRepository.findByChatId(chatId).getName() != null &&
                tgUserRepository.findByChatId(chatId).getPhoneNumber() != null &&
                tgUserRepository.findByChatId(chatId).getMarket() != null &&
                !tgUserRepository.findByChatId(chatId).isAccepted()) {
            return new SendMessage(chatId, getMessage(Message.PLEASE_WAIT, getUserLanguage(chatId)));
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
        setUserLanguage(chatId, langCode);
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
        if (!tgUserRepository.existsByChatId(chatId)) {
            TgUser tgUser = TgUser.builder().chatId(chatId)
                    .name(name)
                    .isAccepted(false)
                    .build();
            tgUserRepository.save(tgUser);
        } else {
            TgUser tgUser = tgUserRepository.findByChatId(chatId);
            tgUser.setName(name);
            tgUser.setAccepted(false);
        }
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
        setUserState(chatId, BotState.PENDING);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forChooseMarket(Update update) {
        String language = getUserLanguage(getChatId(update));

        List<Market> markets = marketRepository.findAll();

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
        for (KeyboardButton button : keyboardButtons) {
            KeyboardRow row = new KeyboardRow();
            row.add(button);
            keyboardRows.add(row);
        }

        markup.setKeyboard(keyboardRows);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        return markup;
    }

    public SendMessage whenPending(Update update) {
        String chatId = getChatId(update);

        Market market = getMarketByNameFromRepo(update.getMessage().getText(), getUserLanguage(chatId));
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setMarket(market);
        tgUserRepository.save(tgUser);

        setUserState(chatId, BotState.IN_REVIEW);
        SendMessage sendMessage = new SendMessage(chatId, getMessage(Message.IN_REVIEW_MESSAGE, getUserLanguage(chatId)) +
                "\n\n" + getMessage(Message.IN_REVIEW_MESSAGE_2, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    public SendMessage whenSendAppToChannel(Update update) {
        String chatId = getChatId(update);

        Market market = getMarketByNameFromRepo(update.getMessage().getText(), getUserLanguage(chatId));
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setMarket(market);
        tgUserRepository.save(tgUser);
        SendMessage sendMessage = new SendMessage(CHANNEL_ID,
                "*" + getMessage(Message.APPLICATION, getUserLanguage(chatId)) + tgUser.getId() + "*\n\n" +
                        getMessage(Message.NAME, getUserLanguage(chatId)) + " " + tgUser.getName() + "\n" +
                        getMessage(Message.PHONE_NUMBER, getUserLanguage(chatId)) + " " + tgUser.getPhoneNumber() + "\n" +
                        getMessage(Message.MARKET, getUserLanguage(chatId)) + " " + getMarketNameByUser(chatId, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forSendAppToChannel(update));
        sendMessage.enableMarkdown(true);
        setUserState(chatId, BotState.IN_REVIEW);
        return sendMessage;
    }

    private InlineKeyboardMarkup forSendAppToChannel(Update update) {
        String chatId = getChatId(update);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();

        button1.setText(getMessage(Message.ACCEPT, getUserLanguage(chatId)));
        button2.setText(getMessage(Message.REJECT, getUserLanguage(chatId)));

        button1.setCallbackData("accept" + update.getMessage().getChatId());
        button2.setCallbackData("reject" + update.getMessage().getChatId());

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        row1.add(button1);
        row1.add(button2);

        rowsInline.add(row1);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public EditMessageText acceptUser(Update update) {
        String chatId = update.getCallbackQuery().getData().substring(6);
        TgUser tgUser = tgUserRepository.findByChatId(chatId);
        tgUser.setAccepted(true);
        tgUserRepository.save(tgUser);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(CHANNEL_ID);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setText("*" + getMessage(Message.APPLICATION, getUserLanguage(chatId)) + tgUser.getId() + "*\n\n" +
                getMessage(Message.NAME, getUserLanguage(chatId)) + " " + tgUser.getName() + "\n" +
                getMessage(Message.PHONE_NUMBER, getUserLanguage(chatId)) + " " + tgUser.getPhoneNumber() + "\n" +
                getMessage(Message.MARKET, getUserLanguage(chatId)) + " " + getMarketNameByUser(chatId, getUserLanguage(chatId)) + "\n" +
                getMessage(Message.ACCEPTED, getUserLanguage(chatId)));
        editMessageText.enableMarkdown(true);
        return editMessageText;
    }

    public SendMessage whenAcceptSendMessageToUser(Update update) {
        String chatId = update.getCallbackQuery().getData().substring(6);
        setUserState(chatId, BotState.MENU);
        return new SendMessage(chatId, getMessage(Message.ACCEPTED_USER_MSG, getUserLanguage(chatId)));
    }

    public SendMessage whenMenu(Update update) {
        String chatId = update.getCallbackQuery().getData().substring(6);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.MENU_MSG, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(update));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }

    private ReplyKeyboardMarkup forMenu(Update update) {
        String chatId = getChatId(update);
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

        SendMessage sendMessage = new SendMessage(CHANNEL_ID,
                "*" + getMessage(Message.SUGGEST_COMPLAINT, getUserLanguage(chatId)) + complaints.getId() + "*\n\n" +
                        getMessage(Message.NAME, getUserLanguage(chatId)) + " " + tgUser.getName() + "\n" +
                        getMessage(Message.PHONE_NUMBER, getUserLanguage(chatId)) + " " + tgUser.getPhoneNumber() + "\n" +
                        getMessage(Message.MARKET, getUserLanguage(chatId)) + " " + getMarketNameByUser(chatId, getUserLanguage(chatId)) + "\n\n" +
                        getMessage(Message.GIVEN_COMMENT, getUserLanguage(chatId)) + " " + complaints.getText());
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    public SendMessage whenResponseToSugComp(Update update) {
        String chatId = getChatId(update);
        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.RESPONSE_SUG_COMP, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(update));
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

        String userMarket = null;
        if (getUserLanguage(chatId).equals("Ru"))
            userMarket = tgUser.getMarket().getNameRu();
        else
            userMarket = tgUser.getMarket().getNameUz();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(getMessage(Message.NAME, getUserLanguage(chatId)) + " " + tgUser.getName() + "\n" +
                getMessage(Message.PHONE_NUMBER, getUserLanguage(chatId)) + " " + tgUser.getPhoneNumber() + "\n" +
                getMessage(Message.MARKET, getUserLanguage(chatId)) + " " + userMarket);
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
        sendMessage.setReplyMarkup(forMenu(update));
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
        sendMessage.setReplyMarkup(forMenu(update));
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
        sendMessage.setReplyMarkup(forMenu(update));
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

        if (update.getMessage().getText().equals(
                getMessage(Message.UZBEK, getUserLanguage(chatId)))) {
            setUserLanguage(chatId, UZ);
        } else if (update.getMessage().getText().equals(
                getMessage(Message.RUSSIAN, getUserLanguage(chatId)))) {
            setUserLanguage(chatId, RU);
        }

        SendMessage sendMessage = new SendMessage(chatId,
                getMessage(Message.LANGUAGE_CHANGED, getUserLanguage(chatId)));
        sendMessage.setReplyMarkup(forMenu(update));
        setUserState(chatId, BotState.CHOOSE_MENU);
        return sendMessage;
    }
}
