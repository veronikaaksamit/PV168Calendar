package cz.muni.fi.pv168;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xaksamit on 10.3.17.
 */
public interface EventManager {

    /**
     * Creates new event
     * @exception IllegalArgumentException
     * @param event event to create
     */
    void createEvent(Event event);

    /**
     * Update existing event
     * @exception IllegalArgumentException
     * @param event event to update
     */
    void updateEvent(Event event);

    /**
     * Delete existing event
     * @exception IllegalArgumentException
     * @param event event to delete
     */
    void deleteEvent(Event event);

    /**
     * get event by id
     * @exception IllegalArgumentException
     * @param id event id
     * @return event
     */
    Event getEvent(Long id);

    /**
     * list all exisitng events
     * @exception IllegalArgumentException
     * @return list of events
     */
    List<Event> listAllEvents();

    /**
     * list all user events
     * @exception IllegalArgumentException
     * @param id user id
     * @return list of user events
     */
    List<Event> listUserEvents(Long id);

    /**
     * filter events by date
     * @param list list to filter
     * @param from from date
     * @param to to date
     * @return filtered events
     */
    List<Event> filterEventByDate(List<Event> list, LocalDateTime from, LocalDateTime to);

    /**
     * filter events by category
     * @param list list to filter
     * @param category category
     * @return filtered events
     */
    List<Event> filterEventByCategory(List<Event> list, Category category);

    /**
     * filter events by event name
     * @param list list to filter
     * @param eventName event name
     * @return filtered events
     */
    List<Event> filterEventByName(List<Event> list, String eventName);

}
