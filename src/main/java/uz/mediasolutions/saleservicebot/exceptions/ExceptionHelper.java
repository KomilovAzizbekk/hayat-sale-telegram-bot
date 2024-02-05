package uz.mediasolutions.saleservicebot.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import uz.mediasolutions.saleservicebot.component.MessageService;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.manual.ErrorData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHelper {
    private static final String NOT_FOUND = "_NOT_FOUND";
    private static final String NOT_NULL = "_NOT_NULL";
    private static final String UK_CONSTRAINT = "_UK_CONSTRAINT";

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(MethodArgumentNotValidException ex) {
        List<ErrorData> errors = new ArrayList<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        fieldErrors.forEach(fieldError -> errors.add(
                new ErrorData(
                        getMessage(fieldError.getDefaultMessage()),
                        fieldError.getField(),
                        getErrorCode(fieldError.getDefaultMessage())
                )));

        return new ResponseEntity<>(ApiResult.error(errors), HttpStatus.BAD_REQUEST);
    }

    private String getMessage(String defaultMessage) {
        if (Objects.nonNull(defaultMessage) && defaultMessage.contains("/")) {
            Function<String, String> messageFunc = s -> s.split("/")[0];
            return messageFunc.apply(defaultMessage);
        } else {
            return defaultMessage;
        }
    }

    private int getErrorCode(String defaultMessage) {
        if (Objects.nonNull(defaultMessage) && defaultMessage.contains("/")) {
            Function<String, String> messageFunc = s -> s.split("/")[1];
            return Integer.parseInt(messageFunc.apply(defaultMessage));
        } else {
            return 0;
        }
    }

    @ExceptionHandler(value = {TypeMismatchException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(TypeMismatchException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), Rest.CONFLICT),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ServletRequestBindingException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(ServletRequestBindingException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {MissingServletRequestPartException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(MissingServletRequestPartException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(BindException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionAccessDenied() {
        return new ResponseEntity<>(
                ApiResult.error(MessageService.getMessage("FORBIDDEN_EXCEPTION"), Rest.ACCESS_DENIED),
                HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(value = {MissingPathVariableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionNotFound() {
        return new ResponseEntity<>(
                ApiResult.error(MessageService.getMessage("PATH_NOTFOUND_EXCEPTION"), Rest.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 404),
                HttpStatus.NOT_FOUND);
    }


    //METHOD XATO BO'LSA
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException() {
        return new ResponseEntity<>(
                ApiResult.error("Method error", 405),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = {HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionHttpMediaTypeNotAcceptable() {
        return new ResponseEntity<>(
                ApiResult.error("No acceptable", 406),
                HttpStatus.NOT_ACCEPTABLE);
    }


    //METHOD XATO BO'LSA
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionHttpMediaTypeNotSupported() {
        return new ResponseEntity<>(
                ApiResult.error(MessageService.getMessage("UNSUPPORTED_MEDIA_TYPE"), 415),
                HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(value = {ConversionNotSupportedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(ConversionNotSupportedException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), Rest.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = {HttpMessageNotWritableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(HttpMessageNotWritableException ex) {
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), Rest.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(Exception ex) {
        log.error("EXCEPTION_HELPER:", ex);
        ex.printStackTrace();
        return new ResponseEntity<>(
                ApiResult.error(
                        MessageService.getMessage("ERROR_IN_SERVER"),
                        Rest.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AsyncRequestTimeoutException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(AsyncRequestTimeoutException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                ApiResult.error(ex.getMessage(), 503),
                HttpStatus.SERVICE_UNAVAILABLE);
    }


    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(DataIntegrityViolationException ex) {
//        if (isDev())
//            ex.printStackTrace();
        try {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();
            SQLException sqlException = ((ConstraintViolationException) ex.getCause()).getSQLException();
            String message = sqlException.getMessage();


            String sqlState = sqlException.getSQLState();

            //"23503" foreign key ulanolmasa
            //"23502" null property uchun

            //agar biror column dan nullable false qo'yilganda tushadigan exception
            if (Objects.equals(sqlState, "23502")) {
                String columnName = constraintViolationException.getConstraintName().toUpperCase(Locale.ROOT);
                String clientMessage = columnName + NOT_NULL;

                System.out.println(clientMessage);

                return new ResponseEntity<>(
                        ApiResult.error(MessageService.getMessage(clientMessage), 400),
                        HttpStatus.BAD_REQUEST
                );
            }
            if (Objects.equals(sqlState, "23503")) {


                String detail = "Detail:";

                //DETAIL: SO'ZINI INDEKSINI ANIQLAB OLYAPMAN ARENTIR SIFATIDA
                int arentir = message.indexOf(detail);

                //DETAIL SO'ZIDAN KEYINGI OCHILGAN 1-QAVS NI INDEXINI OLIB +1 QO'SHTIM
                int fromColumnName = message.indexOf("(", arentir) + 1;

                //DETAIL SO'ZIDAN KEYINGI YOPILGAN 1-QAVS NI INDEXINI OLIB -3 AYIRDIM
                int toColumnName = message.indexOf(")", fromColumnName) - 3;

                //MESSAGEDAN COLUMN NAME NI QIRQIB OLIB UNI UPPER_CASE QILINDI
                String columnName = message.substring(fromColumnName, toColumnName).toUpperCase(Locale.ROOT);

                //MESSAGE_BY_LANG GA BERISH UCHUN
                String clientMessage = columnName + NOT_FOUND;
                return new ResponseEntity<>(
                        ApiResult.error(MessageService.getMessage(clientMessage), 400),
                        HttpStatus.BAD_REQUEST
                );
            } else if (Objects.equals(sqlState, "23505")) {

                //MESSAGE_BY_LANG GA BERISH UCHUN
                String clientMessage = constraintViolationException.getConstraintName().toUpperCase(Locale.ROOT) + UK_CONSTRAINT;
                return new ResponseEntity<>(
                        ApiResult.error(MessageService.getMessage(clientMessage), 400),
                        HttpStatus.BAD_REQUEST
                );
            }
        } catch (Exception exception) {
//            if (isDev())
//                exception.printStackTrace();
            return new ResponseEntity<>(
                    ApiResult.error("Server error. Please try again", Rest.SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(
                ApiResult.error("Server error. Please try again.", Rest.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // FOYDALANUVCHI TOMONIDAN XATO SODIR BO'LGANDA
    @ExceptionHandler(value = {RestException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(RestException ex) {
        ex.printStackTrace();

        //AGAR RESOURSE TOPILMAGANLIGI XATOSI BO'LSA CLIENTGA QAYSI TABLEDA NIMA TOPILMAGANLIGI HAQIDA XABAR QAYTADI
        if (ex.getFieldName() != null)
            return new ResponseEntity<>(ApiResult.error(ex.getUserMsg(), ex.getErrorCode()), ex.getStatus());
        //AKS HOLDA DOIMIY EXCEPTIONLAR ISHLAYVERADI
        if (ex.getErrors() != null)
            return new ResponseEntity<>(ApiResult.error(ex.getErrors()), ex.getStatus());
        return new ResponseEntity<>(ApiResult.error(ex.getUserMsg(), ex.getErrorCode() != null ? ex.getErrorCode() : ex.getStatus().value()), ex.getStatus());
    }


}
