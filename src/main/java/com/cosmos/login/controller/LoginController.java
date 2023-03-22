package com.cosmos.login.controller;

import com.cosmos.common.exception.CustomException;
import com.cosmos.common.model.ApiMessage;
import com.cosmos.common.security.UniqueCodeGenerator;
import com.cosmos.email.helper.EmailHtmlSender;
import com.cosmos.login.dto.AppUserToken;
import com.cosmos.login.dto.ChangePasswordDto;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.login.service.impl.AppUserServiceImpl;
import com.cosmos.user.dto.JwtRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api")
@CrossOrigin
public class LoginController {
	@Autowired
	private AppUserServiceImpl appUserService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private AppUserRepo appUserRepo;
	@Autowired
	private EmailHtmlSender emailHtmlSender;
	@Autowired
	private UniqueCodeGenerator generator;

	@PostMapping(value = "/signin", consumes = "application/json", produces = "application/json")
	public AppUserToken createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		return appUserService.processWebLogin(authenticationRequest);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiMessage> resetPassword(@RequestParam("email") String userEmail) {
		AppUser user = appUserService.findUserByEmail(userEmail);
		if (user == null) {
			throw new CustomException("Email Not Found!", HttpStatus.NOT_FOUND);
		}

		String code = generator.generateOtp(2);

		emailHtmlSender.sendPasswordResetCode(userEmail, code);
		appUserService.createPasswordResetTokenForUser(user, code);

		return new ResponseEntity<>(new ApiMessage("Password Reset Email Sent!", HttpStatus.OK),HttpStatus.OK);
	}

	@PostMapping("/validate-otp")
	public ResponseEntity<ApiMessage> validateOTP(@RequestBody ChangePasswordDto changePasswordDto) {
		appUserService.validateToken(changePasswordDto.getToken());
		return new ResponseEntity<>(new ApiMessage("Validated Successfully!", HttpStatus.OK), HttpStatus.OK);
	}

	@GetMapping("/verify-email")
	public ResponseEntity<ApiMessage> verifyEmail(@RequestParam("token") String token) {
		appUserService.validateToken(token);

		Optional<AppUser> user = appUserService.getUserByToken(token);
		if(!user.isPresent())
			throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);

		AppUser appUser = user.get();
		appUser.setEnabled(true);
		appUserRepo.save(appUser);

		return new ResponseEntity<>(new ApiMessage("Email Verified Successfully!", HttpStatus.OK),HttpStatus.OK);
	}

	@PostMapping("/save-password")
	public ResponseEntity<ApiMessage> savePassword(@RequestBody ChangePasswordDto passwordDto) {

		String result = appUserService.validateToken(passwordDto.getToken());

		Optional<AppUser> user = appUserService.getUserByToken(passwordDto.getToken());
		if(!user.isPresent())
			throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);

		AppUser appUser = user.get();
		appUser.setHashPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
		appUserRepo.save(appUser);

		return new ResponseEntity<>(new ApiMessage("Password reset successful!", HttpStatus.OK), HttpStatus.OK);
	}


	@PostMapping(value = "/change-password")
	public ResponseEntity<ApiMessage> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
		CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		AppUser appUser = appUserService.findUserByEmail(currentlyLoggedInUser.getUsername());
		if (appUser == null) {
			throw new CustomException("Email Not Found!", HttpStatus.NOT_FOUND);
		}

		boolean result = passwordEncoder.matches(changePasswordDto.getOldPassword(), appUser.getHashPassword());

		if (!result) {
			throw new CustomException("Old Password doesn't match!", HttpStatus.NOT_ACCEPTABLE);
		}

		appUser.setHashPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
		appUserRepo.save(appUser);

		return new ResponseEntity<>(new ApiMessage("Successfully Changed Password!", HttpStatus.OK),HttpStatus.OK);
	}



}
