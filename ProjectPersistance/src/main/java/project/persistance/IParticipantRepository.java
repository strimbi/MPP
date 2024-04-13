package project.persistance;


import project.model.Participant;

public interface IParticipantRepository extends IERepository<Participant, Integer>{
    Integer getLowestAvbId();
    Iterable<Participant> findParticipantByTeam(String team);
}