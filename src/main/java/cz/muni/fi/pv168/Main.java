package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by xaksamit on 24.3.17.
 */
public class Main {

    public static void main(String[] args) throws IOException{

        Properties dbConf = new Properties();
        dbConf.load(Main.class.getResourceAsStream("/dbConf.properties"));

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc.url");
        ds.setUsername("jdbc.user");
        ds.setPassword("jdbc.password");

        UserManager userManager = new UserManagerImpl(ds);

        List<User> users = userManager.getAllUsers();
        
        for (User u: users) {
            System.out.println(u.toString());
        }

        EventManager eventManager = new EventManagerImpl(ds);
        List<Event> events = eventManager.listAllEvents();

        for (Event e: events) {
            System.out.println(e.toString());
        }


    }
}
