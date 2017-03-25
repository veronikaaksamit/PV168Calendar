package cz.muni.fi.pv168;

import org.apache.derby.client.am.DateTime;
import org.apache.derby.client.am.SqlException;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xaksamit on 10.3.17.
 */
public class EventManagerImpl implements EventManager {

    private final DataSource dataSource;

    public EventManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createEvent(Event event) {
        try(Connection conn = dataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement
                    ("INSERT INTO EVENT (USERID, EVENTNAME, DESCRIPTION, STARTDATE, ENDDATE, CATEGORY) VALUES(?,?,?,?,?,?)",
                            PreparedStatement.RETURN_GENERATED_KEYS)){
                ps.setLong(1, event.getUserId());
                ps.setString(2, event.getEventName());
                ps.setString(3, event.getDescription());

                LocalDateTime startDate = event.getStartDate();
                ps.setObject(4, startDate == null ? null : startDate.toString(), Types.TIMESTAMP);

                LocalDateTime endDate = event.getEndDate();
                ps.setObject(5, endDate == null ? null : endDate.toString(), Types.TIMESTAMP);

                ps.setInt(6, event.getCategory().ordinal());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        event.setId(id);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateEvent(Event event) {
        try(Connection conn = dataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement
                    ("UPDATE EVENT SET USERID=?, EVENTNAME=?, DESCRIPTION=?, STARTDATE=?, ENDDATE=?, CATEGORY=? WHERE id = ?")){
                ps.setLong(7, event.getId());

                ps.setLong(1, event.getUserId());
                ps.setString(2, event.getEventName());
                ps.setString(3, event.getDescription());

                LocalDateTime startDate = event.getStartDate();
                ps.setObject(4, startDate == null ? null : startDate.toString(), Types.TIMESTAMP);

                LocalDateTime endDate = event.getEndDate();
                ps.setObject(5, endDate == null ? null : endDate.toString(), Types.TIMESTAMP);

                ps.setInt(6, event.getCategory().ordinal());
                int n = ps.executeUpdate();
                if (n != 1) {
                    //ERROR //throw new BookException("not updated book with id " + book.getId(), null);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEvent(Event event) {
        try(Connection conn = dataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM EVENT WHERE id = ?")){
                ps.setLong(1,event.getId());
                int n = ps.executeUpdate();
                if (n != 1) {
                    //ERROR //throw new BookException("not updated book with id " + book.getId(), null);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event getEvent(Long id) {
        Event result = new Event();
        try(Connection conn = dataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM EVENT WHERE ID = ?")){
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.setId(rs.getLong(1));
                        result.setUserId(rs.getLong(2));
                        result.setEventName(rs.getString(3));
                        result.setDescription(rs.getString(4));
                        result.setStartDate(rs.getTimestamp(5).toLocalDateTime());
                        result.setEndDate(rs.getTimestamp(6).toLocalDateTime());
                        result.setCategory(Category.fromInteger(rs.getInt(7)));
                        return result;
                    } else {
                        return null;
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Event> listAllEvents() {
        try(Connection conn = dataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM EVENT")){
                try(ResultSet rs = ps.executeQuery()){
                    List<Event> events = new ArrayList<>();
                    while(rs.next()){
                        Event e = new Event();
                        e.setId(rs.getLong(1));
                        e.setUserId(rs.getLong(2));
                        e.setEventName(rs.getString(3));
                        e.setDescription(rs.getString(4));
                        e.setStartDate(rs.getTimestamp(5).toLocalDateTime());
                        e.setEndDate(rs.getTimestamp(6).toLocalDateTime());
                        e.setCategory(Category.fromInteger(rs.getInt(7)));
                        events.add(e);
                    }
                    return events;
                }
            }
        }
        catch(SQLException ex){
            //throw Exception("Could not select all Events", ex); //NEED TO CORRECT
            System.out.println("Could not select all Events");
        }
        return null;
    }

    @Override
    public List<Event> listUserEvents(Long id) {
        List<Event> userEvents = new ArrayList<>();
        try(Connection conn = dataSource.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM EVENT WHERE USERID = ?")){
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()){
                        Event e = new Event();
                        e.setId(rs.getLong(1));
                        e.setUserId(rs.getLong(2));
                        e.setEventName(rs.getString(3));
                        e.setDescription(rs.getString(4));
                        e.setStartDate(rs.getTimestamp(5).toLocalDateTime());
                        e.setEndDate(rs.getTimestamp(6).toLocalDateTime());
                        e.setCategory(Category.fromInteger(rs.getInt(7)));
                        userEvents.add(e);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Event> filterEventByDate(List<Event> list, LocalDateTime from, LocalDateTime to) {
        List<Event> events = new ArrayList<>();
        for (Event e: list) {

            //ERROR NEED TO IMPLEMENT
           // if(e.getStartDate().){
              //  events.add(e);
            //}
        }
        return events;
    }

    @Override
    public List<Event> filterEventByCategory(List<Event> list, Category category) {
        List<Event> events = new ArrayList<>();
        for (Event e: list) {
            if(e.getCategory().equals(category)){
                events.add(e);
            }
        }
        return events;
    }

    @Override
    public List<Event> filterEventByName(List<Event> list, String eventName) {
        List<Event> events = new ArrayList<>();
        for (Event e: list) {
            if(e.getEventName().equals(eventName)){
                events.add(e);
            }
        }
        return events;
    }
}
