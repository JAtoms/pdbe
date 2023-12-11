package dbs.oracle_project_template;

public class Statements {
    static final String SQL_SELECT_DATA = "SELECT title FROM building WHERE code = ?";
    static final String SQL_SELECT_IMAGE = "SELECT image FROM building WHERE code = ?";
    static final String SQL_SELECT_IMAGE_FOR_UPDATE = "SELECT image FROM building WHERE code = ? FOR UPDATE";
    static final String SQL_INSERT_NEW = "INSERT INTO building (code, title, image) VALUES (?, ?, ordsys.ordimage.init())";
    static final String SQL_UPDATE_DATA = "UPDATE building SET title = ? WHERE code = ?";
    static final String SQL_UPDATE_IMAGE = "UPDATE building SET image = ? WHERE code = ?";
    static final String SQL_UPDATE_STILLIMAGE = "UPDATE building p SET p.image_si = SI_StillImage(p.image.getContent()) WHERE p.code = ?"; // an SQL method call needs to be on table.column, not just column
    static final String SQL_UPDATE_STILLIMAGE_META = "UPDATE building SET image_ac = SI_AverageColor(image_si), image_ch = SI_ColorHistogram(image_si), image_pc = SI_PositionalColor(image_si), image_tx = SI_Texture(image_si) WHERE code = ?";
    static final String SQL_SIMILAR_IMAGE = "SELECT dst.code, SI_ScoreByFtrList(new SI_FeatureList(src.image_ac,?,src.image_ch,?,src.image_pc,?,src.image_tx,?),dst.image_si) AS similarity FROM building src, building dst WHERE (src.code = ?) AND (src.code <> dst.code) ORDER BY similarity ASC";
}
