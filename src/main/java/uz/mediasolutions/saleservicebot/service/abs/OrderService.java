package uz.mediasolutions.saleservicebot.service.abs;

import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.OrderDTO;

import java.util.UUID;

public interface OrderService {
    public ApiResult<OrderDTO> getOrder(UUID id);

    ApiResult<?> delivered(UUID id);
}
