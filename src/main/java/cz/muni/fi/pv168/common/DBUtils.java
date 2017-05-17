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


    private static final Logger logger = Logger.getLogger(DBUtils.class.getName());

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
        
        //DBUtils.executeSqlScript(ds, Main.class.getResourceAsStream("/dropTables.sql"));
        

        DBUtils.executeSqlScript(ds, Main.class.getResourceAsStream("/createTables.sql"));
        
        DBUtils.executeSqlScript(ds, Main.class.getResourceAsStream("/testData.sql"));
        return ds;
    }
    private static String[] readSqlStatements(InputStream inputStream) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read ", ex);
        }
    }

    public static void executeSqlScript(DataSource ds, InputStream inputStream) throws SQLException{
        Connection conn = ds.getConnection();
        try {
            conn = ds.getConnection();
            for (String sqlStatement : readSqlStatements(inputStream)) {
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
