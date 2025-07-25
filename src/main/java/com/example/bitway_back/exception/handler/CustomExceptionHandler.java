package com.example.bitway_back.exception.handler;

import com.example.bitway_back.dto.ErrorResponseDto;
import com.example.bitway_back.exception.CustomException;
import com.example.bitway_back.exception.ErrorCode;
import org.springframework.aop.AopInvocationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.ConnectException;
import java.sql.SQLException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorResponseDto(exception.getStatus(), exception.getMsg()));
    }

    // MissingServletRequestParameterException, MissingPathVariableException, HttpMessageNotReadableException
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            HttpMessageNotReadableException.class,
    })
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMissingParameterException() {
        return new ErrorResponseDto(ErrorCode.BAD_REQUEST);
    }

    // MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleCustomException(MethodArgumentNotValidException exception) {

        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(),
                exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
    }

    // NoHandlerFoundException
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNoHandlerFoundException() {
        return new ErrorResponseDto(ErrorCode.NOT_FOUND);
    }

    // HttpRequestMethodNotSupportedException
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponseDto handleMethodNotSupportedException() {
        return new ErrorResponseDto(ErrorCode.METHOD_NOT_ALLOWED);
    }

    // ConnectException
    @ExceptionHandler(ConnectException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    public ErrorResponseDto handleTimeOutException() {
        return new ErrorResponseDto(ErrorCode.REQUEST_TIMEOUT);
    }

    // InternalServerError
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleInternalError(HttpServerErrorException.InternalServerError exception) {
        return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

    // BadCredentialsException
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleBadCredentialsException() {
        return new ErrorResponseDto(ErrorCode.BAD_CREDENTIALS);
    }

    // AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleAccessDeniedException() {
        return new ErrorResponseDto(ErrorCode.FORBIDDEN_ACCESS);
    }

    // NullPointerException
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleNullPointerException() {
        return new ErrorResponseDto(ErrorCode.BAD_REQUEST);
    }

    // InvalidDataAccessResourceUsageException, BadSqlGrammarException, SQLException
    @ExceptionHandler({
            InvalidDataAccessResourceUsageException.class,
            BadSqlGrammarException.class,
            SQLException.class
    })
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleInvalidDataAccessResourceUsageException() {
        return new ErrorResponseDto(ErrorCode.SQL_EXCEPTION);
    }
    
    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }
    
    // AopInvocationException
    @ExceptionHandler(AopInvocationException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleAopInvocationException() {
        return new ErrorResponseDto(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
