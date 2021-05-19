package com.ncgroup.marketplaceserver.service.impl;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ncgroup.marketplaceserver.exception.constants.ExceptionMessage;
import com.ncgroup.marketplaceserver.exception.domain.EmailExistException;
import com.ncgroup.marketplaceserver.exception.domain.EmailNotFoundException;
import com.ncgroup.marketplaceserver.exception.domain.LinkExpiredException;
import com.ncgroup.marketplaceserver.exception.domain.LinkNotValidException;
import com.ncgroup.marketplaceserver.exception.domain.PasswordNotValidException;
import com.ncgroup.marketplaceserver.exception.domain.UserNotFoundException;
import com.ncgroup.marketplaceserver.model.Role;
import com.ncgroup.marketplaceserver.model.User;
import com.ncgroup.marketplaceserver.model.dto.UserDto;
import com.ncgroup.marketplaceserver.repository.*;
import com.ncgroup.marketplaceserver.security.model.UserPrincipal;
import com.ncgroup.marketplaceserver.security.service.LoginAttemptService;
import com.ncgroup.marketplaceserver.service.EmailSenderService;
import com.ncgroup.marketplaceserver.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailSenderService emailSenderService;
	
	private final int LINK_VALID_TIME_HOUR = 24;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, 
						   BCryptPasswordEncoder passwordEncoder, 
			               LoginAttemptService loginAttemptService,
			               EmailSenderService emailSenderService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailSenderService = emailSenderService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if(user == null) {
			throw new UsernameNotFoundException(MessageFormat.format(ExceptionMessage.USERNAME_NOT_FOUND, username));
		}

		validateLoginAttempt(user);
		UserPrincipal userPrincipal = new UserPrincipal(user);
		return userPrincipal;

	}

	

	@Override
	public UserDto register(String name, String surname, String email, String password, String phone) {
		validateNewEmail(StringUtils.EMPTY, email);
		//validate password
		if(!validatePasswordPattern(password)) {
			throw new PasswordNotValidException(ExceptionMessage.PASSWORD_NOT_VALID);
		}
		
		User user = User.builder()
				.name(name)
				.surname(surname)
				.phone(phone)
				.email(email)
				.password(encodePassword(password))
				.lastFailedAuth(LocalDateTime.now())
				.role(Role.ROLE_USER)
				.build();
        
		String authlink = emailSenderService.sendSimpleEmailValidate(email);
		user.setAuthLink(authlink);		
		user = userRepository.save(user);
        
        log.info("New user registered");
        return UserDto.convertToDto(user);
	}
	
	//Set user.enabled true after user has clicked the correct link sent by email
	@Override
	public UserDto enableUser(String link) {
		User user = validateAuthLink(link);
		userRepository.enableUser(link);
		return user != null ? UserDto.convertToDto(user) : null;
	}
	
	@Override
	public void setNewPassword(long id, String newPassword) {
		//User user = validateAuthLink(link);
		User user = findUserById(id);
		validatePasswordPattern(newPassword);
		if(user.getPassword().equals(newPassword)) {
			throw new PasswordNotValidException(ExceptionMessage.SAME_PASSWORD);
		}
		userRepository.updatePassword(user.getEmail(), encodePassword(newPassword));;
	}
	
	@Override
	public List<UserDto> getUsers() {
		return userRepository.findAll().stream().map(UserDto::convertToDto).collect(Collectors.toList());
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	

	@Override
	public User findUserById(long id) {
		return userRepository.findById(id);
	}
	
	@Override
	public User addUser(String name, String surname, String email, Role role, String phone) {
		validateNewEmail(StringUtils.EMPTY, email);
		User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setRole(role);
        user.setPhone(phone);
        userRepository.save(user);
		return user;
	}

	@Override
	public User updateUser(long id, String newName, String newSurname, String newEmail, String newPhone,
			boolean isEnabled) {
		User user = validateNewEmail(userRepository.findById(id).getEmail(), newEmail);
        user.setName(newName);
        user.setSurname(newSurname);
        user.setEmail(newEmail);
        user.setPhone(newPhone);
        userRepository.save(user);
		return user;
	}

	@Override
	public void deleteUser(long id) {
		userRepository.deleteById(id);
		
	}

	@Override
	public void resetPassword(String email) throws EmailNotFoundException {
		User user = userRepository.findByEmail(email);
		if(user == null) {
			log.info(email);			
			throw new EmailNotFoundException(MessageFormat.format(ExceptionMessage.USERNAME_NOT_FOUND, email));
		}
		
		String auth_link = emailSenderService.sendSimpleEmailPasswordRecovery(email);
		user.setAuthLink(auth_link);
		userRepository.updateAuthLink(email, auth_link);
	}
	
	/*
	 * This method checks the validity of email so that email is not already taken by another user in case of adding new user
	 * Or in case of updating info about existing user, method returns user associated with given email
	 * If currentEmail is Empty then this method is called from register() or addUser() method
	 * */
	private User validateNewEmail(String currentEmail, String newEmail) {
        //Check that email matches RegExpr
		/*if(!validateEmailPattern(newEmail)) {
			throw new PasswordNotValidException(ExceptionMessage.EMAIL_NOT_VALID);
		}*/
		
		User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentEmail)) { //The user wants to update existing email
        	
            User currentUser = findUserByEmail(currentEmail);
            if(currentUser == null) { //No user with such an email
            	throw new UserNotFoundException(MessageFormat.format("User with email {0} does not exist", currentEmail));
            }
            if(userByNewEmail != null && currentUser.getId() != (userByNewEmail.getId())) {
                throw new EmailExistException(MessageFormat.format(ExceptionMessage.EMAIL_ALREADY_EXISTS, currentEmail));
            }
            return currentUser;
        } else {
        	
            if(userByNewEmail != null) {
            	if(!userByNewEmail.isEnabled()) {
            		//user with such email exists but he has not activated account
            		//delete this user as we want to add/register him once more
            		userRepository.deleteById(userByNewEmail.getId());
            	} else {
            		// User with such an email already exists
                	throw new EmailExistException(MessageFormat.format(ExceptionMessage.EMAIL_ALREADY_EXISTS, currentEmail));
            	}
            }
            
            return null;
        }
    }
	
	
	private void validateLoginAttempt(User user) {
		if(loginAttemptService.hasExceededMaxAttempts(user.getId())) {

			//TODO captcha
		} else {
			loginAttemptService.successfullLogin(user.getEmail());
		}
	}
	
	//Checks wheather link exists and non-expired
	private User validateAuthLink(String link) {
		User user = userRepository.findByAuthLink(link);
		if(user == null) {
			return null;
		}
		if(user.getAuthLinkDate().isBefore(LocalDateTime.now().minusHours(LINK_VALID_TIME_HOUR))) {
			return null;
		}
		return user;
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	
	private boolean validateEmailPattern(String email) {
		return true;
	}
	
	private boolean validatePasswordPattern(String password) {
		int count = 0;

		   if( 6 <= password.length() && password.length() <= 32  )
		   {
		      if(password.matches(".*[a-z].*")) {
		         count ++;
		      }
		      if( password.matches(".*[A-Z].*") ) {
		         count ++;
		      }
		      if( password.matches(".*[0-9].*") ) {
		         count ++;
		      }
		   }

		   return count >= 3;
	}
	
	
}
