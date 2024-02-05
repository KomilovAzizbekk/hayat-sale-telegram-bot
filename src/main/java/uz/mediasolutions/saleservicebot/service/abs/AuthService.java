package uz.mediasolutions.saleservicebot.service.abs;

import uz.mediasolutions.saleservicebot.entity.User;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.SignInDTO;
import uz.mediasolutions.saleservicebot.payload.TokenDTO;

public interface AuthService {

    ApiResult<TokenDTO> signIn(SignInDTO signInDTO);

    TokenDTO generateToken(User user);

    User checkUsernameAndPasswordAndEtcAndSetAuthenticationOrThrow(String username, String password);


}
