package dbs.oracle_project_template;

import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

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
            
            ods.setUser(System.getProperty("login"));
            ods.setPassword(System.getProperty("password"));

            /**
             *
             */
            // connect to the database
            try (Connection conn = ods.getConnection()) {


//                // create a Statement
//                try (Statement stmt = conn.createStatement()) {
//                    // select something from the system's dual table
//                    try (ResultSet rset = stmt.executeQuery(
//                            "select 1+2 as col1, 3-4 as col2 from dual")) {
//                        // iterate through the result and print the values
//                        while (rset.next()) {
//                            System.out.println("col1: '" + rset.getString(1)
//                                    + "'\tcol2: '" + rset.getString(2) + "'");
//                        }
//                    } // close the ResultSet
//                } // close the Statement
            } // close the connection

        } catch (SQLException sqlEx) {
            System.err.println("SQLException: " + sqlEx.getMessage());
        }

    }
}
