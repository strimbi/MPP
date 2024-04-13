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

public class ProjectClientWorker implements Runnable, IProjectObserver {
    private IProjectServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ProjectClientWorker(IProjectServices server, Socket connection) {
        this.server = server;
        this.connection = connection;

        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(connected){
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request)request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private void sendResponse(Response response) throws IOException {
        output.writeObject(response);
        output.flush();
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request) {

        if (request.type() == RequestType.LOGIN) {
            OrganizerDto dto = (OrganizerDto) request.data();
            Organizer organizer = DtoUtils.fromDto(dto);
            try {
                server.login(organizer, this);
                return okResponse;
            } catch (ProjectException e) {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.LOGOUT) {
            OrganizerDto dto = (OrganizerDto) request.data();
            Organizer organizer = DtoUtils.fromDto(dto);
            try {
                server.logout(organizer, this);
                connected = false;
                return okResponse;
            } catch (ProjectException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_ALL_RACES) {
            OrganizerDto dto = (OrganizerDto) request.data();
            Organizer organizer = DtoUtils.fromDto(dto);
            try {
                Race[] ans = server.getAllRaces(organizer, this);
                RacesDto[] dtos = DtoUtils.getDto(ans);
                return new Response.Builder().type(ResponseType.RACES).data(dtos).build();
            } catch (ProjectException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_ALL_PARTICIPANTS) {
            OrganizerDto dto = (OrganizerDto) request.data();
            Organizer organizer = DtoUtils.fromDto(dto);
            try {
                Participant[] ans = server.getAllParticipants(organizer, this);
                ParticipantDto[] dtos = DtoUtils.getDto(ans);
                return new Response.Builder().type(ResponseType.PARTICIPANTS).data(dtos).build();
            } catch (ProjectException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.GET_FILTERED_PARTICIPANTS) {
            ParticipantDto participantDto = (ParticipantDto) request.data();
            Participant participant = DtoUtils.fromDto(participantDto);
            try {
                Participant[] ans = server.getParticipantsForTeam(participant, this);
                ParticipantDto[] dtos = DtoUtils.getDto(ans);
                return new Response.Builder().type(ResponseType.FILTERED_PARTICIPANTS).data(dtos).build();
            } catch (ProjectException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        if (request.type() == RequestType.ADD_PARTICIPANT) {
            ParticipantDto participantDto = (ParticipantDto) request.data();
            Participant participant = DtoUtils.fromDto(participantDto);
            try {
                server.addParticipant(participant, this);
                return okResponse;
            } catch (ProjectException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }

        return null;
    }

    @Override
    public void ticketBuyed() throws ProjectException {
        OrganizerDto agencyDto = new OrganizerDto("agency1", "agency1","1234");
        Response response = new Response.Builder().type(ResponseType.RACES).data(agencyDto).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            throw new ProjectException("Error sending response "+e);
        }
    }
}