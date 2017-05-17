/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.User;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author veronika
 */
public class UserComboBoxModel extends AbstractListModel<User> {

    private List<User> users;
    
    @Override
    public int getSize() {
        return users.size();
    }

    @Override
    public User getElementAt(int index) {
        return users.get(index);
    }
    
    public void setUsers(List<User> usersToAdd){
        users = usersToAdd;
        fireContentsChanged(users, 0, usersToAdd.size());
        
    }

}
