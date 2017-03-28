package cz.muni.fi.pv168;

import cz.muni.fi.pv168.common.DBUtils;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

/**
 * Created by xaksamit on 24.3.17.
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException{

        Properties dbConf = new Properties();

        dbConf.load(Main.class.getResourceAsStream("/dbConf.properties"));


        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(dbConf.getProperty("jdbc.url"));
        ds.setUsername(dbConf.getProperty("jdbc.user"));
        ds.setPassword(dbConf.getProperty("jdbc.password"));

        //DBUtils.executeSqlScript(ds, Main.class.getResource("/dropTables.sql"));
        DBUtils.executeSqlScript(ds, Main.class.getResource("/createTables.sql"));


        UserManager userManager = new UserManagerImpl(ds);

        User user = new User();
        user.setEmail("bla");
        user.setFullName("BLA");
        userManager.createUser(user);
        List<User> users = userManager.getAllUsers();
        
        for (User u: users) {
            System.out.println(u.toString());
        }

        Event event = new Event();
        event.setCategory(Category.BIRTHDAY);
        event.setUserId(user.getId());
        event.setEventName("event1");
        event.setDescription("desc");
        event.setStartDate(LocalDateTime.of(2016, 12, 5, 11, 00,0));
        event.setEndDate(LocalDateTime.of(2016, 12, 5, 12, 00,0));


        EventManager eventManager = new EventManagerImpl(ds);

        eventManager.createEvent(event);
        event.setEventName("coolerNameForEvent");
        eventManager.updateEvent(event);
        List<Event> events = eventManager.listAllEvents();

        for (Event e: events) {
            System.out.println(e.toString());
        }
        eventManager.deleteEvent(event);
        for (Event e: events) {
            System.out.println(e.toString());
        }




    }
}
