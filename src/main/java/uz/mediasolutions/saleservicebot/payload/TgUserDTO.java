package uz.mediasolutions.saleservicebot.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TgUserDTO {

    private String name;

    private String phoneNumber;

    private String market;

}
