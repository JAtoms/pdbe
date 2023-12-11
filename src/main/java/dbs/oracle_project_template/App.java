package dbs.oracle_project_template;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("*** Oracle database for Property Registry ***");
        OracleDriver.main(args);
        System.out.println("Connecting to the Property Registry db. and running a simple query...");
        try {
            // create a OracleDataSource instance
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb");

            // Request login and password

            ods.setUser("xnwokoj00");
            ods.setPassword("yTBDz7n2");

            // connect to the database
            try (Connection conn = ods.getConnection()) {

                final List<Buildings> buildings = new ArrayList<>();

                // Create new buildings
                for (int i = 1; i <= 4; i++) {
                    buildings.add(new Buildings(i, "property" + i));
                }


                for (Buildings Buildings : buildings) {
                    // Set building title before saving to database
                    Buildings.setTitle("property" + Buildings.getCode());
                    Buildings.saveToDb(conn);
                }

                // Save images of buildings to local database
                for (Buildings Buildings : buildings) {
                    Buildings.saveImageToDbFromFile(conn, "./src/images/testImage/property" + Buildings.getCode() + ".gif");
                }

                // Save images of buildings to local database
                for (Buildings Buildings : buildings) {
                    Buildings.loadImageFromDbToFile(conn, "./src/images/result/property" + Buildings.getCode() + ".gif");
                }

                Buildings.getMostSimilarBuilding(buildings, conn);
                Buildings.getImageProperties(conn);

            } // close the connection

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

    }
}
