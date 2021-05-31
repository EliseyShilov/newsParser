package main.entities;

import java.time.LocalDate;

public class AutoSiteData {

    private String title;

    private String link;

    private LocalDate publicationDate;

    private String author;

    private String text;

    private Source source;

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

    public void setTitle(String title) { this.title = title; }

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

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
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

}
