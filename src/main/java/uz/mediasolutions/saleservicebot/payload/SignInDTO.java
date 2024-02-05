package uz.mediasolutions.saleservicebot.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInDTO {

    @NotBlank(message = "enter username")
    private String username;

    @NotBlank(message = "enter password")
    private String password;

}
