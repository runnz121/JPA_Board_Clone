package JPA_Board_Clone.zone;


import JPA_Board_Clone.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct // @PostConstruct는 의존성 주입이 이루어진 후 초기화를 수행하는 메서드이다. @PostConstruct가 붙은 메서드는 클래스가 service(로직을 탈 때? 로 생각 됨)를 수행하기 전에 발생한다. https://zorba91.tistory.com/223
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {  //갯수확인
            Resource resource = new ClassPathResource("zones_kr.csv"); //자바 (resource 폴더안) classpath에 존재하는 파일 정보를 읽어옴 https://roqkffhwk.tistory.com/124
            //Files.readAllLines로 파일을 읽어와 list에 저장
            List<Zone> zones = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                    .stream() //stream() >> 람다함수 쓰기 위함 https://jeong-pro.tistory.com/165
                    .map(line -> {// 쉽게 생각하시면 stream에서 map function으로 element들을 하나씩 줄건데 map function 내부에서 그 element를 뭘로 칭할지 적는거라고 보시면 됩니다.
                        String[] split = line.split(",");
                        return Zone.builder()   //zone 에 builder()로 값은 넣어준다
                                .city(split[0])
                                .localNameOfCity(split[1])
                                .province(split[2])
                                .build();       //builder를 위에서 썻으면 이것으로 build()로 리턴시켜준다
                    })
                    .collect(Collectors.toList());
            zoneRepository.saveAll(zones);

        }
    }
}
