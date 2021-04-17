package main.entities;

public class AuthorData {

    private String fio;

    private String firstName;

    private String lastName;

    private String middleName;

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getFio() {
        return fio;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMiddleName() {
        return middleName;
    }
}
