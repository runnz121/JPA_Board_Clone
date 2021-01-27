package JPA_Board_Clone.study;


import JPA_Board_Clone.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);
                                            //EntityGraphType.LOAD : attributePaths 정의한 멤버변수는 EAGER 로 불러오고 나머지 멤버변수는 각자의 FetchType을 존중해서 불러온다.
    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD) //https://medium.com/msolo021015/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-jpa-entitygraph-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-282e17c269a7
    Study findByPath(String paht);
}
