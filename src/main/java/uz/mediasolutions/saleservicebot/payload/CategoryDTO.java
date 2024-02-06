package uz.mediasolutions.saleservicebot.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private UUID id;

    private String nameUz;

    private String nameRu;

    private Integer number;

}
