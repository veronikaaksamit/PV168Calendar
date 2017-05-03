package cz.muni.fi.pv168;


import java.time.LocalDateTime;

/**
 * Created by xaksamit on 10.3.17.
 */
public class Event {

    private Long id;

    private Long userId;

    private String eventName;

    private Category category;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" + id + ": " + System.lineSeparator()
                + userId + ", " + System.lineSeparator()
                + eventName + ", " + System.lineSeparator()
                + category + ", " + System.lineSeparator()
                + startDate + ", " + System.lineSeparator()
                + endDate + ", " + System.lineSeparator()
                + description +"}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj.getClass() != this.getClass()) return false;

        final Event other = (Event) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
