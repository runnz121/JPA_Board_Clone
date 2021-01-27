package JPA_Board_Clone.settings.form;

import JPA_Board_Clone.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

    private String zoneName;

    //indexof() : 해당 문자열 찾아 인덱스로(숫자)로 반환
    //substring() : 문자열 start 위치부터 end 전까지 문자열 발췌

   public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() {
        return Zone.builder()
                .city(getCityName())
                .localNameOfCity(getLocalNameOfCity())
                .province(getProvinceName())
                .build();
    }
}
