package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.Event;
import cz.muni.fi.pv168.EventManager;
import cz.muni.fi.pv168.EventManagerImpl;
import cz.muni.fi.pv168.Main;
import cz.muni.fi.pv168.User;
import cz.muni.fi.pv168.UserManager;
import cz.muni.fi.pv168.UserManagerImpl;
import cz.muni.fi.pv168.common.DBUtils;
import cz.muni.fi.pv168.common.ServiceFailureException;
import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.sql.DataSource;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.*;

/**
 *
 * @author veronika
 */
public class CalendarGUI extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(CalendarGUI.class);
    private DataSource ds;
    
    private UserManager userManager;

    
    private EventManager eventManager;
    
    private EventTableModel eventModel;
    private User userToEdit;

    
    private DeleteEventWorker deleteEventWorker;
    private FindAllEventsWorker findAllEventsWorker;
    private FindEventByUserWorker findEventByUserWorker;
    private UserComboBoxWorker userComboBoxWorker;
    private FindUserByEmailWorker findUserByEmailWorker;
    private DeleteUserWorker deleteUserWorker;
    
    private DefaultComboBoxModel usersComboBoxModel = new DefaultComboBoxModel();
    private UserTableModel userTableModel;

    public UserTableModel getUserTableModel() {
        return userTableModel;
    }

    public JComboBox<String> getjComboBoxUsers() {
        return jComboBoxUsers;
    }
    
    public UserManager getUserManager() {
        return userManager;
    }

    public EventTableModel getEventModel() {
        return eventModel;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    
    
    public EventTableModel getEventTableModel(){
        return eventModel;
    }
    
    private DefaultComboBoxModel getUsersComboBox(){
        return usersComboBoxModel;
    }
    
        /**
     * Creates new form CalendarGUI
     */
    public CalendarGUI() throws SQLException, IOException {
        Localization.setCurrentLocale(Locale.forLanguageTag("en-US"));
        initComponents();
        myInitComponents();
        //Localization.currentLocale = new Locale("")
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground( new Color(187,255,50));
        
        DBUtils.setDataSource();
        DBUtils.createDB();
        DBUtils.insertIntoDB();
        this.ds = DBUtils.getDataSource();
        eventManager = new EventManagerImpl(ds);
        userManager = new UserManagerImpl(ds);
        
        eventModel = (EventTableModel)jTableEvents.getModel();
        findAllEventsWorker = new FindAllEventsWorker();
        findAllEventsWorker.execute();
        
        userTableModel = new UserTableModel();
        userComboBoxWorker = new UserComboBoxWorker();
        usersComboBoxModel = (DefaultComboBoxModel) jComboBoxUsers.getModel();
        userComboBoxWorker.execute();
        
    }
    
    private int[] convert(List<Integer> rows){
        int[] result = new int[rows.size()];
        for(int i = 0; i< rows.size(); i++){
            result[i] = rows.get(i);
        }
        return result;
    }
    
    public class FindUserByEmailWorker extends SwingWorker<User, Integer>{
        
        @Override
        protected User doInBackground() throws Exception {
            String email = (String) jComboBoxUsers.getSelectedItem();
            return userManager.getUserByEmail(email);
        }
        
        @Override
        protected void done(){
             try{
                log.debug("Getting user by email ");
                userToEdit = get();
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindUserByEmailWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindUserByEmailWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindUserByEmailWorker");
            }
        }
    }
    
    private class FindEventByUserWorker extends SwingWorker<List<Event>, Integer>{

        @Override
        protected List<Event> doInBackground() throws Exception {
            User u = userManager.getUserByEmail(jComboBoxUsers.getSelectedItem().toString());
            if(u != null) {
                return eventManager.listUserEvents(u.getId());
            } else {
                return eventManager.listAllEvents();
            }

        }
        
        @Override
        protected void done() {
            try{
                log.debug("Loading user events from db.");
                eventModel.setEvents(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindEventByUserWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindEventByUserWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindEventByUserWorker");
            }
        }
        
    }

    private class FindAllEventsWorker extends SwingWorker<List<Event>, Integer> {

        @Override
        protected List<Event> doInBackground() throws Exception {
            return eventManager.listAllEvents();
        }

        @Override
        protected void done() {
            try{
                log.debug("Loading all events from.");
                eventModel.setEvents(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllEventsWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllEventsWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllEventsWorker");
            }
        }
    }
    
    private class DeleteEventWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selected = jTableEvents.getSelectedRows();
            List<Integer> toDeleteEvents = new ArrayList<>();
            log.debug("Starting deletion of event in Worker " +selected.length);
            if (selected.length >= 0) {
                for (int selectedRow : selected) {
                    Event event = eventModel.getEvent(selectedRow);
                    try {
                        eventManager.deleteEvent(event);
                        toDeleteEvents.add(selectedRow);
                    }catch (Exception ex) {
                        log.error("Cannot delete event." + ex);
                        throw new ServiceFailureException(event.toString());
                    }
                }
                jTableEvents.getSelectionModel().clearSelection();
                return convert(toDeleteEvents);
            }
            return null;
        }
        
        @Override
        protected void done(){
            try{
                int[]indices = get();
                log.debug("removing " + indices.length + " events");
                if(indices!= null && indices.length > 0){
                    eventModel.deleteEvents(indices);
                }
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in  DeleteEventWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in  DeleteEventWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in  DeleteEventWorker");
            }
        }
    }
    
    public class UserComboBoxWorker extends SwingWorker<List<User>, Integer> {

        @Override
        protected List<User> doInBackground() throws Exception {
            return userManager.getAllUsers();
        }

        @Override
        protected void done() {
            try {
                
                List<User> users = get();
                log.debug("Getted all users");
                userTableModel.setUsers(users);
                usersComboBoxModel.removeAllElements();
                log.debug("Removed all elements from usersComboBoxModel");
                usersComboBoxModel.addElement(Localization.getRbTexts().getString("select-user"));
                for (User user : users) {
                    usersComboBoxModel.addElement(user.getEmail());
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UserComboBoxWorker: " + ex.getMessage());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UserComboBoxWorker interrupted: " + ex.getMessage());
                throw new RuntimeException("Operation interrupted.. UserComboBoxWorker");
            }
        }
    }
    
    private class DeleteUserWorker extends SwingWorker<String, Void> {

        @Override
        protected String doInBackground() {
            String selectedEmail = (String) jComboBoxUsers.getSelectedItem();
            log.debug("Starting deletion of user in Worker with email " +selectedEmail);
            User userToDelete = userManager.getUserByEmail(selectedEmail);            
            try {
                userManager.deleteUser(userToDelete);
            }catch (Exception ex) {
                log.error("Cannot delete user with email " + selectedEmail + " " + ex);
                throw new ServiceFailureException(userToDelete.toString());
            }
            return selectedEmail;
        }
        
        @Override
        protected void done(){
            try{
                String deletedEmail = get();
                log.debug("removing user with email " + deletedEmail );
                jComboBoxUsers.removeItem(deletedEmail);
            }catch(ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, Localization.getRbTexts().getString("can-not-delete-user"));
                log.error("Exception was thrown in  DeleteUserWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in  DeleteUserWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in  DeleteUserWorker");
            }
        }
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelSelectUser = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableEvents = new javax.swing.JTable();
        jButtonEditUser = new javax.swing.JButton();
        jButtonDeleteUser = new javax.swing.JButton();
        jButtonCreateUser = new javax.swing.JButton();
        jButtonAddEvent = new javax.swing.JButton();
        jButtonEditEvent = new javax.swing.JButton();
        jButtonDeleteEvent = new javax.swing.JButton();
        jComboBoxUsers = new javax.swing.JComboBox<>();
        jButtonSelectEventsByUser = new javax.swing.JButton();
        jButtonFilter = new javax.swing.JButton();
        jButtonCzech = new javax.swing.JButton();
        jButtonSlovak = new javax.swing.JButton();
        jButtonEnglish = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calendar");
        setBackground(new java.awt.Color(255, 255, 51));
        setName("Calendar"); // NOI18N

        //ResourceBundle resourceB = ResourceBundle.getBundle("texts");
        jLabelSelectUser.setText("select-user");
        jLabelSelectUser.setFocusable(false);

        jTableEvents.setModel(new EventTableModel());
        jTableEvents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableEventsMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTableEvents);
        if (jTableEvents.getColumnModel().getColumnCount() > 0) {
            jTableEvents.getColumnModel().getColumn(1).setResizable(false);
            jTableEvents.getColumnModel().getColumn(2).setResizable(false);
            jTableEvents.getColumnModel().getColumn(3).setResizable(false);
        }

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jButtonEditUser.setText(bundle.getString("EditUser")); // NOI18N
        jButtonEditUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditUserActionPerformed(evt);
            }
        });

        jButtonDeleteUser.setText(bundle.getString("DeleteUser")); // NOI18N
        jButtonDeleteUser.setName(""); // NOI18N
        jButtonDeleteUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonDeleteUserMouseClicked(evt);
            }
        });

        jButtonCreateUser.setText(bundle.getString("CreateUser")); // NOI18N
        jButtonCreateUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateUserActionPerformed(evt);
            }
        });

        jButtonAddEvent.setText(bundle.getString("AddEvent")); // NOI18N
        jButtonAddEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddEventActionPerformed(evt);
            }
        });

        jButtonEditEvent.setEnabled(false);
        jButtonEditEvent.setText(bundle.getString("EditEvent")); // NOI18N
        jButtonEditEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditEventActionPerformed(evt);
            }
        });

        jButtonDeleteEvent.setEnabled(false);
        jButtonDeleteEvent.setText(bundle.getString("DeleteEvent")); // NOI18N
        jButtonDeleteEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteEventActionPerformed(evt);
            }
        });

        jComboBoxUsers.setModel(getUsersComboBox());
        userComboBoxWorker = new UserComboBoxWorker();
        userComboBoxWorker.execute();

        jButtonSelectEventsByUser.setText(bundle.getString("SelectEvents")); // NOI18N
        jButtonSelectEventsByUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonSelectEventsByUserMouseClicked(evt);
            }
        });

        jButtonFilter.setText(bundle.getString("Filter")); // NOI18N
        jButtonFilter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonFilterMouseClicked(evt);
            }
        });
        jButtonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilterActionPerformed(evt);
            }
        });

        jButtonCzech.setText(bundle.getString("czech")); // NOI18N
        jButtonCzech.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonCzechMouseClicked(evt);
            }
        });

        jButtonSlovak.setText(bundle.getString("slovak")); // NOI18N
        jButtonSlovak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonSlovakMouseClicked(evt);
            }
        });

        jButtonEnglish.setText(bundle.getString("english")); // NOI18N
        jButtonEnglish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonEnglishMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonFilter)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonAddEvent)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonEditEvent)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonDeleteEvent))
                            .addComponent(jScrollPane2)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabelSelectUser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSelectEventsByUser)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonEditUser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonCreateUser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonDeleteUser))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonCzech)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonSlovak)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEnglish)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCzech)
                    .addComponent(jButtonSlovak)
                    .addComponent(jButtonEnglish))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelSelectUser, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSelectEventsByUser))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonEditUser)
                        .addComponent(jButtonDeleteUser)
                        .addComponent(jButtonCreateUser)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAddEvent)
                    .addComponent(jButtonEditEvent)
                    .addComponent(jButtonDeleteEvent)
                    .addComponent(jButtonFilter))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("frame0");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void myInitComponents() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts", Localization.getCurrentLocale()); // NOI18N
        jButtonEditUser.setText(bundle.getString("EditUser")); // NOI18N
        jButtonDeleteUser.setText(bundle.getString("DeleteUser")); // NOI18N
        jButtonCreateUser.setText(bundle.getString("CreateUser")); // NOI18N
        jButtonAddEvent.setText(bundle.getString("AddEvent")); // NOI18N
        jButtonEditEvent.setText(bundle.getString("EditEvent")); // NOI18N
        jButtonDeleteEvent.setText(bundle.getString("DeleteEvent")); // NOI18N
        jButtonSelectEventsByUser.setText(bundle.getString("SelectEvents")); // NOI18N
        jButtonFilter.setText(bundle.getString("Filter")); // NOI18N
        jButtonCzech.setText(bundle.getString("czech")); // NOI18N
        jButtonSlovak.setText(bundle.getString("slovak")); // NOI18N
        jButtonEnglish.setText(bundle.getString("english")); // NOI18N
     }
    
    private void jButtonDeleteUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDeleteUserMouseClicked
       if(jComboBoxUsers.getSelectedItem() != Localization.getRbTexts().getString("select-user"))
       {
        jButtonEditEvent.setEnabled(false);
        jButtonDeleteUser.setEnabled(false);
        deleteUserWorker = new DeleteUserWorker();
        deleteUserWorker.execute();
        jButtonDeleteUser.setEnabled(true);
       }
    }//GEN-LAST:event_jButtonDeleteUserMouseClicked

    private void jButtonSelectEventsByUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSelectEventsByUserMouseClicked
        jButtonEditEvent.setEnabled(false);
        jButtonSelectEventsByUser.setEnabled(false);
        findEventByUserWorker = new FindEventByUserWorker();
        findEventByUserWorker.execute();
        jButtonSelectEventsByUser.setEnabled(true);
    }//GEN-LAST:event_jButtonSelectEventsByUserMouseClicked

    private void jButtonDeleteEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteEventActionPerformed
        jButtonEditEvent.setEnabled(false);
        jButtonDeleteEvent.setEnabled(false);
        deleteEventWorker = new DeleteEventWorker();
        deleteEventWorker.execute();
    }//GEN-LAST:event_jButtonDeleteEventActionPerformed

    private void jButtonAddEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddEventActionPerformed
       java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EventForm(CalendarGUI.this, null, -1, "add").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButtonAddEventActionPerformed

    private void jButtonCreateUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateUserActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserForm(CalendarGUI.this, null, new String(), "add").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButtonCreateUserActionPerformed

    private void jButtonEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditUserActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String selectedEmail = (String) jComboBoxUsers.getSelectedItem();
                log.debug("jButtonEditUserActionPerformed email = " + selectedEmail);
                if(selectedEmail != Localization.getRbTexts().getString("select-user"))
                {
                    //log.debug("jButtonEditUserActionPerformed email from Worker = " + userToEdit.getEmail());
                new UserForm(CalendarGUI.this, null, selectedEmail, "update");
                }
                
            }
        });
    }//GEN-LAST:event_jButtonEditUserActionPerformed

    private void jButtonEditEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditEventActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = jTableEvents.getSelectedRow();
                if(selectedRow >= 0){
                    new EventForm(CalendarGUI.this, eventModel.getEvent(selectedRow), selectedRow, "update");
                }                
            }
        });
    }//GEN-LAST:event_jButtonEditEventActionPerformed

    private void jTableEventsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEventsMouseReleased
        if(jTableEvents.getSelectedRowCount()!= 1){
            jButtonEditEvent.setEnabled(false);
        }else{
            jButtonEditEvent.setEnabled(true);
        }
        if(jTableEvents.getSelectedRowCount()> 0){
            jButtonDeleteEvent.setEnabled(true);
        }
    
    }//GEN-LAST:event_jTableEventsMouseReleased

    private void jButtonFilterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFilterMouseClicked
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                String selectedEmail = (String) jComboBoxUsers.getSelectedItem();
                log.debug("jButtonFilterMouseClicked email = " + selectedEmail);
                new FilterForm(CalendarGUI.this, selectedEmail);
            }
        });
    }//GEN-LAST:event_jButtonFilterMouseClicked

    private void jButtonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonFilterActionPerformed

    private void jButtonEnglishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonEnglishMouseClicked
        Localization.setCurrentLocale(Locale.US);    
        myInitComponents();
        
    }//GEN-LAST:event_jButtonEnglishMouseClicked

    private void jButtonSlovakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSlovakMouseClicked
        // TODO add your handling code here:
        Localization.setCurrentLocale(Locale.forLanguageTag("sk-SK"));
        myInitComponents();

    }//GEN-LAST:event_jButtonSlovakMouseClicked

    private void jButtonCzechMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCzechMouseClicked
        Localization.setCurrentLocale(Locale.forLanguageTag("cs-CZ"));
        myInitComponents();
    }//GEN-LAST:event_jButtonCzechMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CalendarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CalendarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CalendarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CalendarGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new CalendarGUI().setVisible(true);
                } catch (SQLException ex) {
                    log.error("Could not run CalendarGUI SQLException...CalendarGUI main. " + ex.getMessage());
                } catch (IOException ex) {
                    log.error("Could not run CalendarGUI IOException ...CalendarGUI main. " + ex.getMessage());
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddEvent;
    private javax.swing.JButton jButtonCreateUser;
    private javax.swing.JButton jButtonCzech;
    private javax.swing.JButton jButtonDeleteEvent;
    private javax.swing.JButton jButtonDeleteUser;
    private javax.swing.JButton jButtonEditEvent;
    private javax.swing.JButton jButtonEditUser;
    private javax.swing.JButton jButtonEnglish;
    private javax.swing.JButton jButtonFilter;
    private javax.swing.JButton jButtonSelectEventsByUser;
    private javax.swing.JButton jButtonSlovak;
    private javax.swing.JComboBox<String> jComboBoxUsers;
    private javax.swing.JLabel jLabelSelectUser;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableEvents;
    // End of variables declaration//GEN-END:variables
}
