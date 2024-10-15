package rt.marson.syeta.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.entity.Role;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.VerificationToken;
import rt.marson.syeta.dto.auth.JwtAuthenticationResponseDto;
import rt.marson.syeta.dto.auth.SignInRequestDto;
import rt.marson.syeta.dto.auth.SignUpRequestDto;
import rt.marson.syeta.service.MailService;
import rt.marson.syeta.service.user.CountryService;
import rt.marson.syeta.service.user.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final MailService mailService;
    private final CountryService countryService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Transactional
    public JwtAuthenticationResponseDto signUp(SignUpRequestDto request) {
        if (userService.isEmailAlreadyExist(request.getEmail())) {
            throw new BadCredentialsException("Email " + request.getEmail() + " уже зарегестрирован");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(encodePassword(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .country(
                        request.getCountryName() == null ?
                        null :
                        countryService.findCountryByName(request.getCountryName())
                )
                .role(Role.USER)
                .birthDate(request.getBirthDate())
                .isActive(true)
                .build();

        userService.create(user);

        VerificationToken token = mailService.createVerificationToken(user);
        mailService.sendConfirmationEmail(user, token.getToken());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponseDto(jwt, user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponseDto signIn(SignInRequestDto request) {

        /*authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));*/
        var user = userService
                .getUserByEmailForLogin(request.getEmail());
        if (!isPasswordsEqual(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверные логин или пароль");
        }

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponseDto(jwt, user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean isPasswordsEqual(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userService.getUserByEmailOrNullIfNotExists(email);
        if (user != null) {
            VerificationToken token = mailService.createVerificationToken(user);
            mailService.sendResetPasswordOffer(user, token.getToken());
        }
    }
}
