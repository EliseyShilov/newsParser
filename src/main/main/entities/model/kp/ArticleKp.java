package main.entities.model.kp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticleKp {

    @JsonProperty("headline")
    private String headline;

    @JsonProperty("datePublished")
    private String date;

    @JsonProperty("description")
    private String description;

    @JsonProperty("articleBody")
    private String articleBody;

    @JsonProperty("author")
    private List<ArticleAuthorKp> author;

}
