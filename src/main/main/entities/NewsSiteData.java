package main.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsSiteData {

    String title;

    String link;

    LocalDate publicationDate;

    String text;

    Source source;

    List<NewsSiteAuthor> author;

    public enum Source {

        KP("Комсомольская правда"),
        VE("Вести");

        private final String desc;

        Source(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    Integer emotionalRating;

}
