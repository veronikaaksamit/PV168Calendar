package cz.muni.fi.pv168;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by xaksamit on 10.3.17.
 */
public class EventManagerImplTest {

    private EventManager eventManager;
    private UserManager userManager;

    @Before
    public void setUp() throws Exception {
        eventManager = new EventManagerImpl();
        userManager = new UserManagerImpl();
    }

    @Test
    public void createEvent(){
        Event event = newEvent("event", Category.BIRTHDAY);

        eventManager.createEvent(event);
        Long eventId = event.getId();
        Event createdEvent = eventManager.getEvent(eventId);

        assertNotNull(eventId);
        assertNotSame(event, createdEvent);
        assertDeepEquals(event, createdEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEventNull() {
        eventManager.createEvent(null);
    }

    @Test
    public void updateEvent(){
        User user1 = newUser("Marek", "email@email.com");
        userManager.createUser(user1);
        Event event1 = newEvent("event1Name", Category.BIRTHDAY, user1);
        eventManager.createEvent(event1);

        User user2 = newUser("Matus", "matus@email.com");
        userManager.createUser(user2);
        Event event2 = newEvent("event2Name", Category.NAMEDAY, user2);
        eventManager.createEvent(event2);

        eventManager.updateEvent(event1);

        event2.setEventName("eventName");
        event2.setCategory(Category.OTHER);
        event2.setStartDate(LocalDateTime.of(2016, 12, 5, 12, 00,0));
        event2.setUserId(user1.getId());
        eventManager.updateEvent(event2);

        Event updatedEvent1 = eventManager.getEvent(event1.getId());
        Event updatedEvent2 = eventManager.getEvent(event2.getId());

        assertEquals(event1.getEventName(), updatedEvent1.getEventName());
        assertEquals(event1.getUserId(), updatedEvent1.getUserId());
        assertEquals(event1.getCategory(), updatedEvent1.getCategory());
        assertEquals(event1.getStartDate(), updatedEvent1.getStartDate());
        assertEquals(event1.getEndDate(), updatedEvent1.getEndDate());
        assertEquals(event1.getDescription(), updatedEvent1.getDescription());

        assertEquals("eventName", updatedEvent2.getEventName());
        assertEquals(user1.getId(), updatedEvent2.getUserId());
        assertEquals(Category.OTHER, updatedEvent2.getCategory());
        assertEquals(LocalDateTime.of(2016, 12, 5, 12, 00,0), updatedEvent2.getStartDate());
        assertEquals(event2.getEndDate(), updatedEvent2.getEndDate());
        assertEquals(event2.getDescription(), updatedEvent2.getDescription());

        assertDeepEquals(event1, eventManager.getEvent(event1.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateEventNull(){
        eventManager.updateEvent(null);
    }

    @Test
    public void deleteEvent(){
        Event event1 = newEvent("event1Name", Category.BIRTHDAY);
        eventManager.createEvent(event1);
        Event event2 = newEvent("event2Name", Category.NAMEDAY);
        eventManager.createEvent(event2);

        List<Event> events = eventManager.listAllEvents();
        assertTrue(events.size() == 2);

        eventManager.deleteEvent(event1);
        events = eventManager.listAllEvents();
        assertTrue(events.size() == 1);
        assertDeepEquals(event2, events.get(0));

        eventManager.deleteEvent(event2);
        events = eventManager.listAllEvents();
        assertTrue(events.size() == 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteEventNull(){
        eventManager.deleteEvent(null);
    }

    @Test
    public void getEvent(){


    }

    @Test(expected = IllegalArgumentException.class)
    public void getEventNull(){
        eventManager.getEvent(null);

    }

    @Test
    public void listAllEvents(){

    }

    @Test
    public void listUserEvents(){

    }

    @Test
    public void filterEventByDate(){

    }

    @Test
    public void filterEventByCategory(){

    }

    @Test
    public void filterEventByName(){

    }


    private void assertDeepEquals(Event expected, Event actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getEventName(), actual.getEventName());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    private void assertDeepEquals(List<Event> expected, List<Event> actual){
        assertTrue(expected.size() == actual.size());

        for( int i = 0; i< expected.size(); i++){
            assertDeepEquals(expected.get(i), actual.get(i));
        }
    }

    private User newUser(String name, String email) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        return user;
    }

    private Event newEvent(String name, Category category, User user) {
        Event event = new Event();

        event.setUserId(user.getId());
        event.setEventName(name);
        event.setCategory(category);
        event.setStartDate(LocalDateTime.of(2017, 2, 3, 16, 30,0));
        event.setEndDate(LocalDateTime.of(2017, 2, 4, 16, 30,0));
        event.setDescription("description");
        return event;
    }

    private Event newEvent(String name, Category category) {
        User user1 = newUser("Marek", "email");
        userManager.createUser(user1);
        Event event = new Event();

        event.setUserId(user1.getId());
        event.setEventName(name);
        event.setCategory(category);
        event.setStartDate(LocalDateTime.of(2017, 2, 3, 16, 30,0));
        event.setEndDate(LocalDateTime.of(2017, 2, 4, 16, 30,0));
        event.setDescription("description");
        return event;
    }





}