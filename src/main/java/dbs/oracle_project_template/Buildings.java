package dbs.oracle_project_template;

import static dbs.oracle_project_template.Statements.SQL_INSERT_NEW;
import static dbs.oracle_project_template.Statements.SQL_SELECT_DATA;
import static dbs.oracle_project_template.Statements.SQL_SELECT_IMAGE;
import static dbs.oracle_project_template.Statements.SQL_SELECT_IMAGE_FOR_UPDATE;
import static dbs.oracle_project_template.Statements.SQL_SIMILAR_IMAGE;
import static dbs.oracle_project_template.Statements.SQL_UPDATE_DATA;
import static dbs.oracle_project_template.Statements.SQL_UPDATE_IMAGE;
import static dbs.oracle_project_template.Statements.SQL_UPDATE_STILLIMAGE;
import static dbs.oracle_project_template.Statements.SQL_UPDATE_STILLIMAGE_META;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

public class Buildings {
    private int code;
    private String title;


    public Buildings(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public Buildings(Connection connection, int code) throws NotFoundException, SQLException {
        this.code = code;
        // load the rest of properties from the database
        loadFromDb(connection);
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // To load the properties of a building based on its code from a database.
    public void loadFromDb(Connection connection) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_DATA)) {
            preparedStatement.setInt(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.title = resultSet.getString(1);
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    // To save properties of a building to a database.
    // If the building does not exist, it is created.
    // If it exists, it is updated.
    public void saveToDb(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatementInsert = connection.prepareStatement(SQL_INSERT_NEW)) {
            preparedStatementInsert.setInt(1, code);
            preparedStatementInsert.setString(2, title);
            try {
                // try insert before update
                preparedStatementInsert.executeUpdate();
            } catch (SQLException sqlException) {
                try (PreparedStatement preparedStatementUpdate = connection.prepareStatement(SQL_UPDATE_DATA)) {
                    preparedStatementUpdate.setString(1, title);
                    preparedStatementUpdate.setInt(2, code);
                    // try the update id the insert failed
                    preparedStatementUpdate.executeUpdate();
                }
            }
        }
    }


    // To save an image of the product to a database from a local file.
    public void saveImageToDbFromFile(Connection connection, String filename) throws SQLException, NotFoundException, IOException {
        final boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            OrdImage ordImage;
            try {
                // at first, try to get the image from an existing row
                ordImage = selectOrdImageForUpdate(connection);
            } catch (SQLException | NotFoundException ex) {
                try (PreparedStatement preparedStatementInsert = connection.prepareStatement(SQL_INSERT_NEW)) {
                    preparedStatementInsert.setInt(1, code);
                    preparedStatementInsert.setString(2, title);
                    // insert a new row if the suitable row does not exist
                    preparedStatementInsert.executeUpdate();
                }
                // get the image from the previously inserted row
                ordImage = selectOrdImageForUpdate(connection);
            }
            ordImage.loadDataFromFile(filename);
            ordImage.setProperties();
            try (PreparedStatement preparedStatementUpdate = connection.prepareStatement(SQL_UPDATE_IMAGE)) {
                final OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) preparedStatementUpdate;
                oraclePreparedStatement.setORAData(1, ordImage);
                preparedStatementUpdate.setInt(2, code);
                preparedStatementUpdate.executeUpdate();
            }
            recreateStillImageData(connection);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    // To save an image of the product to a database.
    private OrdImage selectOrdImageForUpdate(Connection connection) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_IMAGE_FOR_UPDATE)) {
            preparedStatement.setInt(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    return (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    // To recreate still image data of the product in a database.
    private void recreateStillImageData(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatementSi = connection.prepareStatement(SQL_UPDATE_STILLIMAGE)) {
            preparedStatementSi.setInt(1, code);
            preparedStatementSi.executeUpdate();
        }
        try (PreparedStatement preparedStatementSiMeta = connection.prepareStatement(SQL_UPDATE_STILLIMAGE_META)) {
            preparedStatementSiMeta.setInt(1, code);
            preparedStatementSiMeta.executeUpdate();
        }
    }


    // To load building images from database and save it to a local file.
    public void loadImageFromDbToFile(Connection connection, String filename) throws SQLException, NotFoundException, IOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_IMAGE)) {
            preparedStatement.setInt(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    final OrdImage ordImage = (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                    ordImage.getDataInFile(filename);
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }

    public static void getMostSimilarBuilding(final List<Buildings> buildings, Connection conn) {
        // Search similarity of building images
        final Buildings firstBuildings = buildings.get(0);
        final Buildings similarBuildings;
        try {
            similarBuildings = firstBuildings.findTheMostSimilar(conn, 0.3, 0.3, 0.1, 0.3);
        } catch (SQLException | NotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\nThe most similar to building" + firstBuildings.getCode() + " with title " + firstBuildings.getTitle() + " is building" + similarBuildings.getCode());

    }

    public static void getImageProperties(Connection connection) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_IMAGE)) {
            preparedStatement.setInt(1, 1);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    final OrdImage ordImage = (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                    System.out.println("\nProperties of image" + ordImage.getFormat() + "\n"
                            + "Height: " + ordImage.getHeight() + "\n"
                            + "Width: " + ordImage.getWidth() + "\n"
                            + "Update time:" + ordImage.getUpdateTime());
                } else {
//                    throw new NotFoundException();
                }
            }
        }
    }


    /**
     * Find a buildings with the most similar image to the current buildings based on several criteria.
     *
     * @param connection database connection
     * @param weightAC   average color criteria
     * @param weightCH   color histogram criteria
     * @param weightPC   positional color criteria
     * @param weightTX   texture criteria
     * @return object of the found buildings
     * @throws SQLException      SQL error
     * @throws NotFoundException the suitable buildings is not in the database
     */
    public Buildings findTheMostSimilar(Connection connection, double weightAC, double weightCH, double weightPC, double weightTX) throws SQLException, NotFoundException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SIMILAR_IMAGE)) {
            preparedStatement.setDouble(1, weightAC);
            preparedStatement.setDouble(2, weightCH);
            preparedStatement.setDouble(3, weightPC);
            preparedStatement.setDouble(4, weightTX);
            preparedStatement.setInt(5, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final int code = resultSet.getInt(1);
                    return new Buildings(connection, code);
                } else {
                    throw new NotFoundException();
                }
            }
        }
    }


    public class NotFoundException extends Exception {
        // nothing to extend
    }

}

