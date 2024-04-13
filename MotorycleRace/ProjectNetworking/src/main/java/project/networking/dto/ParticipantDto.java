package project.networking.dto;

import project.model.Participant;

public class ParticipantDto {
    private Integer id;
    private String race_id;
    private String name;
    private String motor;
    private String team;

    public ParticipantDto(int id, String name, String motor, String team, String race_id) {
        this.id = id;
        this.race_id = race_id;
        this.name = name;
        this.motor = motor;
        this.team = team;
    }

    public ParticipantDto(Participant participant) {
        this.id = participant.getID();
        this.race_id = String.valueOf(participant.getRaceID());
        this.name = participant.getName();
        this.motor = participant.getMotor();
        this.team = participant.getTeam();
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeam(){return team;}
    public void setTeam(String team){
        this.team=team;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getRace_id(){return race_id;}

    public void setRace_id(String id){this.race_id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}