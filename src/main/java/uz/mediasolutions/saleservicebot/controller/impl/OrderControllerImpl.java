package uz.mediasolutions.saleservicebot.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import uz.mediasolutions.saleservicebot.controller.abs.OrderController;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.OrderDTO;
import uz.mediasolutions.saleservicebot.service.abs.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderControllerImpl implements OrderController {

    private final OrderService orderService;

    @Override
    public ApiResult<OrderDTO> getOrder(UUID id) {
        return orderService.getOrder(id);
    }

    @Override
    public ApiResult<?> delivered(UUID id) {
        return orderService.delivered(id);
    }
}
