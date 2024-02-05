package uz.mediasolutions.saleservicebot.manual;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    private Boolean success = false;

    private String message;

    private T data;

    private List<ErrorData> errors;

    private Long totalElements;

    //RESPONSE FAQAT BITTA BOOLEANDAN IBORAT BO'LSA
    private ApiResult(Boolean success) {
        this.success = success;
    }

    //SUCCESS RESPONSE WITH DATA
    private ApiResult(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    private ApiResult(T data, Boolean success, Long totalElements) {
        this.data = data;
        this.success = success;
        this.totalElements = totalElements;
    }

    //SUCCESS RESPONSE WITH MESSAGE
    private ApiResult(String message) {
        this.success = Boolean.TRUE;
        this.message = message;
    }

    //ERROR RESPONSE WITH MESSAGE AND ERROR CODE
    private ApiResult(String errorMsg, Integer errorCode) {
        this.success = false;
        this.errors = Collections.singletonList(new ErrorData(errorMsg, errorCode));
    }


    //ERROR RESPONSE WITH ERROR DATA LIST
    private ApiResult(List<ErrorData> errors) {
        this.success = false;
        this.errors = errors;
    }

    public static <E> ApiResult<E> success(E data) {
        return new ApiResult<>(true, data);
    }
    public static <E> ApiResult<E> successPageable(E data, Long totalElements) {
        return new ApiResult<>(data, true, totalElements);
    }
    public static <E> ApiResult<E> success(E data, boolean notMessage) {
        return new ApiResult<>(true, data);
    }

    public static <E> ApiResult<E> success() {
        return new ApiResult<>(true);
    }

    public static ApiResult<String> success(String message) {
        return new ApiResult<>(message);
    }

    public static ApiResult<ErrorData> error(List<ErrorData> errors) {
        return new ApiResult<>(String.valueOf(errors));
    }

    public static ApiResult<ErrorData> error(String errorMsg, Integer errorCode) {
        return new ApiResult<>(errorMsg, errorCode);
    }

}
