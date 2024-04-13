package project.networking.dto;

import project.model.Organizer;
import project.model.Participant;
import project.model.Race;

public class DtoUtils {
    public static OrganizerDto getDto(Organizer organizer) {
        String username = organizer.getID();
        String name = organizer.getName();
        String password = organizer.getPassword();
        return new OrganizerDto(username, name, password);
    }

    public static Organizer fromDto(OrganizerDto organizerDto) {
        String username = organizerDto.getId();
        String name = organizerDto.getName();
        String password = organizerDto.getPassword();
        return new Organizer(username, name, password);
    }

    public static Race fromDto(RacesDto raceDto) {
        return new Race(raceDto.getId(), raceDto.getMotor(), Integer.parseInt(raceDto.getParticipants()));
    }

    public static RacesDto getDto(Race race) {
        return new RacesDto(race);
    }

    public static ParticipantDto getDto(Participant participant) {
        return new ParticipantDto(participant.getID(), participant.getName(),
                participant.getMotor(), participant.getTeam(), String.valueOf(participant.getRaceID()));
    }

    public static Participant fromDto(ParticipantDto participantDto) {
        return new Participant(participantDto.getId(), Integer.parseInt(participantDto.getRace_id()),
                participantDto.getName(), participantDto.getMotor(), participantDto.getTeam());
    }

    public static Race[] fromDto(RacesDto[] racesDtos) {
        Race[] races = new Race[racesDtos.length];
        for (int i = 0; i < racesDtos.length; ++i) {
            races[i] = fromDto(racesDtos[i]);
        }
        return races;
    }

    public static RacesDto[] getDto(Race[] races) {
        RacesDto[] racesDtos = new RacesDto[races.length];
        for (int i = 0; i < races.length; ++i) {
            racesDtos[i] = getDto(races[i]);
        }
        return racesDtos;
    }

    public static Participant[] fromDto(ParticipantDto[] participantDtos) {
        Participant[] participants = new Participant[participantDtos.length];
        for (int i = 0; i < participantDtos.length; ++i) {
            participants[i] = fromDto(participantDtos[i]);
        }
        return participants;
    }

    public static ParticipantDto[] getDto(Participant[] participants) {
        ParticipantDto[] participantDtos = new ParticipantDto[participants.length];
        for (int i = 0; i < participants.length; ++i) {
            participantDtos[i] = getDto(participants[i]);
        }
        return participantDtos;
    }

}