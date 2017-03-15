package cz.muni.fi.pv168;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xaksamit on 10.3.17.
 */
public class EventManagerImpl implements EventManager {
    @Override
    public void createEvent(Event event) {

    }

    @Override
    public void updateEvent(Event event) {

    }

    @Override
    public void deleteEvent(Event event) {

    }

    @Override
    public Event getEvent(Long id) {
        return null;
    }

    @Override
    public List<Event> listAllEvents() {
        return null;
    }

    @Override
    public List<Event> listUserEvents(Long id) {
        return null;
    }

    @Override
    public List<Event> filterEventByDate(List<Event> list, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<Event> filterEventByCategory(List<Event> list, Category category) {
        return null;
    }

    @Override
    public List<Event> filterEventByName(List<Event> list, String eventName) {
        return null;
    }
}
