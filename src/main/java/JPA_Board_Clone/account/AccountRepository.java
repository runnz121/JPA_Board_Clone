package JPA_Board_Clone.account;


import JPA_Board_Clone.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;//optional 기능을 사용하려면 이걸 import


@Transactional(readOnly = true) //해당 매소드 트랜잭션시 읽기 기능만 할것인지를 지정함(트랜젝션이 필요한 곳에 선언함)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existByNickname(String nickName);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNickname);


}
