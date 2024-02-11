package uz.mediasolutions.saleservicebot.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private UUID id;

    private Long number;

    private Double lan;

    private Double lat;

    private String comment;

    private String orderedTime;

    private TgUserDTO user;

    private List<ChosenProductDTO> products;

}
