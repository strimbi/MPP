package project.networking.dto;

import project.model.Race;

public class RacesDto {
    private Integer id;
    private String motor;
    private String participants;

    public RacesDto(Race race) {
        this.id = race.getID();
        this.motor = race.getMotor();
        this.participants = String.valueOf(race.getNrParticipants());
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String nrParticipants) {
        this.participants = nrParticipants;
    }
}