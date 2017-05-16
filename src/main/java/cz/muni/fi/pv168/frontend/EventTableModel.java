/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.Event;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return event.getEventName();
            case 1:
                return event.getCategory();
            case 2:
                return event.getStartDate();
            case 3:
                return event.getEndDate();
            case 4:
                return event.getDescription();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
     public void setEvents(List<Event> eventsToAdd) {
        events = eventsToAdd;
        fireTableDataChanged();
    }
    
}
