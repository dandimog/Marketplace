package com.ncgroup.marketplaceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.TextNode;
import com.ncgroup.marketplaceserver.exception.constants.ExceptionMessage;
import com.ncgroup.marketplaceserver.exception.domain.EmailNotValidException;
import com.ncgroup.marketplaceserver.model.User;
import com.ncgroup.marketplaceserver.model.dto.LoginUserDto;
import com.ncgroup.marketplaceserver.model.dto.UserDto;
import com.ncgroup.marketplaceserver.security.constants.JwtConstants;
import com.ncgroup.marketplaceserver.security.model.UserPrincipal;
import com.ncgroup.marketplaceserver.security.util.JwtProvider;
import com.ncgroup.marketplaceserver.service.UserService;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController  {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtProvider jwtProvider;
    
    @Value("${url.confirm-account.redirect}")
    private String redirectConfirmAccountUrl;
    
    @Value("${url.reset-password.redirect}")
    private String redirectResetPasswordUrl;
    

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserService userService, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginUserDto user) {
    	/*try {
    	authenticate(user.getEmail(), user.getPassword());
    	} catch (Throwable e) {
			e.printStackTrace();	
    	}*/
    	authenticate(user.getEmail(), user.getPassword());
        User loginUser = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(UserDto.convertToDto(loginUser), jwtHeader, OK);
     
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto user) {
        UserDto newUser = userService.register(
        		user.getName(), user.getSurname(), user.getEmail(), user.getPassword(), user.getPhone());
        return new ResponseEntity<>(newUser, OK);
    }
    
    @GetMapping("/confirm-account")
    public ResponseEntity<Void> activate(@RequestParam(name = "token") String link) {
        UserDto newUser = userService.enableUser(link);
        if(newUser == null) {
        	return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.LOCATION, redirectConfirmAccountUrl).build();
        } else {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.LOCATION, redirectConfirmAccountUrl).build();
        }
    }

    
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody TextNode email) {
        userService.resetPassword(email.asText());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/confirm-passreset/{link}")
    public ResponseEntity<UserDto> confirmPassReset(@PathVariable String link) {
    	UserDto user = userService.enableUser(link);
    	if(user == null) {
    		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.LOCATION, redirectResetPasswordUrl+"/"+user.getId()).build();
        } else {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.LOCATION, redirectResetPasswordUrl+"/"+user.getId()).build();
        }
    }
    
    
    @PostMapping("/setnewpassword/{id}")
    public ResponseEntity<UserDto> setNewPassword(@RequestBody String password, @PathVariable long id) {
        userService.setNewPassword(id, password);
        User user = userService.findUserById(id);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(UserDto.convertToDto(user), jwtHeader, OK);
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> findAllUsers() {
    	List<UserDto> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }
    
    
    /*@PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userService.addUser(user.getName(),user.getSurname(), user.getEmail(), user.getRole(), user.getPassword());
        return new ResponseEntity<>(newUser, OK);
    }
    
    @GetMapping("/find/{id}")
    public ResponseEntity<User> addUser(@PathVariable long id) {
        User user = userService.findUserById(id);
        return new ResponseEntity<>(user, OK);
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> findAllUsers() {
    	List<UserDto> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }
    
    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updateUser = userService.updateUser(
        		user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.getPhone(), user.isEnabled())
;        return new ResponseEntity<>(updateUser, OK);
    }*/

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtConstants.TOKEN_HEADER, jwtProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
    
    
}