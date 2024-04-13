package project.persistance.jdbc;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.model.Participant;
import project.persistance.IParticipantRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class ParticipantRepository implements IParticipantRepository {

    private final JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public ParticipantRepository(Properties properties) {
        logger.info("Initialising ParticipantRepository with properties {}", properties);
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public void add(Participant elem) {
        logger.traceEntry("saving task {} ",elem);
        Connection con=jdbcUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into participants values (?,?,?,?,?)")){
            preStmt.setInt(1,elem.getID());
            preStmt.setString(2,elem.getName());
            preStmt.setString(3,elem.getMotor());
            preStmt.setString(4,elem.getTeam());
            preStmt.setInt(5,elem.getRaceID());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Participant elem) {}

    @Override
    public void update(Participant elem, Integer id) {}

    @Override
    public Participant findById(Integer id) {
        logger.traceEntry();
        String sql = "SELECT * FROM participants WHERE id = ?";

        Participant participant = null;

        try (
                Connection connection = jdbcUtils.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String motor = resultSet.getString("motor");
                String team = resultSet.getString("team");
                int race_id = resultSet.getInt("race_id");

                participant = new Participant(id,race_id,name, motor,team);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }

        logger.traceExit();
        return participant;
    }

    @Override
    public Integer getLowestAvbId() {
        logger.traceEntry();
        String sql = "SELECT MAX(id) FROM participants";
        Integer id = null;

        try (
                Connection connection = jdbcUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }
        logger.traceExit();
        return id;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry();
        String sql = "SELECT * FROM participants";

        List<Participant> participantList = new ArrayList<>();

        try (
                Connection connection = jdbcUtils.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String motor = resultSet.getString("motor");
                String team = resultSet.getString("team");
                int race_id = resultSet.getInt("race_id");

                Participant participant = new Participant(id,race_id,name, motor, team);

                participantList.add(participant);
            }

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }

        logger.traceExit();
        return participantList;
    }

    @Override
    public Iterable<Participant> findParticipantByTeam(String team){
        List<Participant> participantList = new ArrayList<>();
        for(Participant participant : this.findAll()) {
            if(Objects.equals(participant.getTeam().trim().toLowerCase(), team)){
                participantList.add(participant);
            }
        }
        return participantList;
    }
}