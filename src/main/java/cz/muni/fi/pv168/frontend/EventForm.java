/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.Category;
import cz.muni.fi.pv168.Event;
import cz.muni.fi.pv168.User;
import cz.muni.fi.pv168.common.DateTimePicker;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import org.joda.time.DateTime;
import org.jdesktop.swingx.JXDatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dadka
 */
public class EventForm extends javax.swing.JFrame {

    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    private final static Logger log = LoggerFactory.getLogger(EventForm.class);
    private String mode;
    private long eventId;
    private CalendarGUI context;
    private Event event;
    private int rowIndex;
    private String action;
    private EventTableModel eventTableModel;
    private DefaultComboBoxModel categoryComboBoxModel = new DefaultComboBoxModel<>(Category.values());

    private JXDatePicker startDateDtp;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JXDatePicker endDateDtp;

    private AddEventWorker addEventWorker;
    private UpdateEventWorker updateEventWorker;

    /**
     * Creates new form EventForm
     */
    public EventForm(CalendarGUI context, Event event, int rowIndex, String action) {
        initComponents();

        this.context = context;
        this.event = event;
        this.rowIndex = rowIndex;
        this.action = action;
        this.eventTableModel = context.getEventTableModel();
        jButtonCancel.setText(rb.getString("cancel"));
        jComboBoxUsersInEventForm.setModel(context.getjComboBoxUsers().getModel());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Date startDate = new Date();
        startDateDtp = new JXDatePicker();
        Date endDate = new Date();
        endDateDtp = new JXDatePicker();

        if (event != null) {
            Long id = event.getUserId();

            User u = context.getUserTableModel().getUserById(id);
            jComboBoxUsersInEventForm.setSelectedItem(u.getEmail());
            jTextFieldFullname.setText(event.getEventName());
            jComboBoxCategory.setSelectedItem(event.getCategory());
            jTextFieldDescription.setText(event.getDescription());

            startDate = Date.from(event.getStartDate().atZone(ZoneId.systemDefault()).toInstant());
            startDateDtp.setDate(startDate);

            SpinnerDateModel startDateSm = new SpinnerDateModel(startDate, null, null, Calendar.MINUTE);
            startDateSpinner = new JSpinner(startDateSm);
            JSpinner.DateEditor startDateDe = new JSpinner.DateEditor(startDateSpinner, "HH:mm");
            startDateDe.getTextField().setEditable(false);
            startDateSpinner.setEditor(startDateDe);
            startDateSpinner.setBounds(210, 0, 100, 30);

            //endDateSpinner;
            //endDateDtp;
            startDate = Date.from(event.getStartDate().atZone(ZoneId.systemDefault()).toInstant());

            startDateDtp = new JXDatePicker();
            startDateDtp.setDate(startDate);
            startDateDtp.setBounds(0, 0, 200, 30);
            jPanelStartDate.add(startDateDtp);

            startDateSpinner = new JSpinner(startDateSm);

            startDateSpinner.setEditor(startDateDe);
            startDateSpinner.setBounds(210, 0, 100, 30);
            jPanelStartDate.add(startDateSpinner);

//            end time
            /////////////////////////////////////////////////////////
            endDate = Date.from(event.getEndDate().atZone(ZoneId.systemDefault()).toInstant());
            endDateDtp.setDate(endDate);

            SpinnerDateModel endDateSm = new SpinnerDateModel(endDate, null, null, Calendar.MINUTE);
            endDateSpinner = new JSpinner(endDateSm);
            JSpinner.DateEditor endDateDe = new JSpinner.DateEditor(endDateSpinner, "HH:mm");
            endDateDe.getTextField().setEditable(false);
            endDateSpinner.setEditor(endDateDe);
            endDateSpinner.setBounds(210, 0, 100, 30);
            jPanelEndDate.add(endDateSpinner);

            endDateDtp = new JXDatePicker();
            endDateDtp.setDate(endDate);
            endDateDtp.setBounds(0, 0, 200, 30);
            jPanelEndDate.add(endDateDtp);

        } else {
            startDate = new Date();
            startDateDtp = new JXDatePicker();
            startDateDtp.setDate(startDate);
            startDateDtp.setBounds(0, 0, 200, 30);
            jPanelStartDate.add(startDateDtp);

            SpinnerDateModel startDateSm = new SpinnerDateModel(startDate, null, null, Calendar.MINUTE);
            startDateSpinner = new JSpinner(startDateSm);
            JSpinner.DateEditor startDateDe = new JSpinner.DateEditor(startDateSpinner, "HH:mm");
            startDateDe.getTextField().setEditable(false);
            startDateSpinner.setEditor(startDateDe);
            startDateSpinner.setBounds(210, 0, 100, 30);
            jPanelStartDate.add(startDateSpinner);

            endDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.HOUR_OF_DAY, 1);
            endDate = cal.getTime();

            endDateDtp = new JXDatePicker();
            endDateDtp.setDate(endDate);
            endDateDtp.setBounds(0, 0, 200, 30);
            jPanelEndDate.add(endDateDtp);

            SpinnerDateModel endDateSm = new SpinnerDateModel(endDate, null, null, Calendar.MINUTE);
            endDateSpinner = new JSpinner(endDateSm);
            JSpinner.DateEditor endDateDe = new JSpinner.DateEditor(endDateSpinner, "HH:mm");
            endDateDe.getTextField().setEditable(false);
            endDateSpinner.setEditor(endDateDe);
            endDateSpinner.setBounds(210, 0, 100, 30);
            jPanelEndDate.add(endDateSpinner);

        }
        this.setVisible(true);
    }

    /**
     * Creates new form EventForm
     */
    public EventForm() {
        initComponents();
    }

    public EventForm(String mode, long eventId) {
        super();
        this.mode = mode;
        this.eventId = eventId;
    }

    public class UpdateEventWorker extends SwingWorker<Event, Integer> {

        @Override
        protected Event doInBackground() throws Exception {
            event = getEventFromForm();
            context.getEventManager().updateEvent(event);
            return event;
        }

        @Override
        protected void done() {
            try {
                Event event = get();
                context.getEventTableModel().updateEvent(event);
                EventForm.this.dispose();
            } catch (ExecutionException ex) {
                log.error("Exception was thrown in UpdateEventWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in UpdateEventWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in UpdateEventWorker");
            }
        }
    }

    public class AddEventWorker extends SwingWorker<Event, Integer> {

        @Override
        protected Event doInBackground() throws Exception {
            event = getEventFromForm();
            context.getEventManager().createEvent(event);
            return event;
        }

        @Override
        protected void done() {
            try {
                Event event = get();
                context.getEventModel().addEvent(event);
                log.debug("AddEventWorker added event =");
                EventForm.this.dispose();
            } catch (ExecutionException ex) {
                log.error("Exception was thrown in AddEventWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in AddEventWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in AddEventWorker");
            }
        }
    }

    private Event getEventFromForm() {

        String eventName = jTextFieldFullname.getText();
        if (eventName == null || eventName.isEmpty()) {
            warningMessageBox(rb.getString("name-null"));
            return null;
        }

        DateTime startDate = new DateTime(startDateDtp.getDate());
        DateTime startDateTime = new DateTime(startDateSpinner.getValue());
        LocalDateTime startTime = LocalDateTime.of(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth(), startDateTime.getHourOfDay(), startDateTime.getMinuteOfHour(), startDateTime.getSecondOfMinute());
        log.debug("SpinnerDateModel: " + startDateTime);

        DateTime endDate = new DateTime(endDateDtp.getDate());
        DateTime endDateTime = new DateTime(endDateSpinner.getValue());
        LocalDateTime endTime = LocalDateTime.of(endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth(), endDateTime.getHourOfDay(), endDateTime.getMinuteOfHour(), endDateTime.getSecondOfMinute());
        log.debug("SpinnerDateModel: " + endDateTime);

        String description = jTextFieldDescription.getText();
        if (description == null) {
            description = "";
        }

        User user = context.getUserTableModel().getUserByEmail((String) jComboBoxUsersInEventForm.getSelectedItem());
        event.setEventName(eventName);
        event.setStartDate(startTime);
        event.setEndDate(endTime);
        event.setDescription(description);
        event.setUserId(user.getId());

        log.debug("Category is" + (Category) jComboBoxCategory.getSelectedItem());
        event.setCategory((Category) jComboBoxCategory.getSelectedItem());

        return event;
    }

    private void warningMessageBox(String message) {
        log.debug("Showing warning message box with message: " + message);
        JOptionPane.showMessageDialog(rootPane, message, null, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jComboBoxCategory = new javax.swing.JComboBox<>();
        jLabelStartDate = new javax.swing.JLabel();
        jLabelEndDate = new javax.swing.JLabel();
        eventLabel = new javax.swing.JLabel();
        jLabelFullname = new javax.swing.JLabel();
        jLabelDescription = new javax.swing.JLabel();
        jTextFieldFullname = new javax.swing.JTextField();
        jTextFieldDescription = new javax.swing.JTextField();
        jLabelCategory = new javax.swing.JLabel();
        jPanelStartDate = new javax.swing.JPanel();
        jPanelEndDate = new javax.swing.JPanel();
        jComboBoxUsersInEventForm = new javax.swing.JComboBox<>();
        jLabelUserForEvent = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonOK.setText("Ok");
        jButtonOK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonOKMouseClicked(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonCancelMouseClicked(evt);
            }
        });

        jComboBoxCategory.setModel(categoryComboBoxModel);

        jLabelStartDate.setText("Start Date:");

        jLabelEndDate.setText("End Date:");

        eventLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        eventLabel.setText("Event");

        jLabelFullname.setText("Name:");

        jLabelDescription.setText("Description:");

        jLabelCategory.setText("Category:");

        jPanelStartDate.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanelStartDateLayout = new javax.swing.GroupLayout(jPanelStartDate);
        jPanelStartDate.setLayout(jPanelStartDateLayout);
        jPanelStartDateLayout.setHorizontalGroup(
            jPanelStartDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );
        jPanelStartDateLayout.setVerticalGroup(
            jPanelStartDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanelEndDate.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanelEndDateLayout = new javax.swing.GroupLayout(jPanelEndDate);
        jPanelEndDate.setLayout(jPanelEndDateLayout);
        jPanelEndDateLayout.setHorizontalGroup(
            jPanelEndDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );
        jPanelEndDateLayout.setVerticalGroup(
            jPanelEndDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jLabelUserForEvent.setText("User:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFullname)
                    .addComponent(jLabelDescription)
                    .addComponent(jLabelCategory)
                    .addComponent(jLabelUserForEvent)
                    .addComponent(jLabelStartDate)
                    .addComponent(jLabelEndDate)
                    .addComponent(eventLabel))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldFullname, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldDescription, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxCategory, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanelEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBoxUsersInEventForm, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(eventLabel)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxUsersInEventForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelUserForEvent))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFullname)
                    .addComponent(jTextFieldFullname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDescription)
                    .addComponent(jTextFieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCategory)
                    .addComponent(jComboBoxCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelStartDate)
                    .addComponent(jPanelStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelEndDate)
                    .addComponent(jPanelEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCancelMouseClicked
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButtonCancelMouseClicked

    private void jButtonOKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOKMouseClicked
        if (this.action == "add") {
            addEventWorker = new AddEventWorker();
            addEventWorker.execute();
        }
        if (this.action == "update") {
            updateEventWorker = new UpdateEventWorker();
            updateEventWorker.execute();
        }
        this.dispose();
    }//GEN-LAST:event_jButtonOKMouseClicked

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
            java.util.logging.Logger.getLogger(EventForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EventForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EventForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EventForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EventForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel eventLabel;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox<String> jComboBoxCategory;
    private javax.swing.JComboBox<String> jComboBoxUsersInEventForm;
    private javax.swing.JLabel jLabelCategory;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelEndDate;
    private javax.swing.JLabel jLabelFullname;
    private javax.swing.JLabel jLabelStartDate;
    private javax.swing.JLabel jLabelUserForEvent;
    private javax.swing.JPanel jPanelEndDate;
    private javax.swing.JPanel jPanelStartDate;
    private javax.swing.JTextField jTextFieldDescription;
    private javax.swing.JTextField jTextFieldFullname;
    // End of variables declaration//GEN-END:variables
}
