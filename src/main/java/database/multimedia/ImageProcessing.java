package database.multimedia;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static database.Statements.*;

public class ImageProcessing {

    private static Connection connection;

    public ImageProcessing(Connection connection) {
        ImageProcessing.connection = connection;
    }


    public List<String> insertImage(File imageFile, int imageID, String title) throws SQLException, DataBaseException, IOException {
        final boolean previousAutoCommit = connection.getAutoCommit();

        String insertionStatus = "Image insertion failed", imageProperties = "";
        List<String> imageList = new ArrayList<>();

        Random rand = new Random();
        int code = imageID != 101 ? imageID : rand.nextInt(100);

        connection.setAutoCommit(false);
        try {
            OrdImage ordImage;
            System.out.println("Inserting image...");
            try {
                // at first, try to get the image from an existing row
                ordImage = returnSingleImage(code, imageID != 101);
            } catch (SQLException | DataBaseException ex) {
                try (PreparedStatement preparedStatementInsert = connection.prepareStatement(INSERT_NEW_IMAGE)) {
                    preparedStatementInsert.setInt(1, code);
                    preparedStatementInsert.setString(2, imageFile.getName());
                    // insert a new row if the suitable row does not exist
                    preparedStatementInsert.executeUpdate();
                }
                // get the image from the previously inserted row
                ordImage = returnSingleImage(code, imageID != 101);
            }
            ordImage.loadDataFromFile(imageFile.getAbsolutePath());
            ordImage.setProperties();
            updateImage(code, ordImage, title, imageID != 101);
            insertionStatus = "Image inserted successfully";
            String compressionFormat = ordImage.getCompressionFormat();
            String format = ordImage.getFormat();
            String height = String.valueOf(ordImage.getHeight());
            String width = String.valueOf(ordImage.getWidth());
            String mimeType = ordImage.getMimeType();
            imageProperties = "Height: " + height + "\n" +
                    "Width: " + width + "\n" +
                    "Format: " + format + "\n" +
                    "Mime type: " + mimeType + "\n" +
                    "Compression format: " + compressionFormat + "\n";
            imageList.add(insertionStatus);
            imageList.add(imageProperties);
            saveReturnedImage(code);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
        System.out.println(insertionStatus);
        return imageList;
    }


    // Delete image
    public String deleteImage(int code) throws SQLException {
        String deletionStatus;
        System.out.println("Deleting image...");
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_IMAGE)) {
            preparedStatement.setInt(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                deletionStatus = "Image with ID " + code + " was deleted.";
                System.out.println(deletionStatus);
            } catch (Exception e) {
                deletionStatus = "Error in deleting image with ID " + code + ".";
            }
        }
        return deletionStatus;
    }

    private OrdImage returnSingleImage(int image_id, Boolean isUpdate) throws SQLException, DataBaseException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(isUpdate ? SELECT_IMAGE_FOR_UPDATE : SELECT_IMAGE)) {
            preparedStatement.setInt(1, image_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    final OracleResultSet oracleResultSet = (OracleResultSet) resultSet;
                    System.out.println("Image found with the given ID.");
                    return (OrdImage) oracleResultSet.getORAData(1, OrdImage.getORADataFactory());
                } else {
                    throw new DataBaseException();
                }
            }
        }
    }

    private void updateImage(int imageId, OrdImage ordImage, String title, Boolean isUpdate) {

        try (PreparedStatement preparedStatement = connection.prepareStatement(isUpdate ? UPDATE_IMAGE_WITH_TITLE : UPDATE_IMAGE)) {
            final OraclePreparedStatement oraclePreparedStatement = (OraclePreparedStatement) preparedStatement;
            oraclePreparedStatement.setORAData(1, ordImage);
            preparedStatement.setInt(2, imageId);
            if (title != null)
                preparedStatement.setString(3, title);
            preparedStatement.executeUpdate();
            System.out.println("Image inserted successfully: " + imageId);
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
                    ordImage.getDataInFile("src/main/resources/results/newImage" + ".png");
                    System.out.println("Image saved to ./results");
                } else {
                    System.out.println("No image found with the given ID.");
                }
            }
        }
    }

    public String getAlLImages() throws SQLException {
        List<String> imageList = new ArrayList<>();
        StringBuilder buildingData = new StringBuilder("No data in database");
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String imageId = String.valueOf((resultSet.getLong("image_id")));
                    String mageTitle = (resultSet.getString("title"));
                    String data = imageId + " - " + mageTitle + "\n";
                    if (buildingData.indexOf("No data in database") != -1)
                        buildingData = new StringBuilder();
                    imageList.add(data);
                    while (!imageList.isEmpty()) {
                        buildingData.append(imageList.getFirst());
                        imageList.removeFirst();
                    }
                }
            }
        }
        return buildingData.toString();
    }

    public String clearDB() {
        String deletionStatus;
        try (PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_TABLE)) {
            preparedStatement.executeUpdate();
            deletionStatus = ("Database cleared");
            System.out.println("Database cleared");
        } catch (Exception exception) {
            deletionStatus = ("Error in clearing database");
            System.out.println("Error in clearing database");
        }
        return deletionStatus;
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
//                    updateImage(imageId, ordImage);
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

    //
    // The buildings should be represented by polygons, the swimming pool by a circle, and the football pitch by a rectangle.
    // The buildings should have a name, the swimming pool should have a name and a capacity, and the football pitch should have a name and a size.
    // The buildings should be located in the city of Brno, the swimming pool in the city of Bratislava, and the football pitch in the city of Prague.


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
