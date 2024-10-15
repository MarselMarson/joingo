package rt.marson.syeta.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

//@Entity
//@Table(name = "phone_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "code")
    String code;
    @Column(name = "flag")
    String flag;

    @Column(name = "country_name_rus")
    String countryNameRus;
    @Column(name = "country_name_eng")
    String countryNameEng;
    @Column(name = "country_name_esp")
    String countryNameEsp;
    @Column(name = "country_name_por")
    String countryNamePor;
    @Column(name = "country_name_ind")
    String countryNameInd;
}
