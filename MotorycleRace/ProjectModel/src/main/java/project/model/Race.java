package project.model;

import java.io.Serializable;

public  class Race implements Entity<Integer>, Serializable {

    private int ID;
    private String motor;
    private int nrParticipants;

    public Race(){
        this.ID = 0;
        this.motor = "";
        this.nrParticipants = 0;
    }
    public Race(int ID, String motor, int nrParticipants){
        this.ID = ID;
        this.motor = motor;
        this.nrParticipants = nrParticipants;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public int getNrParticipants() {
        return nrParticipants;
    }

    public void setNrParticipants(int nrParticipants) {
        this.nrParticipants = nrParticipants;
    }

    public Integer getID(){
        return ID;
    }
    public void setID(Integer id){
        ID = id;
    }

    @Override
    public String toString() {
        return
                "ID=" + ID + ", motor='" + motor + '\'' + ", number of participants='" + nrParticipants;
    }
}
