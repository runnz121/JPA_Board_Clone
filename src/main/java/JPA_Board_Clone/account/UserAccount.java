package JPA_Board_Clone.account;

import JPA_Board_Clone.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {

    private Account account;
                                          //https://ithub.tistory.com/66
    public UserAccount(Account account) { //super(): 자식 클래스가 자신을 생성할 때 부모 클래스의 생성자를 불러 초기화 할 때 사용(기본적으로 자식 클래스의 생성자에 추가)
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER"))); //부모클래스 Account로 부터 nickname과 password를 가져오고 이를 스프링 서큐리티 ROLE_USER권한으로 초기화
        this.account = account;
    }
}