import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.networking.utils.AbstractServer;
import project.networking.utils.ProjectConcurrentServer;
import project.networking.utils.ServerException;
import project.persistance.IOrganizerRepository;
import project.persistance.IParticipantRepository;
import project.persistance.IRaceRepository;
import project.persistance.jdbc.OrganizerRepository;
import project.persistance.jdbc.ParticipantRepository;
import project.persistance.jdbc.RaceRepository;
import project.server.ProjectServiceImpl;
import project.services.IProjectServices;


import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static final Logger logger = LogManager.getLogger();
    private static final int defaultPort = 55555;

    public static void main(String[] args) {
        logger.traceEntry();
        Properties serverProperties = new Properties();

        try {
            serverProperties.load(StartServer.class.getResourceAsStream("/project_server.properties"));
        } catch (IOException e) {
            logger.error(e);
            return;
        }

        IOrganizerRepository organizerRepository = new OrganizerRepository(serverProperties);
        IParticipantRepository participantRepository = new ParticipantRepository(serverProperties);
        IRaceRepository raceRepository = new RaceRepository(serverProperties);


        IProjectServices projectServices = new ProjectServiceImpl (organizerRepository, participantRepository,
                raceRepository);

        int projectServerPort = defaultPort;
        try {
            projectServerPort = Integer.parseInt(serverProperties.getProperty("project.server.port"));
        } catch (NumberFormatException nef) {
            logger.error(nef);
            logger.info("Using default port: {}", defaultPort);
        }

        logger.info("Starting server on port: {}", projectServerPort);

        AbstractServer server = new ProjectConcurrentServer(projectServerPort, projectServices);

        try {
            server.start();
        } catch (ServerException e) {
            logger.error(e);
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                logger.error(e);
            }
        }

    }
}