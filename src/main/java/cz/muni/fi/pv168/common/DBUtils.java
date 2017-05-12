package cz.muni.fi.pv168.common;

import cz.muni.fi.pv168.Main;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Dadka on 27.03.2017.
 */
public class DBUtils {


    private static final Logger logger = Logger.getLogger(
            DBUtils.class.getName());

    /**
     * Closes connection and logs possible error.
     *
     * @param conn connection to close
     * @param statements  statements to close
     */
    public static void closeQuietly(Connection conn, Statement ... statements) {
        for (Statement st : statements) {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Error when closing statement", ex);
                }
            }
        }
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when switching autocommit mode back to true", ex);
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when closing connection", ex);
            }
        }
    }

    public static DataSource initDB() throws SQLException, IOException{
        Properties dbConf = new Properties();
        dbConf.load(Main.class.getResourceAsStream("/dbConf.properties"));

        BasicDataSource ds = new BasicDataSource();
        
        
        ds.setUrl(dbConf.getProperty("jdbc.url"));
        ds.setUsername(dbConf.getProperty("jdbc.user"));
        ds.setPassword(dbConf.getProperty("jdbc.password"));

        DBUtils.executeSqlScript(ds, Main.class.getResource("/dropTables.sql"));
        DBUtils.executeSqlScript(ds, Main.class.getResource("/createTables.sql"));
        
        DBUtils.executeSqlScript(ds, Main.class.getResource("/testData.sql"));
        return ds;
    }
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
        Connection conn = ds.getConnection();
        try {
            conn = ds.getConnection();
            for (String sqlStatement : readSqlStatements(path)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        }
        finally
        {
            closeQuietly(conn);
        }
    }

}
