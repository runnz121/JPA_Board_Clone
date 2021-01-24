package JPA_Board_Clone.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$") //https://coding-factory.tistory.com/529
    private String nickname;

    @Email  //이메일 형식이 아닌 경우 에러 발생시킴
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

}

