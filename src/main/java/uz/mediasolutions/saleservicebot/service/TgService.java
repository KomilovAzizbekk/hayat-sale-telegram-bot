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

@Service
@RequiredArgsConstructor
public class TgService extends TelegramLongPollingBot {

    private final MakeService makeService;

    @Override
    public String getBotUsername() {
        return "sakaka_bot";
    }

    @Override
    public String getBotToken() {
        return "6052104473:AAEscLILevwPMcG_00PYqAf-Kpb7eIUCIGg";
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
            }
        }

    }

}
