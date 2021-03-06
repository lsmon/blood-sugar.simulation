package com.health.iq.data;


import com.health.iq.model.Exercise;
import com.health.iq.model.GlycemicIndexByFood;
import com.health.iq.model.Input;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lsmon on 11/2/16.
 */
public class DataAccess {
    private static DataAccess instance = null;

    public static DataAccess getInstance(){
        if (instance == null) instance = new DataAccess();
        return instance;
    }

    private DataAccess() {
        try {
            ResultSet resultSet = ConnectionHandler.getConnection().createStatement().executeQuery("SELECT name FROM sqlite_master");
            boolean doesExerciseTableExists = false;
            boolean doesGlycemicIndexTableExists = false;
            boolean doesInputTableExists = false;

            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                if(tableName.equals(Exercise.TBL_EXERCISE)) doesExerciseTableExists = true;
                if(tableName.equals(GlycemicIndexByFood.TBL_FOOD_DB)) doesGlycemicIndexTableExists = true;
                if(tableName.equals(Input.TBL_INPUTS)) doesInputTableExists = true;
            }
            checkAndCreateTables(doesExerciseTableExists);

            checkAndCreateTableGycemicIndex(doesGlycemicIndexTableExists);

            checkAndCreateTableInputs(doesInputTableExists);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHandler.closeConnection();
        }
    }

    private void checkAndCreateTableInputs(boolean doesInputTableExists) throws SQLException {
        if (!doesInputTableExists) {
            if (!ConnectionHandler.getConnection().createStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (\n" +
                    "  timestamp datetime PRIMARY KEY,\n" +
                    "  type text,\n" +
                    "  id integer\n" +
                    ")", Input.TBL_INPUTS)))
                System.out.println(Input.TBL_INPUTS + " TABLE CREATED");
            else
                System.err.println(Input.TBL_INPUTS + " TABLE CREATION FAILED");
        } else {
            System.out.println(Input.TBL_INPUTS + " TABLE ALREADY EXISTS");
        }
    }

    private void checkAndCreateTableGycemicIndex(boolean doesGlucemicIndexTableExists) throws SQLException {
        if (!doesGlucemicIndexTableExists) {
            if (!ConnectionHandler.getConnection().createStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (\n" +
                    "  id integer PRIMARY KEY,\n" +
                    "  name text,\n" +
                    "  glycemic_index integer\n" +
                    ")", GlycemicIndexByFood.TBL_FOOD_DB)))
                System.out.println(GlycemicIndexByFood.TBL_FOOD_DB + " TABLE CREATED");
            else
                System.err.println(GlycemicIndexByFood.TBL_FOOD_DB + " TABLE CREATION FAILED");
        } else {
            System.out.println(GlycemicIndexByFood.TBL_FOOD_DB + " TABLE ALREADY EXISTS");
        }
    }

    private void checkAndCreateTables(boolean doesExerciseTableExists) throws SQLException {
        if (!doesExerciseTableExists) {
            if (!ConnectionHandler.getConnection().createStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (\n" +
                    "  id integer PRIMARY KEY,\n" +
                    "  exercise text,\n" +
                    "  exercise_index integer\n" +
                    ")", Exercise.TBL_EXERCISE)))
                System.out.println(Exercise.TBL_EXERCISE + " TABLE CREATED");
            else
                System.err.println(Exercise.TBL_EXERCISE + " TABLE CREATION FAILED");
        } else {
            System.out.println(Exercise.TBL_EXERCISE + " TABLE ALREADY EXISTS");
        }
    }

    public static List<Exercise> selectAllExcerciseList() {
        List<Exercise> exerciseList = new LinkedList<>();
        try {
            ResultSet resultSet = ConnectionHandler.getConnection().createStatement().executeQuery(String.format("SELECT * FROM %s", Exercise.TBL_EXERCISE));
            while (resultSet.next())
                exerciseList.add(Exercise.createRecord(resultSet));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHandler.closeConnection();
        }
        return exerciseList;
    }

    public static List<GlycemicIndexByFood> selectAllGlycemicIndexByFoodList(){
        List<GlycemicIndexByFood> glycemicIndexByFoodList = new LinkedList<>();
        try {
            ResultSet resultSet = ConnectionHandler.getConnection().createStatement().executeQuery(String.format("SELECT * FROM %s", GlycemicIndexByFood.TBL_FOOD_DB));
            while (resultSet.next())
                glycemicIndexByFoodList.add(GlycemicIndexByFood.createRecord(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHandler.closeConnection();
        }
        return glycemicIndexByFoodList;
    }

    public static List<Input> selectAllInputs() {
        List<Input> inputList = new LinkedList<>();
        try {
            ResultSet resultSet = ConnectionHandler.getConnection().createStatement().executeQuery(String.format("SELECT * FROM %s", Input.TBL_INPUTS));
            while (resultSet.next())
                inputList.add(Input.createRecord(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHandler.closeConnection();
        }
        return inputList;
    }

    public static boolean insert(Date timestmap, double bloodSugarLevel) {
        PreparedStatement preparedStatement =  null;
        boolean result = false;
        try {
            preparedStatement = ConnectionHandler.getConnection().prepareStatement("INSERT INTO blood_sugar_levels (\"timestamp\", blood_sugar_level) VALUES (?,?)");
            preparedStatement.setLong(1, timestmap.getTime());
            preparedStatement.setDouble(2, bloodSugarLevel);
            result = !preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHandler.close(preparedStatement);
            ConnectionHandler.closeConnection();
        }
        return result;
    }

    public static boolean insert(Input input) {
        PreparedStatement preparedStatement =  null;
        boolean result = false;
        try {
            preparedStatement = ConnectionHandler.getConnection().prepareStatement("INSERT INTO inputs (timestamp, type, id, name, sugar_index) VALUES (?,?,?,?,?)");
            preparedStatement.setLong(1, input.getTimestamp().getTime());
            preparedStatement.setString(2, input.getType());
            preparedStatement.setInt(3, input.getId());
            preparedStatement.setString(4, input.getName());
            preparedStatement.setDouble(5, input.getIndex());
            result = !preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHandler.close(preparedStatement);
            ConnectionHandler.closeConnection();
        }
        return result;
    }
}
