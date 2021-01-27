package JPA_Board_Clone.settings.validator;


import JPA_Board_Clone.account.AccountRepository;
import JPA_Board_Clone.domain.Account;
import JPA_Board_Clone.settings.form.NicknameForm;
import com.fasterxml.jackson.databind.jsontype.impl.MinimalClassNameIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator { //https://engkimbs.tistory.com/728

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) { // 어떤 타입의 객체를 검증할 때 이 객체의 클래스가 이 Validator가 검증할 수 있는 클래스인 지를 판단하는 매서드
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) { //실제 검증 로직이 이루어지는 메서드
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if(byNickname != null) { //: 필드에 대한 에러코드를 추가 에러코드에 대한 메세지가 존재하지 않을 경우 defaultMessage를 사용 https://bbiyakbbiyak.tistory.com/3
            errors.rejectValue("nickname", "wrong.value","입력하신 닉네임은 사용할 수 없습니다");
        }
    }
}
