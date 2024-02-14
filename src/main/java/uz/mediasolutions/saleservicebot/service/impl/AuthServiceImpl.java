package uz.mediasolutions.saleservicebot.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.mediasolutions.saleservicebot.exceptions.RestException;
import uz.mediasolutions.saleservicebot.utills.constants.Message;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;
import uz.mediasolutions.saleservicebot.entity.User;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.payload.SignInDTO;
import uz.mediasolutions.saleservicebot.payload.TokenDTO;
import uz.mediasolutions.saleservicebot.repository.UserRepository;
import uz.mediasolutions.saleservicebot.secret.JwtProvider;
import uz.mediasolutions.saleservicebot.service.abs.AuthService;

import java.sql.Timestamp;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findFirstByUsernameAndEnabledIsTrueAndAccountNonExpiredIsTrueAndAccountNonLockedIsTrueAndCredentialsNonExpiredIsTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public ApiResult<TokenDTO> signIn(SignInDTO signInDTO) {
        usernameNotFoundThrow(signInDTO.getUsername());

        User currentUser = checkUsernameAndPasswordAndEtcAndSetAuthenticationOrThrow(signInDTO.getUsername(), signInDTO.getPassword());
        TokenDTO tokenDTO = generateToken(currentUser);
        return ApiResult.success(tokenDTO);
    }

    @Override
    public TokenDTO generateToken(User user) {
        //HOZIRGI VAQT
        Timestamp issuedAt = new Timestamp(System.currentTimeMillis());

        //USER ORQALI TOKEN OLYABMIZ
        String token = jwtProvider.generateAccessToken(user, issuedAt);

        //TOKEN NI DTO QILIB BERYABMIZ
        return TokenDTO.builder()
                .tokenType(Rest.TYPE_TOKEN)
                .accessToken(token)
                .build();
    }

    @Override
    public User checkUsernameAndPasswordAndEtcAndSetAuthenticationOrThrow(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return (User) authentication.getPrincipal();
        } catch (DisabledException | LockedException | CredentialsExpiredException |
                 UsernameNotFoundException disabledException) {
            throw RestException.restThrow(Message.USER_NOT_FOUND_OR_DISABLED, HttpStatus.BAD_REQUEST);
        } catch (ExpiredJwtException | BadCredentialsException e) {
            throw RestException.restThrow(Message.TOKEN_EXPIRED_OR_BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            throw RestException.restThrow(Message.BAD_REQUEST, HttpStatus.UNAUTHORIZED);
        }
    }


    private void usernameNotFoundThrow(String username){
        if (!userRepository.existsByUsername(username)) {
            throw RestException.restThrow(Message.USERNAME_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
    }

    public void checkPasswordEqualityIfErrorThrow(String password, String prePassword) {
        if (Objects.nonNull(password) && !Objects.equals(password,prePassword)){
            throw RestException.restThrow(Message.MISMATCH_PASSWORDS, HttpStatus.BAD_REQUEST);
        }
    }

    public void usernameIfExistsThrow(String username) {
        if (userRepository.existsByUsername(username)) {
            throw RestException.restThrow(Message.USER_ALREADY_REGISTERED, HttpStatus.BAD_REQUEST);
        }
    }
}
