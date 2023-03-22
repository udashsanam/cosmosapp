package com.cosmos.common.exception;

import com.cosmos.common.model.ApiError;
import com.google.protobuf.Api;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandlerController extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "Malformed JSON request";
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
	}

	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, String> errors = new LinkedHashMap<>();
		//Get all errors
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errors);
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

	@ExceptionHandler(value = {CustomException.class})
	protected ResponseEntity<Object> handleCustomException(CustomException ex) {
		ApiError apiError = new ApiError(ex.getHttpStatus(), ex.getMessage());
		apiError.setMessage(ex.getMessage());
		return buildResponseEntity(apiError);
	}

//	@ExceptionHandler(Exception.class)
//	protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
//		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
//	}

	@ExceptionHandler(value = { ConstraintViolationException.class, DataIntegrityViolationException.class })
	public final ResponseEntity<Object> handleBadRequest(final RuntimeException ex, final WebRequest request) {
		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
	}

	@ExceptionHandler(value = { EntityNotFoundException.class})
	public final ResponseEntity<Object> handleEntityNotFound(final RuntimeException ex) {
		return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
	}
}