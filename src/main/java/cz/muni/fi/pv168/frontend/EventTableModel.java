/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.Event;
import cz.muni.fi.pv168.common.DBUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author veronika
 */
public class EventTableModel extends AbstractTableModel {

    final static Logger log = LoggerFactory.getLogger(EventTableModel.class);
    private List<Event> events = new ArrayList<Event>();
    
    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }
    
    public Event getEvent(int index) {
        return events.get(index);
    }
    
    private void deleteEvent(int index){
        events.remove(index);
        fireTableRowsDeleted(index, index);
    }
    
    public void deleteEvents(int[] indices){
        Integer[] rowsToDelete = DBUtils.getSortedDesc(indices);
        for(int i: rowsToDelete){
            deleteEvent(i);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return event.getEventName();
            case 1:
                return event.getCategory();
            case 2:
                return event.getStartDate().toLocalDate() + " " + event.getStartDate().toLocalTime();
            case 3:
                return event.getEndDate().toLocalDate() + " " + event.getEndDate().toLocalTime();
            case 4:
                return event.getDescription();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
     @Override
    public String getColumnName(int columnIndex) {

        ResourceBundle rb = ResourceBundle.getBundle("texts");
        switch (columnIndex) {
            case 0:
                return rb.getString("event-name");
            case 1:
                return rb.getString("category");
            case 2:
                return rb.getString("start-date");
            case 3:
                return rb.getString("end-date");
            case 4:
                return rb.getString("description");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
     public void setEvents(List<Event> eventsToAdd) {
        events = eventsToAdd;
        fireTableDataChanged();
    }
     
     public void addEvent(Event eventToAdd) {
        events.add(eventToAdd);
        fireTableDataChanged();
    }
     
     public void updateEvent(Event event){
        for(int i= 0; i < events.size(); i++){
            if(events.get(i).getId().equals(event.getId()))
                events.set(i, event);
        } 
         
        fireTableDataChanged();
     }
    
}
