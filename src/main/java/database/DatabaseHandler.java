package database;//package database;

import oracle.ord.im.OrdImage;
import java.sql.*;
import java.sql.ResultSet;
import oracle.jdbc.OracleResultSet;

public class DatabaseHandler {

    private static final String oracleUrl = "jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb";

    private static Connection connection = null;

    public static String connectToDB(String userName, String password) throws SQLException {
        try {
            connection = DriverManager.getConnection(oracleUrl, userName, password);
            selectOrdImage();
            return "Login successful"; // yTBDz7n2
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return "Invalid login credentials";
        }

    }

    public static void selectOrdImage() throws SQLException {
        final String SELECT_IMAGE = "SELECT image FROM building WHERE code = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IMAGE);

        try {
            preparedStatement.setInt(1, 1);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    final OrdImage ordImage = (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
//                    ordImage.getDataInFile(filename);
                    System.out.println("Omi are save to ./results");
                } else {
//                    throw new NotFoundException();
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }



//    public List<Shape> loadShapesFromDb(Connection connection) throws SQLException {
//         final String SQL_SELECT = "SELECT c.geometry, c.geometry.Get_WKT() FROM city c";
//        final List<Shape> shapeList = new LinkedList<>();
//        try (Statement stmt = connection.createStatement()) {
//            try (ResultSet resultSet = stmt.executeQuery(SQL_SELECT)) {
//                while (resultSet.next()) {
//                    // get a JGeometry object (the Java representation of SDO_GEOMETRY data)
//                    final byte[] image = resultSet.getBytes(1);
//                    final String wkt = resultSet.getString(2);
//                    System.out.println("loading " + wkt + " ...");
//                    final JGeometry jGeometry;
//                    try {
//                        jGeometry = JGeometry.load(image);
//                    } catch (Exception exception) {
//                        // JGeometry.load may cause an Exception which we wrap in SQLException
//                        throw new SQLException("error in JGeometry.load", exception);
//                    }
//                    // add a Shape object (the object drawable into Java GUI) into a list of drawable objects
////                    shapeList.add(jGeometryToShape(jGeometry));
//                    // a debug message
//                    System.out.println("... loaded as " + jGeometry.toStringFull());
//                }
//            }
//        }
//        return Collections.unmodifiableList(shapeList);
//    }


    public static class DataBaseException extends Exception {

    }
}
