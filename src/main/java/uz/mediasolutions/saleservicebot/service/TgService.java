package uz.mediasolutions.saleservicebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.saleservicebot.manual.BotState;
import uz.mediasolutions.saleservicebot.utills.constants.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TgService extends TelegramLongPollingBot {

    private final MakeService makeService;

    @Override
    public String getBotUsername() {
        return "uygogo_bot";
    }

    @Override
    public String getBotToken() {
        return "5049026983:AAHjxVS4KdTmMLp4x_ir9khH4w1tB4h6pPQ";
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() &&
                update.getMessage().getText().equals("/start")) {
            execute(makeService.whenStart(update));
        }
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
            } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_NAME)) {
                execute(makeService.whenChangeName2(update));
            } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_PHONE_NUMBER)) {
                execute(makeService.whenChangePhoneNumber2(update));
            } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_MARKET)) {
                execute(makeService.whenChangeMarket2(update));
            } else if (makeService.getUserState(chatId).equals(BotState.CHANGE_LANGUAGE)) {
                execute(makeService.whenChangeLanguage2(update));
            } else if (text.equals(makeService.getMessage(Message.MENU_ORDER, makeService.getUserLanguage(chatId))) &&
                    makeService.getUserState(chatId).equals(BotState.CHOOSE_MENU)) {
                execute(makeService.whenOrder(update));
            } else if (text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId))) &&
                    makeService.getUserState(chatId).equals(BotState.CHOOSE_CATEGORY)) {
                execute(makeService.whenMenuForExistedUser(update));
            } else if (makeService.getCategoryName(makeService.getUserLanguage(chatId)).contains(text)) {
                execute(makeService.whenChosenCategory(update, text));
            } else if (text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId))) &&
                    makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT)) {
                execute(makeService.whenOrder(update));
            } else if (makeService.getProductName(makeService.getUserLanguage(chatId)).contains(text) &&
                    makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT)) {
                execute(makeService.whenChosenProduct(update, text));
            } else if (makeService.numbersUpTo().contains(text) &&
                    makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT)) {
                execute(makeService.whenAddProductToBasket(update, text));
                execute(makeService.whenOrder(update));
            } else if (text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId))) &&
                    makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT)) {
                execute(makeService.whenBackInProductCount(update));
            } else if (text.substring(0, 6).equals(makeService.getMessage(Message.BASKET,
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
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            if (data.startsWith("accept")) {
                execute(makeService.acceptUser(update));
                execute(makeService.whenAcceptSendMessageToUser(update));
                execute(makeService.whenMenu(update));
            } else if (data.startsWith("reject")) {
//                execute(makeService.rejectUser(update));
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
                execute(makeService.whenOfficialOrder(update));
            }
        } else if (update.hasMessage() && update.getMessage().hasLocation()) {
            execute(makeService.whenComment(update));
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
