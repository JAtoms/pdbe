package database;

public class Statements {
    public static final String INSERT_NEW_IMAGE = "INSERT INTO building (image_id, title, image) VALUES (?, ?, ordsys.ordimage.init())";
    public static final String UPDATE_IMAGE = "UPDATE building SET image = ? WHERE image_id = ?";
    public static final String DELETE_IMAGE = "DELETE FROM building WHERE image_id = ?";
    public static final String SELECT_IMAGE = "SELECT image FROM building WHERE image_id = ?";

    public static final String SELECT_IMAGE_FOR_UPDATE = "SELECT image FROM building WHERE image_id = ? FOR UPDATE";

}
