package dbs.oracle_project_template;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.ord.im.OrdImage;

public class Statements {
    static final String SQL_SELECT_DATA = "SELECT title FROM buildings WHERE code = ?";
    static final String SQL_SELECT_IMAGE = "SELECT image FROM buildings WHERE code = ?";
    static final String SQL_SELECT_IMAGE_FOR_UPDATE = "SELECT image FROM buildings WHERE code = ? FOR UPDATE";
    static final String SQL_INSERT_NEW = "INSERT INTO buildings (code, title, image) VALUES (?, ?, ordsys.ordimage.init())";
    static final String SQL_UPDATE_DATA = "UPDATE buildings SET title = ? WHERE code = ?";
    static final String SQL_UPDATE_IMAGE = "UPDATE buildings SET image = ? WHERE code = ?";
    static final String SQL_UPDATE_STILLIMAGE = "UPDATE buildings p SET p.image_si = SI_StillImage(p.image.getContent()) WHERE p.code = ?"; // an SQL method call needs to be on table.column, not just column
    static final String SQL_UPDATE_STILLIMAGE_META = "UPDATE buildings SET image_ac = SI_AverageColor(image_si), image_ch = SI_ColorHistogram(image_si), image_pc = SI_PositionalColor(image_si), image_tx = SI_Texture(image_si) WHERE code = ?";
    static final String SQL_SIMILAR_IMAGE = "SELECT dst.code, SI_ScoreByFtrList(new SI_FeatureList(src.image_ac,?,src.image_ch,?,src.image_pc,?,src.image_tx,?),dst.image_si) AS similarity FROM buildings src, buildings dst WHERE (src.code = ?) AND (src.code <> dst.code) ORDER BY similarity ASC";
}
