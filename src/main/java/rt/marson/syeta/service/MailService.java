package rt.marson.syeta.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rt.marson.syeta.entity.User;
import rt.marson.syeta.entity.VerificationToken;
import rt.marson.syeta.repository.VerificationTokenRepo;
import rt.marson.syeta.service.user.UserService;
import rt.marson.syeta.util.PasswordGeneratorUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {
    private final VerificationTokenRepo verificationTokenRepo;
    private final JavaMailSender mailSender;
    private final UserService userService;

    private static final Long DAYS_TO_RESET_PASSWORD = 1L;

    public boolean confirmEmail(String token) {
        VerificationToken verificationToken = findVerificationToken(token);
        if (verificationToken.getCreatedAt().isBefore(LocalDateTime.now())) {
            User user = verificationToken.getUser();
            userService.setEmailVerified(user);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean resetPassword(String token) {
        VerificationToken verificationToken = findVerificationToken(token);
        if (verificationToken.getCreatedAt().isBefore(LocalDateTime.now().plusDays(DAYS_TO_RESET_PASSWORD))) {
            User user = verificationToken.getUser();
            String password = PasswordGeneratorUtil.generatePassword();
            userService.changePassword(user.getId(), password);
            sendTempPassword(user, password);
            return true;
        } else {
            return false;
        }
    }

    private VerificationToken findVerificationToken(String token) {
        return verificationTokenRepo.findByToken(token).orElseThrow(() -> new EntityNotFoundException("Token not found"));
    }

    public void sendConfirmationEmail(User user, String token) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Подтверждение регистрации JOIN GO");

            String url = "https://syeta.onrender.com/confirm?token=" + token;
            String htmlContent = """
                    <html>
                        <body>
                            <p>Здравствуйте!</p>
                            <p/>
                            <p>Спасибо за регистрацию в приложении JOIN GO!</p>
                            <p>Чтобы активировать Ваш аккаунт и присоединиться к мероприятиям, пожалуйста, подтвердите Вашу электронную почту.</p>
                            <p/>
                            <p><a href="%s" style="font-size: 20px; font-weight: bold; color: white; background-color: #4CAF50; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Подтвердить почту</a></p>
                            <p/>
                            <p>Если Вы не регистрировались в JOIN GO, просто проигнорируйте это письмо.</p>
                            <p/>
                            <p>С наилучшими пожеланиями,<br>Команда JOIN GO</p>
                        </body>
                    </html>
                    """.formatted(url);

            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(mimeMessage);
    }

    public VerificationToken createVerificationToken(User user) {
        return verificationTokenRepo.save(VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .build());
    }

    public void sendEmailConfirmation(Long authUserId) {
        User user = userService.findUserById(authUserId);
        if (!user.isEmailVerified()) {
            sendConfirmationEmail(
                    user,
                    createVerificationToken(user).getToken()
            );
        }
    }

    public void sendResetPasswordOffer(User user, String token) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Сброс пароля для вашего аккаунта в приложении JOIN GO");

            String url = "https://syeta.onrender.com/confirm-password-reset?token=" + token;
            String htmlContent = """
                    <html>
                        <body>
                            <p>Здравствуйте!</p>
                            <p/>
                            <p>Мы получили запрос на сброс пароля для Вашего аккаунта в приложении JOIN GO.</p>
                            <p>Если Вы не запрашивали сброс пароля, просто проигнорируйте это письмо.</p>
                            <p/>
                            <p>Чтобы временный пароль пришел на Вашу почту, пожалуйста, нажмите на кнопку ниже:</p>
                            <p/>
                            <p><a href="%s" style="font-size: 20px; font-weight: bold; color: white; background-color: #FF6347; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Сбросить пароль</a></p>
                            <p/>
                            <p>Ссылка действительна 24 часа.</p>
                            <p/>
                            <p>С наилучшими пожеланиями,<br>Команда JOIN GO</p>
                        </body>
                    </html>
                    """.formatted(url);

            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(mimeMessage);
    }

    public void sendTempPassword(User user, String password) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Ваш временный пароль для приложения JOIN GO");

            String htmlContent = """
                    <html>
                        <body>
                            <p>Здравствуйте!</p>
                            <p/>
                            <p>Вам был выдан временный пароль для доступа к Вашему аккаунту в приложении JOIN GO.</p>
                            <p>Используйте его для входа в приложение. Ваш пароль:</p>
                            <p/>
                            <p><div style="border: 2px dashed #4CAF50; padding: 10px; font-size: 18px; font-weight: bold; color: #333; background-color: #f9f9f9; text-align: center;">%s</div></p>
                            <p/>
                            <p>Команда JOIN GO строго рекомендует изменить пароль после входа в приложение. Вы можете это сделать в разделе Вашего профиля.</p>
                            <p>Если у Вас возникли вопросы или проблемы, пожалуйста, свяжитесь с нашей службой поддержки.</p>
                            <p/>
                            <p>С наилучшими пожеланиями,<br>Команда JOIN GO</p>
                        </body>
                    </html>
                    """.formatted(password);

            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(mimeMessage);
    }
}
