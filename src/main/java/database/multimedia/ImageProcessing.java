package database.multimedia;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import static database.Statements.*;

public class ImageProcessing {

    private static Connection connection;

    public ImageProcessing(Connection connection) {
        ImageProcessing.connection = connection;
    }

    // Pick image and insert it to the database
    public void pickImage() throws SQLException, IOException, DataBaseException {

        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();

        // Set the file chooser to select only files (not directories)
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Set the file filter to show only image files
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".jpg") ||
                        file.getName().toLowerCase().endsWith(".jpeg") ||
                        file.getName().toLowerCase().endsWith(".png") ||
                        file.isDirectory();
            }

            public String getDescription() {
                return "Image files (*.jpg, *.jpeg, *.png)";
            }
        });
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            insertImage(fileChooser.getSelectedFile());
        } else {
            System.out.println("No file selected.");
        }
    }

    public void insertImage(File imageFile) throws SQLException, DataBaseException, IOException {
        final boolean previousAutoCommit = connection.getAutoCommit();

        Random rand = new Random();
        int code = rand.nextInt(100);

        connection.setAutoCommit(false);
        try {
            OrdImage ordImage;
            try {
                // at first, try to get the image from an existing row
                ordImage = returnSingleImage(code);
            } catch (SQLException | DataBaseException ex) {
                try (PreparedStatement preparedStatementInsert = connection.prepareStatement(INSERT_NEW_IMAGE)) {
                    preparedStatementInsert.setInt(1, code);
                    preparedStatementInsert.setString(2, imageFile.getName());
                    // insert a new row if the suitable row does not exist
                    preparedStatementInsert.executeUpdate();
                }
                // get the image from the previously inserted row
                ordImage = returnSingleImage(code);
            }
            ordImage.loadDataFromFile(imageFile.getAbsolutePath());
            ordImage.setProperties();
            updateImage(code, ordImage);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }


    // Delete image
    public void deleteImage(int code) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_IMAGE)) {
            preparedStatement.setInt(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Building with code " + code + " was deleted.");
            } catch (Exception e) {
                System.out.println("Error in deleting image");
            }
        }
    }

    public OrdImage returnSingleImage(int image_id) throws SQLException, DataBaseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IMAGE)) {
            preparedStatement.setInt(1, image_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    return (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                } else {
                    throw new DataBaseException();
                }
            }
        }
    }

    private void updateImage(int imageId, OrdImage ordImage) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_IMAGE)) {
            final OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) preparedStatement;
            oraclePreparedStatement.setORAData(1, ordImage);
            preparedStatement.setInt(2, imageId);
            preparedStatement.executeUpdate();
            System.out.println("Image inserted successfully" + imageId);
        } catch (Exception exception) {
            System.out.println("Error inserting Image");
        }
    }

    public void saveReturnedImage(int imageId) throws SQLException, IOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IMAGE)) {
            preparedStatement.setInt(1, imageId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    final OrdImage ordImage = (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                    ordImage.getDataInFile("src/main/resources/results/newImage" + imageId + ".png");
                    System.out.println("Image saved to ./results");
                } else {
                    System.out.println("No image found with the given ID.");
                }
            }
        }
    }

    public void rotateImage(int imageId, float angle) {

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_IMAGE_FOR_UPDATE)) {
            preparedStatement.setInt(1, imageId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    OrdImage ordImage = (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                    ordImage.process("rotate=" + angle);
                    // Update the rotated image back to the database
                    updateImage(imageId, ordImage);
                } else {
                    System.out.println("Image not found for ID: " + imageId);
                }
            }
        } catch (Exception exception) {
            System.out.println("Error in rotating image" + exception.getMessage());
        }

    }

    private static void queryImageByContent(File imageFilepath) throws
            SQLException, IOException {
        byte[] queryImageData = readImageFromFile(imageFilepath.getPath());

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM building WHERE = ?")) {
            ((OraclePreparedStatement) preparedStatement).setBytesForBlob(1, queryImageData);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int imageId = resultSet.getInt("image_id");
                String title = resultSet.getString("title");
                System.out.println("Matching image found - ID: " + imageId + ", Title: " + title);
            }
        }
    }

    private static byte[] readImageFromFile(String imagePath) throws IOException {
        File file = new File(imagePath);
        byte[] imageData = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(imageData);
        }
        return imageData;
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
