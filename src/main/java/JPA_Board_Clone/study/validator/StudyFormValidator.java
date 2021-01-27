package JPA_Board_Clone.study.validator;

import JPA_Board_Clone.study.StudyRepository;
import JPA_Board_Clone.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor //https://lionhead93.github.io/spring/DI-lombok/
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);//class.isAssiangableFrom() :특정 Class가 어떤 클래스/인터페이스를 상속/구현했는지 체크 https://jistol.github.io/java/2017/08/22/different-instanceof-isassignablefrom/
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;
        if(studyRepository.existsByPath(studyForm.getPath())){
            errors.rejectValue("path", "wrong path", "스터디 경로값을 사용할 수 없습니다.");
           }
        }
    }

