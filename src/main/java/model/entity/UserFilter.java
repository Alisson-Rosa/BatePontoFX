package model.entity;

import model.enumeration.Role;

public class UserFilter {

    private Integer registrationCode;
    private String name;
    private String login;
    private String role;

    public UserFilter() {
    }

    public UserFilter(Integer registrationCode, String name, String login, String role) {
        this.registrationCode = registrationCode;
        this.name = name;
        this.login = login;
        this.role = role;
    }

    public Integer getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(Integer registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
