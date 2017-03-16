package cz.muni.fi.pv168;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
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
    public void createEvent() throws Exception {
        Event event = newEvent("event", Category.BIRTHDAY);

        eventManager.createEvent(event);
        Long eventId = event.getId();
        Event createdEvent = eventManager.getEvent(eventId);

        assertNotNull(eventId);
        assertNotSame(event, createdEvent);
        assertDeepEquals(event, createdEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEventNull()throws Exception {
        eventManager.createEvent(null);
    }

    @Test
    public void updateEvent()throws Exception {
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
    public void updateEventNull()throws Exception {
        eventManager.updateEvent(null);
    }

    @Test
    public void deleteEvent() throws Exception {
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
    public void deleteEventNull()throws Exception {
        eventManager.deleteEvent(null);
    }
    

    @Test(expected = IllegalArgumentException.class)
    public void getEventNull()throws Exception {
        eventManager.getEvent(null);
    }

    @Test
    public void listAllEvents()throws Exception {
        List<Event> events = newEventList();
        List<Event> returnedList = eventManager.listAllEvents();

        assertTrue(events.size() == 4);
        assertTrue(events.size() == returnedList.size());
        assertDeepEquals(events, returnedList);
    }

    @Test
    public void listUserEvents()throws Exception {
        List<Event> events = newEventListOneUser();
        Event event3 = newEvent("eventName", Category.NAMEDAY);
        eventManager.createEvent(event3);
        Event event4 = newEvent("eventName", Category.PERSONAL);
        eventManager.createEvent(event4);
        events.add(event3);
        events.add(event4);

        List<Event> returnedEvents = eventManager.listUserEvents(42L);

        assertTrue(returnedEvents.size() == 2);
        assertDeepEquals(events, returnedEvents);
    }

    @Test
    public void filterEventByDate() throws Exception {
        List<Event> events = newEventList();
        LocalDateTime from = LocalDateTime.of(2017, 2, 3, 16, 30,0);
        LocalDateTime to = LocalDateTime.of(2017, 2, 4, 16, 30,0);

        LocalDateTime from2 = LocalDateTime.of(2017, 2, 4, 16, 35,0);
        LocalDateTime to2 = LocalDateTime.of(2017, 3, 5, 16, 30,0);

        List<Event> okFromTo = eventManager.filterEventByDate(events,from, to);
        assertTrue(events.size() == 4);
        assertTrue(events.size() == okFromTo.size());
        assertDeepEquals(events, okFromTo);

        List<Event> wrongFromTo = eventManager.filterEventByDate(events,from2, to2);
        assertTrue(wrongFromTo.size() == 0);
        assertFalse(events.size() == okFromTo.size());
    }

    @Test
    public void filterEventByCategory() throws Exception {
        List<Event> events = newEventList();
        List<Event> namedayEvents = eventManager.filterEventByCategory(events,  Category.NAMEDAY);
        assertTrue(namedayEvents.size() == 2);
        for(Event event : namedayEvents){
            assertEquals(event.getCategory(), Category.NAMEDAY);
        }

        List<Event> birthdayEvents = eventManager.filterEventByCategory(events,  Category.BIRTHDAY);
        assertTrue(birthdayEvents.size() == 1);
        assertEquals(birthdayEvents.get(0).getCategory(), Category.BIRTHDAY);
    }

    @Test
    public void filterEventByName()throws Exception {
        String name= "eventName";
        List<Event> events = newEventList();
        List<Event> filteredEvents = eventManager.filterEventByName(events, name);
        assertTrue(filteredEvents.size() == 2);

        for(Event event : filteredEvents){
            assertEquals(name, event.getEventName());
            assertEquals(event.getUserId().longValue(), 45L);
            assertTrue(event.getCategory() ==  Category.NAMEDAY || event.getCategory() ==  Category.PERSONAL);
        }


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

    private List<Event> newEventListOneUser(){
        List<Event> events = new ArrayList<Event>();
        User user1 = newUser("Martin", "martin@email");
        user1.setId(42L);
        userManager.createUser(user1);

        Event event1 = newEvent("event1Name", Category.BIRTHDAY, user1);
        Event event2 = newEvent("event2Name", Category.NAMEDAY, user1);
        eventManager.createEvent(event1);
        eventManager.createEvent(event2);

        events.add(event1);
        events.add(event2);
        return events;
    }

    private List<Event> newEventList(){
        List<Event> events = new ArrayList<Event>();
        events.addAll(newEventListOneUser());
        User user2 = newUser("Marek", "marek@email");
        user2.setId(45L);
        userManager.createUser(user2);

        Event event3 = newEvent("eventName", Category.NAMEDAY, user2);
        eventManager.createEvent(event3);
        Event event4 = newEvent("eventName", Category.PERSONAL, user2);
        eventManager.createEvent(event4);
        events.add(event3);
        events.add(event4);

        return events;
    }




}