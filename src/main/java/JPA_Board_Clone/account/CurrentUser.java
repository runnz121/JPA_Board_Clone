package JPA_Board_Clone.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//https://kyu9341.github.io/java/2020/04/30/java_springBootLogin/
// 런타임 까지 유지
@Retention(RetentionPolicy.RUNTIME)
// 타겟은 파라미터에만 붙이겠다.
@Target(ElementType.PARAMETER)
// 익명 사용자인 경우에는 null로, 익명 사용자가 아닌 경우에는 실제 account 객체로
// Principal 을 다이나믹 하게 꺼내기 위해 @CurrentUser 생성
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") //핸들러 매개변수로 현재 인증된 Principal을 참조할 수 있다.
                                                                                    //로그인을 안한 상태에서 인증을 안한 상태에서 접근하는 경우에는 Principal이 anonymousUser라는 문자열이다. 이 경우에는 AuthenticationPrincipal를 사용하는 파라미터에 null을 넣어주고 실제로 인증된 사용자가 있는 경우에는 account 정보를 꺼내서 넣어주고 싶은 것이다.
public @interface CurrentUser {

    //https://velog.io/@leyuri/%ED%98%84%EC%9E%AC-%EC%9D%B8%EC%A6%9D%EB%90%9C-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EB%B3%B4-%EC%B0%B8%EC%A1%B0
}
