package uz.mediasolutions.saleservicebot.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String name;

    private String phoneNumber;

    private MarketDTO market;

    private boolean accepted;

}
