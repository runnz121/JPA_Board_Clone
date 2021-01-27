package JPA_Board_Clone.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id") // equals : 두 객체 내용이 같은지(동등성), hashcode : 두 객체가 같은 객체인지(동일성) https://n1tjrgns.tistory.com/164
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id             //테이블 primary key 역할
    @GeneratedValue //pk값 자동 생성(키생성 전략 선택 가능, https://ithub.tistory.com/24
    private Long id;

    @Column(unique = true, nullable = false)// 객체필드와 db테이블 컬럼을 맵핑 https://velog.io/@leyuri/Spring-boot-JPA-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98-Entity-Table-Column-Id-Lombok
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob    //Large Object의 줄임말 사진과 같이 큰 데이터를 저장하는 컬럼을 사용할때 지정해준다  https://pinokio0702.tistory.com/142
    @Basic(fetch = FetchType.EAGER) //eager 즉시로딩(위의 링크 참조)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb = true;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        emailVerified = true;
        joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1L));
    }


}
