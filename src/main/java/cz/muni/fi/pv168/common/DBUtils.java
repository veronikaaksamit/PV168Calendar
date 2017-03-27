package cz.muni.fi.pv168.common;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by Dadka on 27.03.2017.
 */
public class DBUtils {

    private static String[] readSqlStatements(URL path) {
        try {
            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(path.openStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {

                result.append(line);
            }
            return result.toString().split(";");
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void executeSqlScript(DataSource ds, URL path) throws SQLException{
        try (Connection conn = ds.getConnection()){
            for (String sqlStatement : readSqlStatements(path)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        }
    }

}
