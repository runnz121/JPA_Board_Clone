package JPA_Board_Clone.domain;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
@Builder //https://atoz-develop.tistory.com/entry/JAVA-static-class%EC%99%80-Builder-Pattern%EB%B9%8C%EB%8D%94-%ED%8C%A8%ED%84%B4
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;
}
