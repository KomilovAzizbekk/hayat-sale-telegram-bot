package uz.mediasolutions.saleservicebot.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private UUID id;

    @NotBlank(message = "NameUz cannot be blank")
    private String nameUz;

    @NotBlank(message = "NameRu cannot be blank")
    private String nameRu;

    @NotNull(message = "Number cannot be null")
    private Integer number;

}
