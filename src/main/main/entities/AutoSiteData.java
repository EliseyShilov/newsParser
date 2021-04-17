package main.entities;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class AutoSiteData {

    private String title;

    private String link;

    private LocalDate publicationDate;

    private main.entities.AuthorData author;

    private String text;

    private Source source;

    private List<Tag> tags;

    public enum Tag {
        VW,
        SKODA,
        RENAULT,
        MITSUBISHI,
        LADA,
        UAZ,
    }

    public enum Source {

        ZR("За рулем"),
        AR("Авторевю");

        private final String desc;

        Source(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    public void setTitle(String title) {
        this.title = title;
        setTags(title);
    }

    public String getTitle() {
        return title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setAuthor(AuthorData author) {
        this.author = author;
    }

    public AuthorData getAuthor() {
        return author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public void setTag(Tag tag) {
        if (tags == null)
            this.tags = new LinkedList<>();
        tags.add(tag);
    }

    public List<Tag> getTags() {
        if (tags == null)
            this.tags = new LinkedList<>();
        return tags;
    }

    private void setTags(String title) {
        String[] titleWords = title.trim().toUpperCase().split("[ ,.:;!?\"-]+");
        for (String word : titleWords) {
            switch (word) {
                case "VW":
                case "VOLKSWAGEN":
                case "ФОЛЬКСВАГЕН":
                case "ФОЛЬКСВАГЕНА":
                case "ФОЛЬКСВАГЕНУ":
                case "ФОЛЬКСВАГЕНОМ":
                case "ФОЛЬКСВАГЕНЕ":
                case "ФОЛЬКСВАГЕНЫ":
                case "ФОЛЬКСВАГЕНОВ":
                case "ФОЛЬКСВАГЕНАМ":
                case "ФОЛЬКСВАГЕНАМИ":
                case "ФОЛЬКСВАГЕНАХ":
                case "GOLF":
                case "TIGUAN":
                case "TOUAREG":
                case "PASSAT":
                case "POLO":
                case "SCIROCCO":
                case "JETTA":
                case "CRAFTER":
                case "MULTIVAN":
                case "TRANSPORTER":
                case "ID":
                case "ARTEON":
                    setTag(Tag.VW);
                    break;
                case "SKODA":
                case "ШКОДА":
                case "ШКОДЫ":
                case "ШКОДЕ":
                case "ШКОДУ":
                case "ШКОДОЙ":
                case "ШКОД":
                case "ШКОДАМ":
                case "ШКОДАМИ":
                case "ШКОДАХ":
                case "OCTAVIA":
                case "RAPID":
                case "KAROQ":
                case "KODIAQ":
                case "SUPERB":
                case "KAMIQ":
                case "SCALA":
                case "FABIA":
                case "YETI":
                case "FELICIA":
                case "ROOMSTER":
                    setTag(Tag.SKODA);
                    break;
                case "RENAULT":
                case "РЕНО":
                case "ARCANA":
                case "CAPTUR":
                case "LOGAN":
                case "SANDERO":
                case "KOLEOS":
                    setTag(Tag.RENAULT);
                    break;
                case "MITSUBISHI":
                case "МИЦУБИСИ":
                case "МИЦУБИШИ":
                case "OUTLANDER":
                case "LANCER":
                case "L200":
                case "PAJERO":
                    setTag(Tag.MITSUBISHI);
                    break;
                case "LADA":
                case "ЛАДА":
                case "ВАЗ":
                case "АВТОВАЗ":
                case "ВЕСТА":
                case "КАЛИНА":
                case "ПРИОРА":
                case "НИВА":
                case "TRAVEL":
                case "ШНИВА":
                    setTag(Tag.LADA);
                    break;
                case "УАЗ":
                case "ХАНТЕР":
                case "ПАТРИОТ":
                case "ПАТРИОТУ":
                case "ПАТРИОТЕ":
                case "ПАТРИОТА":
                case "ПАТРИОТЫ":
                case "PATRIOT":
                case "БУХАНКА":
                case "БУХАНКИ":
                case "БУХАНКЕ":
                case "БУХАНКОЙ":
                    setTag(Tag.UAZ);
            }
        }
    }
}
