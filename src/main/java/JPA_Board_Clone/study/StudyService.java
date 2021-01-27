package JPA_Board_Clone.study;

import JPA_Board_Clone.domain.Account;
import JPA_Board_Clone.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;

    public Study createNewStudy(Study study, Account account) { //https://stackoverflow.com/questions/47908507/public-classname-functionnamearguments-what-is-the-name-for-this-kind-of
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }
}