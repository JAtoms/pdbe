package database.multimedia;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MultimediaDatabase {

    public static void create(Connection connection) throws SQLException {
       try {
           dropTable(connection);
           dropSequence(connection);
           createSequence(connection);
           createTable(connection);
           createTrigger(connection);
           System.out.println("Table, Sequence, and Trigger created successfully.");
       } catch (SQLException e) {
           System.out.println("Table, Sequence, and Trigger creation failed.");
           e.printStackTrace();
       }
    }

    private static void createSequence(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createSequenceQuery = "CREATE SEQUENCE building_seq START WITH 1 INCREMENT BY 1";
            statement.execute(createSequenceQuery);
        }
    }

    private static void createTrigger(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTriggerQuery = "CREATE OR REPLACE TRIGGER building_generateFeatures "
                    + "BEFORE INSERT OR UPDATE OF image ON building "
                    + "FOR EACH ROW "
                    + "DECLARE "
                    + "  si ORDSYS.SI_StillImage; "
                    + "BEGIN "
                    + "  IF :NEW.image.height IS NOT NULL THEN "
                    + "    si := new SI_StillImage(:NEW.image.getContent()); "
                    + "    :NEW.image_si := si; "
                    + "    :NEW.image_ac := SI_AverageColor(si); "
                    + "    :NEW.image_ch := SI_ColorHistogram(si); "
                    + "    :NEW.image_pc := SI_PositionalColor(si); "
                    + "    :NEW.image_tx := SI_Texture(si); "
                    + "  END IF; "
                    + "END;";
            statement.execute(createTriggerQuery);
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE building ("
                    + "image_id NUMBER(3) DEFAULT building_seq.nextval NOT NULL,"
                    + "title VARCHAR2(64),"
                    + "image ORDSYS.ORDImage,"
                    + "image_si ORDSYS.SI_StillImage,"
                    + "image_ac ORDSYS.SI_AverageColor,"
                    + "image_ch ORDSYS.SI_ColorHistogram,"
                    + "image_pc ORDSYS.SI_PositionalColor,"
                    + "image_tx ORDSYS.SI_Texture)";
            statement.execute(createTableQuery);
        }
    }

    private static void dropTable(Connection connection) {

        try (Statement statement = connection.createStatement()) {
            // Drop the table if it exists
            statement.executeUpdate("DROP TABLE building");
            System.out.println("Table dropped successfully.");
        } catch (SQLException e) {

        }
    }
    private static void dropSequence(Connection connection) {

        try (Statement statement = connection.createStatement()) {
            // Drop the sequence if it exists
            statement.executeUpdate("DROP SEQUENCE building_seq");
            System.out.println("Sequence dropped successfully.");
        } catch (SQLException e) {

        }
    }

}
