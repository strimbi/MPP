package project.model;

import java.io.Serializable;

public  class Participant implements Entity<Integer>, Serializable {

    private int ID;

    private int race_id;
    private String name;
    private String motor;
    private String team;

    public Participant(){
        this.ID = 0;
        this.motor = "";
        this.team = "";
        this.name="";
    }
    public Participant(int ID,int race_id, String name, String motor, String team) {
        this.ID = ID;
        this.race_id = race_id;
        this.name = name;
        this.motor = motor;
        this.team = team;
    }

    public Participant(String team){
        this.team=team;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getID(){
        return ID;
    }

    public void setID(Integer id){
        ID = id;
    }

    public Integer getRaceID(){
        return race_id;
    }

    public void setRace_id(Integer id){
        race_id = id;
    }

    @Override
    public String toString() {
        return "ID=" + ID +
                ", race_id='" + race_id + '\'' +
                ", name='" + name + '\'' +
                ", motor='" + motor + '\'' +
                ", team='" + team + '\'';
    }
}
