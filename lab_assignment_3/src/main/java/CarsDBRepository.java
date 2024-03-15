import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE manufacturer = ?";
        try (
                Connection connection = dbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, manufacturerN);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String manu = resultSet.getString("manufacturer");
                String model = resultSet.getString("model");
                int year = resultSet.getInt("year");

                Car car = new Car(manu, model, year);
                car.setId(id);
                cars.add(car);
            }

        } catch (SQLException e) {
                logger.error(e);
            System.err.println("Error Db "+e);
        }
            logger.traceExit(cars);
        return cars;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE year BETWEEN ? AND ?";
        try (
                Connection connection = dbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, min);
            preparedStatement.setInt(2, max);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String manu = resultSet.getString("manufacturer");
                String model = resultSet.getString("model");
                int year = resultSet.getInt("year");

                Car car = new Car(manu, model, year);
                car.setId(id);
                cars.add(car);
            }

        } catch (SQLException e) {
              logger.error(e);
            System.err.println("Error Db "+e);
        }
          logger.traceExit(cars);
        return cars;
    }

    @Override
    public void add(Car elem) {
          logger.traceEntry("saving task {} ", elem);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement= con.prepareStatement("insert into cars(manufacturer,model,year)" +
                " values (?,?,?)")){
            preparedStatement.setString(1,elem.getManufacturer());
            preparedStatement.setString(2,elem.getModel());
            preparedStatement.setInt(3,elem.getYear());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} instances", result);

        }catch (SQLException ex)
        {
            logger.error(ex);
            System.err.println("Error Db "+ex);
        }
    }

    @Override
    public void update(Integer integer, Car elem) {
        String sql = "UPDATE cars SET manufacturer=?, model=?, year=? WHERE id=?";
        try (
                Connection connection = dbUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, elem.getManufacturer());
            preparedStatement.setString(2, elem.getModel());
            preparedStatement.setInt(3, elem.getYear());
            preparedStatement.setInt(4, integer);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error Db "+ex);
        }
    }

    @Override
    public Iterable<Car> findAll() {
         logger.traceEntry();
         Connection con= dbUtils.getConnection();
         List<Car> cars=new ArrayList<>();
         try(PreparedStatement preparedStatement = con.prepareStatement("select * from cars")){
                try(ResultSet result = preparedStatement.executeQuery()){
                    while(result.next())
                    {
                        int id = result.getInt("id");
                        String manufacturer=result.getString("manufacturer");
                        String model=result.getString("model");
                        int year = result.getInt("year");
                        Car car=new Car(manufacturer,model,year);
                        car.setId(id);
                        cars.add(car);
                    }
                }
         }catch (SQLException e){
            logger.error(e);
            System.err.println("Error db"+e);
         }
         logger.traceExit(cars);
         return cars;
    }
}
