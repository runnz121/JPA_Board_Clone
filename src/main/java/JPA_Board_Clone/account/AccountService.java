package JPA_Board_Clone.account;


import JPA_Board_Clone.config.AppProperties;
import JPA_Board_Clone.domain.Account;
import JPA_Board_Clone.domain.Tag;
import JPA_Board_Clone.domain.Zone;
import JPA_Board_Clone.mail.EmailMessage;
import JPA_Board_Clone.mail.EmailService;
import JPA_Board_Clone.settings.form.Notification;
import JPA_Board_Clone.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AccountService implements UserDetailsService {


    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper; //https://blog.naver.com/writer0713/221596629064
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
        String message = templateEngine.process("mail/simple-link", context); // 템플릿 이름과 context를 받는다 http://massapi.com/method/org/thymeleaf/TemplateEngine.process.html

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디올래, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account); //principal 객체 ㄹ턴

    }


    // 정석적으로 인증을 하려면 AuthenticationManager에서 authenticate 를 통해 token을 만들어야 한다.
    // 정석적 방법을 쓰지 않는 이유는 정석적인 방법을 사용한다면 plain password가 token에 저장되기 때문인데 형재는 plain password를 사용하지 않을 것이기 때문에
    // 이 방법을 사용한다.


    //https://kyu9341.github.io/java/2020/04/30/java_springBootLogin/
    public void login(Account account) { //https://flyburi.com/584 , https://velog.io/@dnjscksdn98/Spring-Spring-Security%EB%9E%80
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( //아이디와 패스워드 인증시 UsernamePasswordAuthenticationToken 클래스를 기본으로 해서 인증받고 있다. https://brunch.co.kr/@sbcoba/12
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    @Transactional
    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken()+
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "스터디올래 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder() //@builder 를 불러옴 객체 생성시 생성자 말고 빌더 쓰는 이유 https://hashcode.co.kr/questions/887/%EC%9E%90%EB%B0%94%EC%97%90%EC%84%9C-builder%EB%A5%BC-%EC%93%B0%EB%8A%94-%EC%9D%B4%EC%9C%A0%EB%8A%94-%EB%AD%94%EA%B0%80%EC%9A%94
                .to(account.getEmail())
                .subject("스터디올래, 로그인 링크")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account); //ModelMapper.map(엔티티클래스,도메인클래스리터럴(Domain.class)) https://coding-start.tistory.com/151
//        account.setUrl(profile.getUrl());
//        account.setOccupation(profile.getOccupation());
//        account.setBio(profile.getBio());
//        account.setLocation(profile.getLocation());
//        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
    }

    @Transactional
    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, Notification notification) {
        modelMapper.map(notification, account);
//        account.setStudyCreatedByWeb(notification.isStudyCreatedByWeb());
//        account.setStudyCreatedByEmail(notification.isStudyCreatedByEmail());
//        account.setStudyUpdateByWeb(notification.isStudyUpdateByWeb());
//        account.setStudyUpdateByEmail(notification.isStudyUpdateByEmail());
//        account.setStudyEnrollmentResultByEmail(notification.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notification.isStudyEnrollmentResultByWeb());
        accountRepository.save(account);
    }


    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.orElseThrow().getTags().remove(tag);
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones(); //orElseThrow() : 저장된 값이 존재하면 그 값을 반환하고 그렇지 않으면 인수로 전달된 예외를 발생시킴
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent( //비어있는 옵셔녈 객체를 받는 경우 해당 메소드는 수행하지 않는다
                (findAccount) -> findAccount.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent((findAccount) -> findAccount.getZones().remove(zone));
    }
}


