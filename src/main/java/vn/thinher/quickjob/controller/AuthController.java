package vn.thinher.quickjob.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.thinher.quickjob.domain.User;
import vn.thinher.quickjob.domain.dto.LoginDTO;
import vn.thinher.quickjob.domain.dto.ResLoginDTO;
import vn.thinher.quickjob.service.UserService;
import vn.thinher.quickjob.util.SecurityUtil;
import vn.thinher.quickjob.util.annotation.ApiMessage;
import vn.thinher.quickjob.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${thinher.jwt.refresh-token-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // set thông tin đăng nhập vào context (có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User user = this.userService.handleFetchUserByEmail(loginDTO.getUsername());
        if (user != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setId(user.getId());
            userLogin.setName(user.getName());
            userLogin.setEmail(user.getEmail());
            resLoginDTO.setUser(userLogin);
        }

        // create token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUser());
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);
        this.userService.updateUserToken(loginDTO.getUsername(), refreshToken);

        // set cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        User user = this.userService.handleFetchUserByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (user != null) {
            userLogin.setId(user.getId());
            userLogin.setName(user.getName());
            userLogin.setEmail(user.getEmail());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refreshToken") String refreshToken) throws IdInvalidException {
        // check valid refresh token
        Jwt decodedRefreshToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedRefreshToken.getSubject();
        // check user by email + token
        User user = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (user == null) {
            throw new IdInvalidException("Refresh token not found");
        }
        // issue new access token/set refresh token as cookie
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User newUser = this.userService.handleFetchUserByEmail(email);
        if (newUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            userLogin.setId(newUser.getId());
            userLogin.setName(newUser.getName());
            userLogin.setEmail(newUser.getEmail());
            resLoginDTO.setUser(userLogin);
        }

        // create token
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO.getUser());
        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);
        this.userService.updateUserToken(email, newRefreshToken);

        // set cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email.equals("")) {
            throw new IdInvalidException("Access token not found");
        }
        this.userService.updateUserToken(email, null);
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }

}
