package uz.mediasolutions.saleservicebot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.mediasolutions.saleservicebot.entity.ChosenProduct;
import uz.mediasolutions.saleservicebot.entity.Order;
import uz.mediasolutions.saleservicebot.entity.TgUser;
import uz.mediasolutions.saleservicebot.enums.StatusName;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.ChosenProductDTO;
import uz.mediasolutions.saleservicebot.payload.OrderDTO;
import uz.mediasolutions.saleservicebot.payload.TgUserDTO;
import uz.mediasolutions.saleservicebot.repository.OrderRepository;
import uz.mediasolutions.saleservicebot.repository.StatusRepository;
import uz.mediasolutions.saleservicebot.service.MakeService;
import uz.mediasolutions.saleservicebot.service.TgService;
import uz.mediasolutions.saleservicebot.service.abs.OrderService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;
    private final TgService tgService;
    private final MakeService makeService;


    @Override
    public ApiResult<OrderDTO> getOrder(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        OrderDTO dto = toOrderDTO(order);
        return ApiResult.success(dto);
    }

    @SneakyThrows
    @Override
    public ApiResult<?> delivered(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));
        order.setStatus(statusRepository.findByName(StatusName.DELIVERED));
        orderRepository.save(order);
        tgService.execute(makeService.whenDelivered(order.getTgUser().getChatId(), order));
        tgService.execute(makeService.whenDeliveredEdit(id));
        return ApiResult.success("RECEIVED");
    }


    private OrderDTO toOrderDTO(Order order) {
        String orderedTime = order.getOrderedTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));
        Order found = orderRepository.findById(order.getId()).orElseThrow(
                () -> RestException.restThrow("ID NOT FOUND", HttpStatus.BAD_REQUEST));

        return OrderDTO.builder()
                .id(order.getId())
                .number(order.getNumber())
                .lan(order.getLan())
                .lat(order.getLat())
                .user(toTgUserDTO(order.getTgUser()))
                .status(found.getStatus().getName().name())
                .orderedTime(orderedTime)
                .comment(order.getComment())
                .products(toChosenProductDTOList(order.getChosenProducts()))
                .build();
    }

    private ChosenProductDTO toChosenProductDTO(ChosenProduct chosenProduct) {
        return ChosenProductDTO.builder()
                .count(chosenProduct.getCount())
                .productName(chosenProduct.getProduct().getNameUz())
                .build();
    }

    private List<ChosenProductDTO> toChosenProductDTOList(List<ChosenProduct> chosenProducts) {
        List<ChosenProductDTO> productDTOS = new ArrayList<>(chosenProducts.size());
        for (ChosenProduct chosenProduct : chosenProducts) {
            productDTOS.add(toChosenProductDTO(chosenProduct));
        }
        return productDTOS;
    }

    private TgUserDTO toTgUserDTO(TgUser tgUser) {
        return TgUserDTO.builder()
                .name(tgUser.getName())
                .phoneNumber(tgUser.getPhoneNumber())
                .market(tgUser.getMarket().getNameUz())
                .build();
    }


}
