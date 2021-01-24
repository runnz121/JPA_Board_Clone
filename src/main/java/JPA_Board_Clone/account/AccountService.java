package JPA_Board_Clone.account;


import JPA_Board_Clone.config.AppProperties;
import JPA_Board_Clone.domain.Account;
import JPA_Board_Clone.mail.EmailMessage;
import JPA_Board_Clone.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AccountService implements UserDetailsService {


    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper; //
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;

    @Transactional
    public Account processNewAccount(@Valid SignUpForm signupForm) {
        Account newAccount = saveNewAccount(signupForm);
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context(); // Context를 통해 thymleaf탬플릿에 필요한 변수를 key/value형식으로 전달한다  https://gmby.tistory.com/entry/Thymeleaf%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-html-%ED%85%9C%ED%94%8C%EB%A6%BF-%EC%A1%B0%ED%9A%8C
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올래, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
