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
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.sql.DataSource;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingWorker;
import org.slf4j.*;

/**
 *
 * @author veronika
 */
public class CalendarGUI extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(CalendarGUI.class);
    private DataSource ds;
    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    
    private UserManager userManager;
    private EventManager eventManager;
    
    private EventTableModel eventModel;
    
    private FindAllEventsWorker findAllEventsWorker;
    private UserComboBoxWorker userComboBoxWorker;
    
    private DefaultComboBoxModel usersComboBoxModel = new DefaultComboBoxModel();
    
    
    private DefaultComboBoxModel getUsersComboBox(){
        return usersComboBoxModel;
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
    
    //TODO
//    private class DeleteEventWorker extends SwingWorker<int[], Void> {
//
//        @Override
//        protected int[] doInBackground() {
//            int[] selected = JTableEvents.getSelectedRows();
//            List<Event> toDeleteEvents = new ArrayList<>();
//            if (selected.length >= 0) {
//                for (int selectedRow : selected) {
//                    Event event = eventModel.getEvent(selectedRow);
//                    try {
//                        eventManager.deleteEvent(event);
//                        //toDeleteEvents.add(selectedRow);
//                    }catch (Exception ex) {
//                        //log.error("Cannot delete property." + ex);
//                        throw new ServiceFailureException(event.toString());
//                        //JOptionPane.showMessageDialog(null, rb.getString("cannot-delete-property"));
//                    }
//                }
//                //return convert(toDeleteRows);
//            }
//            return null;
//        }
    
    
    public class UserComboBoxWorker extends SwingWorker<List<User>, Integer> {

        @Override
        protected List<User> doInBackground() throws Exception {
            return userManager.getAllUsers();
        }

        @Override
        protected void done() {
            try {
                List<User> users = get();
                usersComboBoxModel.removeAllElements();
                for (User user : users) {
                    usersComboBoxModel.addElement(user.getEmail());
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UserComboBoxWorker: " + ex.getMessage());
            } catch (InterruptedException ex) {
                log.error("doInBackground of OUserComboBoxWorker interrupted: " + ex.getMessage());
                throw new RuntimeException("Operation interrupted.. UserComboBoxWorker");
            }
        }
    }
    
    /**
     * Creates new form CalendarGUI
     */
    public CalendarGUI() throws SQLException, IOException {
        
        initComponents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setBackground( new Color(187,255,50));
        DBUtils.setDataSource();
        DBUtils.createDB();
        DBUtils.insertIntoDB();
        this.ds = DBUtils.getDataSource();
        
        eventManager = new EventManagerImpl(ds);
        userManager = new UserManagerImpl(ds);
        log.debug("data source is " + ds.equals(null));
        
        eventModel = (EventTableModel)JTableEvents.getModel();
        findAllEventsWorker = new FindAllEventsWorker();
        findAllEventsWorker.execute();
        
        usersComboBoxModel = (DefaultComboBoxModel) jComboBoxUsers.getModel();
        userComboBoxWorker = new UserComboBoxWorker();
        userComboBoxWorker.execute();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectUserlabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        JTableEvents = new javax.swing.JTable();
        editUserButton = new javax.swing.JButton();
        deleteUserButton = new javax.swing.JButton();
        createUserButton = new javax.swing.JButton();
        addEventButton = new javax.swing.JButton();
        editEventButton = new javax.swing.JButton();
        deleteEventButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jComboBoxUsers = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Calendar");
        setBackground(new java.awt.Color(255, 255, 51));
        setName("Calendar"); // NOI18N

        //ResourceBundle resourceB = ResourceBundle.getBundle("texts");
        selectUserlabel.setText("select-user");
        selectUserlabel.setFocusable(false);

        JTableEvents.setModel(new EventTableModel());
        jScrollPane2.setViewportView(JTableEvents);
        if (JTableEvents.getColumnModel().getColumnCount() > 0) {
            JTableEvents.getColumnModel().getColumn(1).setResizable(false);
            JTableEvents.getColumnModel().getColumn(2).setResizable(false);
            JTableEvents.getColumnModel().getColumn(3).setResizable(false);
        }

        editUserButton.setText("Edit user");

        deleteUserButton.setText("Delete user");
        deleteUserButton.setName(""); // NOI18N
        deleteUserButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteUserButtonMouseClicked(evt);
            }
        });

        createUserButton.setText("Create user");

        addEventButton.setText("Add event");

        editEventButton.setText("Edit event");

        deleteEventButton.setText("Delete event");

        jButton1.setText("List all events");
        jButton1.setActionCommand("jButton");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        jComboBoxUsers.setModel(getUsersComboBox());
        userComboBoxWorker = new UserComboBoxWorker();
        userComboBoxWorker.execute();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton1)
                        .addGap(62, 62, 62)
                        .addComponent(addEventButton)
                        .addGap(18, 18, 18)
                        .addComponent(editEventButton)
                        .addGap(18, 18, 18)
                        .addComponent(deleteEventButton))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(selectUserlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBoxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27)
                            .addComponent(editUserButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createUserButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(deleteUserButton))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectUserlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(editUserButton)
                        .addComponent(deleteUserButton)
                        .addComponent(createUserButton)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEventButton)
                    .addComponent(editEventButton)
                    .addComponent(deleteEventButton)
                    .addComponent(jButton1))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        jButton1.getAccessibleContext().setAccessibleName("jButtonListEvents");

        getAccessibleContext().setAccessibleName("frame0");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteUserButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteUserButtonMouseClicked
        
        //usersList.;
        //userManager.deleteUser(userManager.getUser());
    }//GEN-LAST:event_deleteUserButtonMouseClicked

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        jButton1.setEnabled(false);
        deleteEventButton.setEnabled(true);
        findAllEventsWorker = new FindAllEventsWorker();
        findAllEventsWorker.execute();
    }//GEN-LAST:event_jButton1MouseReleased

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
    private javax.swing.JTable JTableEvents;
    private javax.swing.JButton addEventButton;
    private javax.swing.JButton createUserButton;
    private javax.swing.JButton deleteEventButton;
    private javax.swing.JButton deleteUserButton;
    private javax.swing.JButton editEventButton;
    private javax.swing.JButton editUserButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBoxUsers;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel selectUserlabel;
    // End of variables declaration//GEN-END:variables
}
