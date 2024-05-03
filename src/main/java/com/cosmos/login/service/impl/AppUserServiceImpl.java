package com.cosmos.login.service.impl;

import com.cosmos.common.exception.CustomException;
import com.cosmos.common.security.JwtTokenProvider;
import com.cosmos.login.dto.AppUserToken;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.entity.PasswordResetToken;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.login.repo.PasswordResetTokenRepo;
import com.cosmos.user.dto.JwtRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;

@Service
public class AppUserServiceImpl {

    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
	private AuthenticationManager authenticationManager;
    @Autowired
	private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PasswordResetTokenRepo passwordTokenRepository;


    public AppUser findUserByEmail(String email) {
        return appUserRepo.findByEmail(email);
    }
    
    public AppUser findUserByUserId(Long id) {
        return appUserRepo.findByUserId(id);
    }

    public AppUser addNewAppUser(AppUser newAppUser) {
        if(findUserByEmail(newAppUser.getEmail()) != null) {
            throw new CustomException("User already exists with this email: " + newAppUser.getEmail(),
                    HttpStatus.BAD_REQUEST);
        }

        return appUserRepo.save(newAppUser);
    }
    
    public void deleteAppUser(Long id) {
//        passwordTokenRepository.deletePasswordResetTokensByUser_UserId(id);
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;

            if(!userDetails.getUsername().equals("cosmosastrology.112@gmail.com")) {
             throw new CustomException("NOt authorize ", HttpStatus.UNAUTHORIZED);
            }
    	appUserRepo.delete(appUserRepo.findByUserId(id));
    }
    
    public AppUserToken processWebLogin(JwtRequest jwtRequest) {
		try {
			AppUser appUser = appUserRepo.findByEmail(jwtRequest.getEmail());
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getEmail(), jwtRequest.getPassword()));
			String token = jwtTokenProvider.createToken(jwtRequest.getEmail(),
					appUser.getFirstName() + ' ' + appUser.getLastName(), appUser.getUserId(), appUser.getRole());
			return new AppUserToken("Bearer", token);
		}  catch (DisabledException e) {
            throw new CustomException("USER_DISABLED: EMAIL NOT VERIFIED", HttpStatus.LOCKED);
        } catch (BadCredentialsException e) {
            throw new CustomException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        } catch (LockedException e) {
            throw new CustomException("USER_ACCOUNT_LOCKED", HttpStatus.LOCKED);
        }

	}

    public Optional<AppUser> getUserByToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token) .getUser());
    }

	public void createPasswordResetTokenForUser(AppUser user, String code) {
        PasswordResetToken myToken = new PasswordResetToken(code, user);
        passwordTokenRepository.save(myToken);
    }

    public String validateToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        if (passToken == null)
            throw new CustomException("Invalid Token!", HttpStatus.NOT_FOUND);

        if(isTokenExpired(passToken))
            throw new CustomException("Token Expired!", HttpStatus.NOT_ACCEPTABLE);


        return passToken.getToken();
    }


    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
