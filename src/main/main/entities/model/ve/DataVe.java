package main.entities.model.ve;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.entities.model.kp.ChildKp;
import main.entities.model.kp.MetaKp;

import java.util.List;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataVe {

    @JsonProperty("id")
    Integer id;

    @JsonProperty("datePub")
    DatePubVe datePub;

    @JsonProperty("title")
    String title;


}
