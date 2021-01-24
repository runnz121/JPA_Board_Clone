package JPA_Board_Clone.config;


import JPA_Board_Clone.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception { //기본설정 https://yellowh.tistory.com/138
        http.authorizeRequests()//URL에 대한 요구사항을 정의 할 수 있다.
                .mvcMatchers("/","/login","/sign-up","/check-email","check-enail-toekn", //antMatchers와 차이는 스프링 mvc패턴일땐 mvcMatchers 쓰며, 더 안전 https://stackoverflow.com/questions/50536292/difference-between-antmatcher-and-mvcmatcher
                        "/email-login","/chekc-email-login","/login-link").permitAll() //다음과같은 url에 대해서 허용
                .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() //get방식이며 profile로 시작하고 라는 뜻
                .anyRequest().authenticated(); //모든 요청에 대해 인증을 받게한다 (서큐리티는 쓰인 순서에 따라 우선순위 부여)
        http.formLogin()
                .loginPage("/login").permitAll(); // form 기반 로그인 설정(로그인 페이지 설정)

        http.logout()
                .logoutSuccessUrl("/"); //로그아웃 후 이동될 경로

        http.rememberMe() //https://velog.io/@max9106/Spring-Security-RememberMe%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%9C%A0%EC%A7%80%ED%95%98%EA%B8%B0
                .userDetailsService(accountService) //db에서 유저 정보를 가저오는 역할
                .tokenRepository(tokenRepository());
    }

    @Bean   //tokenrepository 구현체(토큰 관리용 db)
    public PersistentTokenRepository tokenRepository() { //영속하는 토큰 키를 저장
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring() //인증이 필요없는(정적리소스)favicon.ico와 같은 경우 이를 무시 https://lelecoder.com/140
                .mvcMatchers("/node_modules/**") //여기서 nodemudle과 같이 css나 외부 링크 정적 리소스는 무시하여 로딩 빠르게함
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
