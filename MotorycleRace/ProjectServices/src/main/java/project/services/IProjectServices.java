package project.services;

import project.model.Organizer;
import project.model.Participant;
import project.model.Race;

public interface IProjectServices {
    void login(Organizer organizer, IProjectObserver client) throws ProjectException;
    void logout(Organizer organizer, IProjectObserver client) throws ProjectException;
    Race[] getAllRaces(Organizer organizer, IProjectObserver client) throws ProjectException;
    Participant[] getAllParticipants(Organizer organizer, IProjectObserver client) throws  ProjectException;
    Participant[] getParticipantsForTeam(Participant participant, IProjectObserver client) throws ProjectException;
    void addParticipant(Participant participant, IProjectObserver client) throws ProjectException;
    int getLowestId(IProjectObserver client) ;
}