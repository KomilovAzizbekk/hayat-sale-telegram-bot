package uz.mediasolutions.saleservicebot.utills;


import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.mediasolutions.saleservicebot.entity.User;
import uz.mediasolutions.saleservicebot.secret.JwtAuthFilter;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@Component
public class CommonUtils {

    private static JwtAuthFilter jwtAuthFilter;


    //SECURITY DAN USERNI OLISH
    public static User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals("" + authentication.getPrincipal()))
            return null;
        return (User) authentication.getPrincipal();
    }

    //SECURITY CONTEX GA USERNI JOYLASH
    public static void setUserToSecurityContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>()));
    }

    public static String createMessage(String mes, MessageSource messageSource, Object[] objects) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(mes, objects, locale);
    }


    // TIZIMDA TURGAN RequestContextHolder dan HttpServletRequest ni olamiz
    public static HttpServletRequest currentRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return Optional.of(servletRequestAttributes).map(ServletRequestAttributes::getRequest).orElse(null);
    }

    public static User getUserFromRequest() {
        String token = currentRequest().getHeader(Rest.AUTHORIZATION_HEADER);
        if (token != null) {
            User user = jwtAuthFilter.getUserFromBearerToken(token);
            return user!=null?user:new User();
        }
        return new User();
    }


//    public static AnonymousUser getAnonymousUserFromRequest() {
//        HttpServletRequest httpServletRequest = currentRequest();
//        String cookie = httpServletRequest.getHeader(Rest.COOKIE_KEY);
//
//        //BU XATO KETGANDA FRONTDAN COOKIE MALUMOTLARI OLISH UCHUN  ** api/v1/cookie/data  GET SOROVINI YUBORIB OLISHI KERAK
//        //TODO AYTISH ESIMDAN CHIQMASIN
//        if (cookie == null)
//            throw RestException.restThrow(Message.EMPTY_COOKIE, HttpStatus.BAD_REQUEST);
//
//        Optional<AnonymousUser> optionalAnonymousUser = anonymousUserRepository.findByCookieId(cookie);
//        return optionalAnonymousUser.orElseThrow(() -> RestException.restThrow(Message.EMPTY_COOKIE, HttpStatus.BAD_REQUEST));
//
//    }


    //BU METHOD FAQAT MIJOZNI TOKEN ORQALI KELGANINI YOKI COOKIE ORQALI KELGANINI BILISH UCHUN
    public static boolean isUser() {
        String token = currentRequest().getHeader(Rest.AUTHORIZATION_HEADER);
        return null != jwtAuthFilter.getUserFromBearerToken(token);
    }


}
