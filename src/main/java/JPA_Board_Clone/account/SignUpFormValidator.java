package JPA_Board_Clone.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component //적용함으로서 빈으로 등록하게끔 사용
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator { //객체 검증을 위한 스프링 validator 상속 https://engkimbs.tistory.com/728

    private final AccountRepository accountRepository; // 자바, 스프링 의존성 주입 차이 https://medium.com/@kimddub/java-%EC%9D%98%EC%A1%B4%EC%84%B1%EC%97%90-%EB%8C%80%ED%95%B4-dip-%EC%9D%98%EC%A1%B4%EC%97%AD%EC%A0%84-%EC%9B%90%EC%B9%99-di-%EC%9D%98%EC%A1%B4%EC%84%B1-%EC%A3%BC%EC%9E%85-b4a669f62bc0





    //2개의 메소드를 validator로 부터 구현해야된다
    /*boolean supports(Class clazz) : 어떤 타입의 객체를 검증할 때 이 객체의 클래스가 이 Validator가 검증할 수 있는 클래스인 지를 판단하는 매서드
    void validate(Object target, Errors error) : 실제 검증 로직이 이루어지는 메서드, 구현할 때 ValidationUtils를 사용하여 편리하게 작성 가능
    */

    @Override
    public boolean supports(Class<?> aClass){ // 자바 제네릭 타입 ?는 와일드 카드로 어떠한 형태의 타입도 받을 수 있다.
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override // 이메일과 닉네임 중복 검사
    public void validate(Object object, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) object; // ( ) 명시적 변환(큰곳에서 작은 곳으려 형변환) https://data-make.tistory.com/214, 묵시적 형 변환
        if(accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다");

        }

        if(accountRepository.existByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임 입니다");
        }
    }



}
