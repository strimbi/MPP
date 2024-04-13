package project.networking.protocol;

import project.model.Organizer;
import project.model.Participant;
import project.model.Race;
import project.networking.dto.DtoUtils;
import project.networking.dto.OrganizerDto;
import project.networking.dto.ParticipantDto;
import project.networking.dto.RacesDto;
import project.services.IProjectObserver;
import project.services.IProjectServices;
import project.services.ProjectException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProjectServicesProxy implements IProjectServices {
    private String host;
    private int port;
    private IProjectObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> responses;
    private volatile boolean finished;

    public ProjectServicesProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingDeque<>();
    }

    @Override
    public void login(Organizer organizer, IProjectObserver client) throws ProjectException {
        initializeConnection();
        OrganizerDto dto = DtoUtils.getDto(organizer);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(dto).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            this.client = client;
        } else {
            String error = response.data().toString();
            closeConnection();
            throw new ProjectException(error);
        }
    }

    @Override
    public void logout(Organizer organizer, IProjectObserver client) throws ProjectException {
        OrganizerDto organizerDto = DtoUtils.getDto(organizer);
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(organizerDto).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            throw new ProjectException(response.data().toString());
        }
    }

    @Override
    public Race[] getAllRaces(Organizer organizer, IProjectObserver client) throws ProjectException {
        OrganizerDto organizerDto = DtoUtils.getDto(organizer);
        Request request = new Request.Builder().type(RequestType.GET_ALL_RACES).data(organizerDto).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.RACES) {
            RacesDto[] racesDtos = (RacesDto[])response.data();

            return DtoUtils.fromDto(racesDtos);
        }
        throw new ProjectException("There was an error");
    }

    @Override
    public Participant[] getAllParticipants(Organizer organizer, IProjectObserver client) throws ProjectException {
        OrganizerDto organizerDto = DtoUtils.getDto(organizer);
        Request request = new Request.Builder().type(RequestType.GET_ALL_PARTICIPANTS).data(organizerDto).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.PARTICIPANTS) {
            ParticipantDto[] participantDtos = (ParticipantDto[])response.data();

            return DtoUtils.fromDto(participantDtos);
        }
        throw new ProjectException("There was an error");
    }

    @Override
    public Participant[] getParticipantsForTeam(Participant participant, IProjectObserver client) throws ProjectException {
        ParticipantDto participantDto = DtoUtils.getDto(participant);
        Request request = new Request.Builder().type(RequestType.GET_FILTERED_PARTICIPANTS).data(participantDto).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.FILTERED_PARTICIPANTS) {
            ParticipantDto[] participantDtos = (ParticipantDto[])response.data();

            return DtoUtils.fromDto(participantDtos);
        }
        throw new ProjectException("There was an error");
    }

    @Override
    public void addParticipant(Participant participant, IProjectObserver client) throws ProjectException {
        ParticipantDto participantDto = DtoUtils.getDto(participant);
        Request request = new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participantDto).build();
        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new ProjectException(response.data().toString());
        }
    }

    @Override
    public int getLowestId(IProjectObserver client) {
        return 0;
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws ProjectException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ProjectException("Error sending object "+e);
        }
    }

    private Response readResponse() {
        Response response = null;
        try{
            response = responses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    //logger.info("Response received");
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            responses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private void handleUpdate(Response response) {
        try {
            client.ticketBuyed();
        } catch (ProjectException e) {
            e.printStackTrace();
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.RACES;
    }
}