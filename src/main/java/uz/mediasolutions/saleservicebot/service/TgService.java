package uz.mediasolutions.saleservicebot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.saleservicebot.entity.TgUser;
import uz.mediasolutions.saleservicebot.manual.BotState;
import uz.mediasolutions.saleservicebot.repository.FileRepository;
import uz.mediasolutions.saleservicebot.repository.TgUserRepository;
import uz.mediasolutions.saleservicebot.utills.constants.Message;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TgService extends TelegramLongPollingBot {

    private final MakeService makeService;
    private final FileRepository fileRepository;
    private final TgUserRepository tgUserRepository;

    @Override
    public String getBotUsername() {
//        return "uygogo_bot";
        return "hayat_rasmiydiller_bot";
    }

    @Override
    public String getBotToken() {
//        return "5049026983:AAHjxVS4KdTmMLp4x_ir9khH4w1tB4h6pPQ";
        return "6547891262:AAFHC38MvnNiMGi3KotogPbHWDgho1So-iE";
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        String chatId1 = makeService.getChatId(update);
        TgUser tgUser = tgUserRepository.findByChatId(chatId1);

        if (tgUserRepository.existsByChatId(chatId1) && tgUser.isBlocked()) {
            execute(new SendMessage(chatId1, makeService.getMessage(Message.YOU_ARE_BLOCKED,
                    makeService.getUserLanguage(chatId1))));
        } else if (update.hasMessage() && update.getMessage().hasText() &&
                update.getMessage().getText().equals("/start")) {
            execute(makeService.whenStart(update));
        } else if (makeService.getUserState(chatId1).equals(BotState.START) &&
                (!update.hasMessage() ||
                        !update.getMessage().hasText() ||
                        !update.getMessage().getText().equals("/start"))
                && !(makeService.getChatId(update).equals(makeService.CHANNEL_ID_APP) ||
                makeService.getChatId(update).equals(makeService.CHANNEL_ID_SUG_COMP) ||
                makeService.getChatId(update).equals(makeService.CHANNEL_ID_ORDER))) {
            execute(makeService.whenRerun(update));
        } else {
            System.out.println(update);
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String text = update.getMessage().getText();
                if (makeService.getUserState(chatId).equals(BotState.CHOOSE_LANG) &&
                        text.equals(makeService.getMessage(Message.UZBEK, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenUz(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_LANG) &&
                        text.equals(makeService.getMessage(Message.RUSSIAN, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenRu(update));
                } else if (makeService.getUserState(chatId).equals(BotState.ENTER_PHONE_NUMBER)) {
                    execute(makeService.whenPhoneNumber(update));
                } else if (makeService.getUserState(chatId).equals(BotState.INCORRECT_PHONE_FORMAT)) {
                    execute(makeService.whenIncorrectPhoneFormat(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_MARKET)) {
                    execute(makeService.whenChooseMarket(update));
                } else if (makeService.getUserState(chatId).equals(BotState.PENDING)) {
                    execute(makeService.whenPending(update));
                    execute(makeService.whenSendAppToChannel(update));
                } else if (text.equals(makeService.getMessage(Message.MENU_SUG_COMP, makeService.getUserLanguage(chatId))) &&
                        makeService.getUserState(chatId).equals(BotState.CHOOSE_MENU)) {
                    execute(makeService.whenSuggestAndComplaint(update));
                } else if (makeService.getUserState(chatId).equals(BotState.SEND_SUGGESTION_COMPLAINT)) {
                    execute(makeService.whenReceiveSuggestAndComplaint(update));
                    execute(makeService.whenResponseToSugComp(update));
                } else if (text.equals(makeService.getMessage(Message.MENU_SETTINGS, makeService.getUserLanguage(chatId))) &&
                        makeService.getUserState(chatId).equals(BotState.CHOOSE_MENU)) {
                    execute(makeService.whenSettings1(update));
                    execute(makeService.whenSettings2(update));
                } else if (text.equals(makeService.getMessage(Message.MENU_PRICE_LIST, makeService.getUserLanguage(chatId))) &&
                        makeService.getUserState(chatId).equals(BotState.CHOOSE_MENU)) {
                    if (!fileRepository.findAll().isEmpty())
                        execute(makeService.sendFile(update));
                    else
                        execute(makeService.whenFileNotExists(update));
                } else if (text.equals(makeService.getMessage(Message.MENU_ABOUT_US, makeService.getUserLanguage(chatId))) &&
                        makeService.getUserState(chatId).equals(BotState.CHOOSE_MENU)) {
                    execute(makeService.whenAboutUs(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_NAME)) {
                    execute(makeService.whenChangeName2(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_PHONE_NUMBER)) {
                    execute(makeService.whenChangePhoneNumber2(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_MARKET)) {
                    execute(makeService.whenChangeMarket2(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_LANGUAGE)) {
                    execute(makeService.whenChangeLanguage2(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_MENU) &&
                        text.equals(makeService.getMessage(Message.MENU_ORDER, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenOrder(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_CATEGORY) &&
                        text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenMenuForExistedUser(update));
                } else if ((makeService.getUserState(chatId).equals(BotState.CHOOSE_CATEGORY)) &&
                        makeService.getCategoryName(makeService.getUserLanguage(chatId)).contains(text)) {
                    execute(makeService.whenChosenCategory(update, text));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT) &&
                        text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenOrder(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT) &&
                        makeService.getProductName(makeService.getUserLanguage(chatId)).contains(text)) {
                    execute(makeService.whenChosenProduct(update, text));
                } else if (makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT) &&
                        makeService.numbersUpTo().contains(text)) {
                    execute(makeService.whenAddProductToBasket(update, text));
                    execute(makeService.whenChosenCategory2(update));
                } else if (makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT) &&
                        text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenBackInProductCount(update));
                } else if ((makeService.getUserState(chatId).equals(BotState.CHOOSE_CATEGORY) ||
                        makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT) ||
                        makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT)) &&
                        text.length() > 6 &&
                        text.substring(0, 6).equals(makeService.getMessage(Message.BASKET,
                                makeService.getUserLanguage(chatId)).substring(0, 6))) {
                    deleteMessage(update);
                    execute(makeService.whenBasket(update));
                } else if (makeService.getUserState(chatId).equals(BotState.WRITE_COMMENT)) {
                    execute(makeService.whenOrderCreated1(update));
                    execute(makeService.whenOrderCreated2(update));
                }

            } else if (update.hasMessage() && update.getMessage().hasContact()) {
                String chatId = update.getMessage().getChatId().toString();
                if (makeService.getUserState(chatId).equals(BotState.CHOOSE_MARKET)) {
                    execute(makeService.whenChooseMarket(update));
                } else if (makeService.getUserState(chatId).equals(BotState.INCORRECT_PHONE_FORMAT)) {
                    execute(makeService.whenIncorrectPhoneFormat(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_PHONE_NUMBER)) {
                    execute(makeService.whenChangePhoneNumber2(update));
                }
            } else if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();
                if (data.startsWith("accept")) {
                    execute(makeService.acceptUser(update));
                    execute(makeService.whenAcceptSendMessageToUser(update));
                    execute(makeService.whenMenu(update));
                } else if (data.startsWith("reject")) {
                    execute(makeService.rejectUser(update));
                    execute(makeService.whenRejectSendMessageToUser(update));
                } else if (data.equals("changeName")) {
                    execute(makeService.whenChangeName1(update));
                } else if (data.equals("changePhone")) {
                    execute(makeService.deleteMessageForCallback(update));
                    execute(makeService.whenChangePhoneNumber1(update));
                } else if (data.equals("changeMarket")) {
                    execute(makeService.deleteMessageForCallback(update));
                    execute(makeService.whenChangeMarket1(update));
                } else if (data.equals("changeLanguage")) {
                    execute(makeService.deleteMessageForCallback(update));
                    execute(makeService.whenChangeLanguage1(update));
                } else if (data.equals("menu")) {
                    execute(makeService.deleteMessageForCallback(update));
                    execute(makeService.whenMenuForExistedUser(update));
                } else if (data.equals("clear")) {
                    execute(makeService.whenClear(update));
                    execute(makeService.whenMenuForExistedUser(update));
                } else if (data.startsWith("minus")) {
                    execute(makeService.whenMinus(update));
                } else if (data.startsWith("delete")) {
                    execute(makeService.whenDelete(update));
                } else if (data.startsWith("plus")) {
                    execute(makeService.whenPlus(update));
                } else if (data.equals("officialOrder")) {
                    execute(makeService.whenOfficialOrder1(update));
                    execute(makeService.whenOfficialOrder(update));
                } else if (data.startsWith("111")) {
                    execute(makeService.whenAcceptOrder1(update));
                    execute(makeService.whenAcceptOrderForChannel(update));
                } else if (data.startsWith("222")) {
                    execute(makeService.whenAcceptOrder2(update));
                    execute(makeService.whenAcceptOrderForChannel(update));
                } else if (data.startsWith("333")) {
                    execute(makeService.whenAcceptOrder3(update));
                    execute(makeService.whenAcceptOrderForChannel(update));
                } else if (data.startsWith("444")) {
                    execute(makeService.whenRejectOrder1(update));
                    execute(makeService.whenRejectOrder2(update));
                }
            } else if (update.hasMessage() && update.getMessage().hasLocation()) {
                String chatId = update.getMessage().getChatId().toString();
                if (makeService.getUserState(chatId).equals(BotState.SEND_LOCATION)) {
                    execute(makeService.whenComment(update));
                }
            }
        }
    }

    public void deleteMessage(Update update) throws TelegramApiException {
        SendMessage sendMessageRemove = new SendMessage();
        sendMessageRemove.setChatId(update.getMessage().getChatId().toString());
        sendMessageRemove.setText(".");
        sendMessageRemove.setReplyMarkup(new ReplyKeyboardRemove(true));
        org.telegram.telegrambots.meta.api.objects.Message message = execute(sendMessageRemove);
        DeleteMessage deleteMessage = new DeleteMessage(update.getMessage().getChatId().toString(), message.getMessageId());
        execute(deleteMessage);

    }

}
