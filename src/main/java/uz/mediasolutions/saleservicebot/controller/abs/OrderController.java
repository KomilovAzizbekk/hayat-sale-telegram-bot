package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.web.bind.annotation.*;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.OrderDTO;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

import java.util.UUID;

@RequestMapping(OrderController.ORDER)
public interface OrderController {

    String ORDER = Rest.BASE_PATH + "order/";
    String GET = "get/{id}";
    String DELIVERED = "delivered/{id}";

    @GetMapping(GET)
    ApiResult<OrderDTO> getOrder(@PathVariable UUID id);

    @PostMapping(DELIVERED)
    ApiResult<?> delivered(@PathVariable UUID id);

}
