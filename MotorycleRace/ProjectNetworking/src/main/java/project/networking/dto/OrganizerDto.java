package project.networking.dto;

import project.model.Organizer;

public class OrganizerDto {
    private String username;
    private String name;
    private String password;

    public OrganizerDto(String username,String name,String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return username;
    }

    public void setId(String id) {
        this.username = id;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password){
        this.password=password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}