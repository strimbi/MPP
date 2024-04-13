package project.server;

import project.model.Organizer;
import project.model.Participant;
import project.model.Race;
import project.persistance.IOrganizerRepository;
import project.persistance.IParticipantRepository;
import project.persistance.IRaceRepository;
import project.services.IProjectObserver;
import project.services.IProjectServices;
import project.services.ProjectException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectServiceImpl implements IProjectServices {

    private final IOrganizerRepository organizerRepository;
    private final IParticipantRepository participantRepository;
    private final IRaceRepository raceRepository;
    private Map<String, IProjectObserver> loggedOrganizers;

    public ProjectServiceImpl(IOrganizerRepository organizerRepository, IParticipantRepository participantRepository,
                              IRaceRepository raceRepository) {
        this.organizerRepository = organizerRepository;
        this.participantRepository = participantRepository;
        this.raceRepository = raceRepository;
        loggedOrganizers = new ConcurrentHashMap<>();
    }

    private void notifyOrganizers() {
        for (IProjectObserver client : loggedOrganizers.values()) {
            try {
                client.ticketBuyed();
            } catch (ProjectException e) {
                System.err.println("Error notifying agency " + e);
            }
        }
    }

    @Override
    public void login(Organizer organizer, IProjectObserver client) throws ProjectException {
        boolean valid = organizerRepository.existsOrganizer(organizer.getID(), organizer.getPassword());
        if (valid) {
            if (loggedOrganizers.get(organizer.getID()) != null) {
                throw new ProjectException("User already logged in!");
            }
            loggedOrganizers.put(organizer.getID(), client);
        } else {
            throw new ProjectException("Authentication failed!");
        }
    }

    @Override
    public void logout(Organizer organizer, IProjectObserver client) throws ProjectException {
        IProjectObserver localClient = loggedOrganizers.remove(organizer.getID());
        if (localClient == null) {
            throw new ProjectException("Organizer " + organizer.getID() + " was not logged in");
        }
    }

    @Override
    public Race[] getAllRaces(Organizer organizer, IProjectObserver client) throws ProjectException {
        try {
            List<Race> races;
            races = (List<Race>) raceRepository.findAll();
            Race[] raceArray = new Race[races.size()];
            raceArray = races.toArray(raceArray);
            return  raceArray;
        } catch (Exception e) {
            throw new ProjectException("Error " + e);
        }
    }

    @Override
    public Participant[] getAllParticipants(Organizer organizer, IProjectObserver client) throws ProjectException {
        try {
            List<Participant> participants;
            participants = (List<Participant>) participantRepository.findAll();
            Participant[] participantArray = new Participant[participants.size()];
            participantArray = participants.toArray(participantArray);
            return participantArray;
        } catch (Exception e) {
            throw new ProjectException("Error " + e);
        }
    }

    @Override
    public Participant[] getParticipantsForTeam(Participant participant, IProjectObserver client) throws ProjectException {
        try {
            List<Participant> participants;
            participants = (List<Participant>) participantRepository.findParticipantByTeam(participant.getTeam());
            Participant[] participantsArray = new Participant[participants.size()];
            participantsArray = participants.toArray(participantsArray);
            return participantsArray;
        } catch (Exception e) {
            throw new ProjectException("Error " + e);
        }
    }

    @Override
    public void addParticipant(Participant participant, IProjectObserver client) throws ProjectException {
        try {
            participant.setID(participantRepository.getLowestAvbId() + 1);
            participantRepository.add(participant);
            notifyOrganizers();
        } catch (Exception e) {
            throw new ProjectException("Error " + e);
        }
    }

    @Override
    public int getLowestId(IProjectObserver client) {
        return this.participantRepository.getLowestAvbId();
    }
}