package cz.muni.fi.pv168;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xaksamit on 10.3.17.
 */
public interface EventManager {

    void createEvent(Event event);
    void updateEvent(Event event);
    void deleteEvent(Event event);

    Event getEventById(Long id);
    List<Event> listAllEvents();
    List<Event> listUserEvents(Long id);

    List<Event> filterEventByDate(List<Event> list, LocalDateTime from, LocalDateTime to);

    List<Event> filterEventByCategory(List<Event> list, Category category);

    List<Event> filterEventByName(List<Event> list, String eventName);

}
