package project.persistance.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import project.model.Organizer;
import project.persistance.IOrganizerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OrganizerRepository implements IOrganizerRepository {

    private final JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public OrganizerRepository(Properties properties) {
        logger.info("Initialising OrganizerRepository with properties {}", properties);
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public void add(Organizer elem) {
        logger.traceEntry("saving task {} ",elem);
        Connection con=jdbcUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into organizers values (?,?,?)")){
            preStmt.setString(1,elem.getID());
            preStmt.setString(2,elem.getName());
            preStmt.setString(3,elem.getPassword());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Organizer elem) {}

    @Override
    public void update(Organizer elem, String id) {}

    @Override
    public Organizer findById(String id) {
        logger.traceEntry();
        String sql = "SELECT * FROM organizers WHERE id = ?";

        Organizer organizer = null;

        try (
                Connection connection = jdbcUtils.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");

                organizer = new Organizer(id,name,password);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }

        logger.traceExit();
        return organizer;
    }

    @Override
    public Iterable<Organizer> findAll() {
        logger.traceEntry();
        String sql = "SELECT * FROM organizers";

        List<Organizer> organizerList = new ArrayList<>();

        try (
                Connection connection = jdbcUtils.getConnection()
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String password = resultSet.getString("passsword");

                Organizer organizer = new Organizer(id, name, password);

                organizerList.add(organizer);
            }

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }

        logger.traceExit();
        return organizerList;
    }

    @Override
    public boolean existsOrganizer(String username, String password) {
        logger.traceEntry();
        String sql = "SELECT * FROM organizers WHERE username = ? AND password = ?";

        try (
                Connection connection = jdbcUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+ e);
        }
        logger.traceExit();
        return false;
    }

}