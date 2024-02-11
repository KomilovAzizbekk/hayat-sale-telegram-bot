package uz.mediasolutions.saleservicebot.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChosenProductDTO {

    private String productName;

    private Integer count;

}
