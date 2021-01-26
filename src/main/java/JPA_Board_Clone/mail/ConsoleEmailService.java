package JPA_Board_Clone.mail;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

@Slf4j
@Profile("local")//환경설정을 할 수 있다. local환경에서 해당 클래스가 동작하도록 함(설정 파일로지정 할 수 있음)  https://dailyheumsi.tistory.com/172
@Component
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendEmail(EmailMessage emailMessage)
    {
        log.info("ent email : {}", emailMessage.getMessage());
    }
}
