package project.persistance;

import project.model.Organizer;

public interface IOrganizerRepository extends IERepository<Organizer, String>{
    boolean existsOrganizer(String username, String password);
}