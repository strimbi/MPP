package project.persistance.jdbc;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.model.Race;
import project.persistance.IRaceRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RaceRepository implements IRaceRepository {

    private final JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public RaceRepository(Properties properties) {
        logger.info("Initialising RaceRepository with properties {}", properties);
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public void add(Race elem) {
        logger.traceEntry("saving task {} ",elem);
        Connection con=jdbcUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into races values (?,?,?)")){
            preStmt.setInt(1,elem.getID());
            preStmt.setString(2,elem.getMotor());
            preStmt.setInt(3,elem.getNrParticipants());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Race elem) {}

    @Override
    public void update(Race elem, Integer id) {}

    @Override
    public Race findById(Integer id) {
        logger.traceEntry();
        String sql = "SELECT * FROM races WHERE id = ?";

        Race race = null;

        try (
                Connection connection = jdbcUtils.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String motor = resultSet.getString("motor");
                int nrparticipants = resultSet.getInt("nrparticipants");

                race = new Race(id, motor,nrparticipants);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }

        logger.traceExit();
        return race;
    }

    @Override
    public Iterable<Race> findAll() {
        logger.traceEntry();
        String sql = "SELECT * FROM races";

        List<Race> raceList = new ArrayList<>();

        try (
                Connection connection = jdbcUtils.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String motor = resultSet.getString("motor");
                int nrparticipants = resultSet.getInt("nrparticipants");

                Race race = new Race(id, motor, nrparticipants);

                raceList.add(race);
            }

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }

        logger.traceExit();
        return raceList;
    }
}