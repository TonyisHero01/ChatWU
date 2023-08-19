package cz.cuni.mff.java.chatwu.dto;

import java.io.Serializable;

public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * username
     */

    private String name;

    /**
     * password
     */

    private String password;

    /**
     * constructor
     */
    public UserDto() {

    }
    /**
     * constructor
     */
    public UserDto(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
