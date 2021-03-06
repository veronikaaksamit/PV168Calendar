/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.User;
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
public class UserTableModel extends AbstractTableModel {

    final static Logger log = LoggerFactory.getLogger(UserTableModel.class);
    private List<User> users = new ArrayList<User>();
    
    
    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    public void clearTable() {
        users = new ArrayList<User>();
    }
    
    public User getUser(int index) {
        return users.get(index);
    }
    
    public User getUserByEmail(String email) {
        for(User u: users){
            if(u.getEmail().equals(email))
                return u;
        }
        return null;
    }
    
    public User getUserById(Long id) {
        for(User u: users){
            if(u.getId().equals(id))
                return u;
        }
        return null;
    }
    private void deleteUser(int index){
        users.remove(index);
        fireTableRowsDeleted(index, index);
    }
    
    public void deleteUsers(int[] indices){
        Integer[] rowsToDelete = DBUtils.getSortedDesc(indices);
        for(int i: rowsToDelete){
            deleteUser(i);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return user.getFullName();
            case 1:
                return user.getEmail();
            case 2:
                return user.getId();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
     @Override
    public String getColumnName(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return Localization.getRbTexts().getString("Name");
            case 1:
                return Localization.getRbTexts().getString("Email");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
     public void setUsers(List<User> usersToAdd) {
        users = usersToAdd;
        fireTableDataChanged();
    }
     
     public void addUser(User userToAdd) {
        users.add(userToAdd);
        fireTableDataChanged();
    }
     
     public void updateUser(User user){
         for(int i= 0; i < users.size(); i++){
            if(users.get(i).getId().equals(user.getId()))
                users.set(i, user);
        } 
        fireTableDataChanged();
     }
    
}
