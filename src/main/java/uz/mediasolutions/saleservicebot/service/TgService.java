package uz.mediasolutions.saleservicebot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.saleservicebot.entity.Basket;
import uz.mediasolutions.saleservicebot.entity.ChosenProduct;
import uz.mediasolutions.saleservicebot.entity.Product;
import uz.mediasolutions.saleservicebot.entity.TgUser;
import uz.mediasolutions.saleservicebot.manual.BotState;
import uz.mediasolutions.saleservicebot.repository.BasketRepository;
import uz.mediasolutions.saleservicebot.repository.ChosenProductRepository;
import uz.mediasolutions.saleservicebot.repository.FileRepository;
import uz.mediasolutions.saleservicebot.repository.TgUserRepository;
import uz.mediasolutions.saleservicebot.utills.constants.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TgService extends TelegramLongPollingBot {

    private final MakeService makeService;
    private final TgUserRepository tgUserRepository;
    private final FileRepository fileRepository;
    private final BasketRepository basketRepository;
    private final ChosenProductRepository chosenProductRepository;

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
                } else if (text.equals("/post")) {
                    execute(makeService.whenPost(update));
                } else if (text.equals("/upload")) {
                    execute(makeService.whenUpload(update));
                } else if (makeService.getUserState(chatId).equals(BotState.UPLOAD_FILE)) {
                    execute(makeService.whenRerun(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_LANG) &&
                        text.equals(makeService.getMessage(Message.RUSSIAN, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenRu(update));
                } else if (makeService.getUserState(chatId).equals(BotState.ENTER_PHONE_NUMBER)) {
                    execute(makeService.whenPhoneNumber(update));
                } else if (makeService.getUserState(chatId).equals(BotState.INCORRECT_PHONE_FORMAT)) {
                    execute(makeService.whenIncorrectPhoneFormat(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_MARKET)) {
                    execute(makeService.whenChooseMarket(update));
//                } else if (makeService.getUserState(chatId).equals(BotState.PENDING)) {
//                    execute(makeService.whenPending(update));
//                    execute(makeService.whenSendAppToChannel(update));
                } else if (makeService.getUserState(chatId).equals(BotState.MENU)) {
                    execute(makeService.whenMenu(update));
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
                    if (fileRepository.existsByName("price"))
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
                } else if (makeService.getUserState(chatId).equals(BotState.POST) &&
                        text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenStart(update));
                } else if (makeService.getUserState(chatId).equals(BotState.POST) &&
                        !text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId)))) {
                    whenPostText(update);
                    execute(makeService.whenStart(update));
                } else if ((makeService.getUserState(chatId).equals(BotState.CHOOSE_CATEGORY)) &&
                        makeService.getCategoryName(makeService.getUserLanguage(chatId)).contains(text)) {
                    execute(makeService.whenChosenCategory(update, text));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT) &&
                        text.equals(makeService.getMessage(Message.BACK, makeService.getUserLanguage(chatId)))) {
                    execute(makeService.whenOrder(update));
                } else if (makeService.getUserState(chatId).equals(BotState.CHOOSE_PRODUCT) &&
                       makeService.getProductName(makeService.getUserLanguage(chatId)).contains(text)) {
                    deleteMessage(update);
                    Product productByName = makeService.getProductByName(text, makeService.getUserLanguage(chatId));
                    execute(makeService.whenChosenProduct(update, productByName.getForUnique(), "0"));
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

            } else if (update.hasMessage() && update.getMessage().hasDocument()) {
                if (makeService.getUserState(chatId1).equals(BotState.UPLOAD_FILE)) {
                    execute(makeService.saveFile(update));
                } else if (makeService.getUserState(chatId1).equals(BotState.POST)) {
                    whenPostDocument(update);
                    execute(makeService.whenStart(update));
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
                String chatId = update.getCallbackQuery().getFrom().getId().toString();
                String data = update.getCallbackQuery().getData();
//                if (data.startsWith("accept") && (Objects.equals(chatId, makeService.CHAT_ID_1) ||
//                        Objects.equals(chatId, makeService.CHAT_ID_2) ||
//                        Objects.equals(chatId, makeService.CHAT_ID_3))) {
//                    execute(makeService.acceptUser(update));
//                    execute(makeService.whenAcceptSendMessageToUser(update));
//                    execute(makeService.whenMenu(update));
//                } else if (data.startsWith("reject") && (Objects.equals(chatId, makeService.CHAT_ID_1) ||
//                        Objects.equals(chatId, makeService.CHAT_ID_2) ||
//                        Objects.equals(chatId, makeService.CHAT_ID_3))) {
//                    execute(makeService.rejectUser(update));
//                    execute(makeService.whenRejectSendMessageToUser(update));
//                }
                if (data.equals("changeName")) {
                    execute(makeService.whenChangeName1(update));
                } else if (makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT) &&
                        data.equals("basket")) {
                    whenBasket1(update);
                } else if (makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT) &&
                        data.substring(0,1).matches("\\d")) {
                    execute(makeService.whenChosenProduct1(update, Integer.valueOf(data.substring(1)),
                            data.substring(0,1)));
                } else if (makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT) &&
                        data.startsWith("continue")) {
                    execute(makeService.whenAddProductToBasket(update, data.substring(8)));
                    execute(makeService.whenChosenCategory2(update));
                } else if (data.substring(0,1).equals("❌")) {
                    execute(makeService.whenChosenProduct1(update, Integer.valueOf(data.substring(1)),
                            data.substring(0,1)));
                } else if (makeService.getUserState(chatId).equals(BotState.PRODUCT_COUNT) &&
                        data.equals("back")) {
                    execute(makeService.edit(update));
                    execute(makeService.whenBackInProductCount(update));
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
                } else {
                    boolean b = Objects.equals(chatId, makeService.CHAT_ID_1) ||
                            Objects.equals(chatId, makeService.CHAT_ID_2) ||
                            Objects.equals(chatId, makeService.CHAT_ID_3);
                    if (data.startsWith("111") && b) {
                        execute(makeService.whenAcceptOrder1(update));
                        execute(makeService.whenAcceptOrderForChannel(update));
                    } else if (data.startsWith("222") && b) {
                        execute(makeService.whenAcceptOrder2(update));
                        execute(makeService.whenAcceptOrderForChannel(update));
                    } else if (data.startsWith("333") && b) {
                        execute(makeService.whenAcceptOrder3(update));
                        execute(makeService.whenAcceptOrderForChannel(update));
                    } else if (data.startsWith("444") && b) {
                        execute(makeService.whenRejectOrder1(update));
                        execute(makeService.whenRejectOrder2(update));
                    }
                }
            } else if (update.hasMessage() && update.getMessage().hasLocation()) {
                String chatId = update.getMessage().getChatId().toString();
                if (makeService.getUserState(chatId).equals(BotState.SEND_LOCATION)) {
                    execute(makeService.whenComment(update));
                }
            } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
                if (makeService.getUserState(chatId1).equals(BotState.POST)) {
                    whenPostPhoto(update);
                    execute(makeService.whenStart(update));
                }
            } else if (update.hasMessage() && update.getMessage().hasAudio()) {
                if (makeService.getUserState(chatId1).equals(BotState.POST)) {
                    whenPostAudio(update);
                    execute(makeService.whenStart(update));
                }
            } else if (update.hasMessage() && update.getMessage().hasVideo()) {
                if (makeService.getUserState(chatId1).equals(BotState.POST)) {
                    whenPostVideo(update);
                    execute(makeService.whenStart(update));
                }
            } else if (update.hasMessage() && update.getMessage().hasVoice()) {
                if (makeService.getUserState(chatId1).equals(BotState.POST)) {
                    whenPostVoice(update);
                    execute(makeService.whenStart(update));
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


    @SneakyThrows
    public void whenPostText(Update update) {
        String chatId = makeService.getChatId(update);

        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId))
                execute(new SendMessage(user.getChatId(), update.getMessage().getText()));
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT,
                        makeService.getUserLanguage(chatId)), users.size())));
    }

    @SneakyThrows
    public void whenPostDocument(Update update) {
        String fileId1 = update.getMessage().getDocument().getFileId();
        String caption = update.getMessage().getCaption();
        String chatId = makeService.getChatId(update);

        SendDocument sendDocument = new SendDocument();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendDocument.setChatId(user.getChatId());
                sendDocument.setDocument(new InputFile(fileId1));
                if (caption != null)
                    sendDocument.setCaption(caption);
                execute(sendDocument);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT,
                        makeService.getUserLanguage(chatId)), users.size())));
    }

    @SneakyThrows
    public void whenPostPhoto(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getPhoto().get(0).getFileId();
        String caption = update.getMessage().getCaption();
        SendPhoto sendPhoto = new SendPhoto();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendPhoto.setChatId(user.getChatId());
                sendPhoto.setPhoto(new InputFile(fileId1));
                if (caption != null)
                    sendPhoto.setCaption(caption);
                execute(sendPhoto);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT,
                        makeService.getUserLanguage(chatId)), users.size())));
    }

    @SneakyThrows
    public void whenPostAudio(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getAudio().getFileId();
        String caption = update.getMessage().getCaption();
        SendAudio sendAudio = new SendAudio();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendAudio.setChatId(user.getChatId());
                sendAudio.setAudio(new InputFile(fileId1));
                if (caption != null)
                    sendAudio.setCaption(caption);
                execute(sendAudio);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT,
                        makeService.getUserLanguage(chatId)), users.size())));
    }

    @SneakyThrows
    public void whenPostVideo(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getVideo().getFileId();
        String caption = update.getMessage().getCaption();
        SendVideo sendVideo = new SendVideo();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendVideo.setChatId(user.getChatId());
                sendVideo.setVideo(new InputFile(fileId1));
                if (caption != null)
                    sendVideo.setCaption(caption);
                execute(sendVideo);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT,
                        makeService.getUserLanguage(chatId)), users.size())));
    }

    @SneakyThrows
    public void whenPostVoice(Update update) {
        String chatId = makeService.getChatId(update);
        String fileId1 = update.getMessage().getVoice().getFileId();
        String caption = update.getMessage().getCaption();
        SendVoice sendVoice = new SendVoice();
        List<TgUser> users = tgUserRepository.findAll();
        for (TgUser user : users) {
            if (!Objects.equals(user.getChatId(), chatId)) {
                sendVoice.setChatId(user.getChatId());
                sendVoice.setVoice(new InputFile(fileId1));
                if (caption != null)
                    sendVoice.setCaption(caption);
                execute(sendVoice);
            }
        }
        execute(new SendMessage(chatId,
                String.format(makeService.getMessage(Message.POST_SENT,
                        makeService.getUserLanguage(chatId)), users.size())));
    }

    @SneakyThrows
    public void whenBasket1(Update update) {
        String chatId = makeService.getChatId(update);
        String language = makeService.getUserLanguage(chatId);

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
        EditMessageText editMessageText = new EditMessageText();
        if (!basket.getChosenProducts().isEmpty()) {
            editMessageText.setText(String.format(makeService.getMessage(Message.PRODUCTS_IN_BASKET, language),
                    makeService.getChosenProductsNameAndCount(chatId, language)));
            editMessageText.setChatId(chatId);
            editMessageText.setReplyMarkup(makeService.forWhenBasketInline(update));
            editMessageText.enableHtml(true);
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            execute(editMessageText);
        } else {
            editMessageText.setText(makeService.getMessage(Message.EMPTY_BASKET, makeService.getUserLanguage(chatId)));
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            makeService.setUserState(chatId, BotState.CHOOSE_MENU);
            execute(editMessageText);
            execute(makeService.whenMenuForExistedUser(update));
        }
    }

    public boolean existsProduct(Update update, String text) {
        boolean b = false;
        String chatId = makeService.getChatId(update);
        List<String> productName = makeService.getProductName(makeService.getUserLanguage(chatId));
        for (String s : productName) {
            if (Objects.equals(s, text)) {
                b = true;
                break;
            }
        }
        return b;
    }

}
