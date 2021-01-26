package JPA_Board_Clone.domain;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @Generated
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;
}
