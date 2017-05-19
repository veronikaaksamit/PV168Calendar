/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.User;
import cz.muni.fi.pv168.frontend.CalendarGUI.FindUserByEmailWorker;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dadka
 */
public class UserForm extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(UserForm.class);
    private String mode;
    private long userId;
    private CalendarGUI context;
    private User user;
    private String selectedEmail;
    private String action;
    
    private AddUserWorker addUserWorker;
    private UpdateUserWorker updateUserWorker;
    
    /**
     * Creates new form UserForm
     */
    public UserForm() {
        initComponents();
    }
    
 

    /**
     * Creates new form UserForm 
     */
    public UserForm(CalendarGUI context, User user, String selectedEmail, String act) {
        initComponents();
        myInitComponents();
        context.setEnabled(false);
        this.context = context;
        this.selectedEmail = selectedEmail;
        this.action = act;
        
        
        if(selectedEmail != null && !selectedEmail.isEmpty()){   
            log.debug("UserForm contructor getting user by email " + selectedEmail);
            this.user = context.getUserManager().getUserByEmail(selectedEmail);
            log.debug("UserForm got user by email");
        }
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        if (this.user != null) {
            jTextFieldName.setText(this.user.getFullName());
            jTextFieldEmail.setText(this.user.getEmail());
        }
        this.setVisible(true);
    }
    
    public class AddUserWorker extends SwingWorker<User, Integer>{
        
        @Override
        protected User doInBackground() throws Exception {
            user = getUserFromForm();
            context.getUserManager().createUser(user);
            return user;
        }
        
        @Override
        protected void done(){
             try{
                 User user = get();
                 context.getjComboBoxUsers().addItem(user.getEmail());
                 context.getUserTableModel().addUser(user);
                 UserForm.this.dispose();
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in AddUserWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in AddUserWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in AddUserWorker");
            }
        }
    }
    
    public class UpdateUserWorker extends SwingWorker<User, Integer>{
        
        @Override
        protected User doInBackground() throws Exception {
            user = getUserFromForm();
            context.getUserManager().updateUser(user);
            return user;
        }
        
        @Override
        protected void done(){
             try{
                 User user = get();
                 context.getjComboBoxUsers().removeItem(context.getjComboBoxUsers().getSelectedItem());
                 context.getjComboBoxUsers().addItem(user.getEmail());
                 context.getUserTableModel().updateUser(user);
                 UserForm.this.dispose();
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in AddUserWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in AddUserWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in AddUserWorker");
            }
        }
    }
     
     private User getUserFromForm(){
         String name = jTextFieldName.getText();
         if(name == null || name.length() < 3){
             warningMessageBox(Localization.getRbTexts().getString("name-null"));
             return null;
         }
         
         String email= jTextFieldEmail.getText();
         if(email == null || email.length() < 3 || !email.contains("@") ){
             warningMessageBox(Localization.getRbTexts().getString("email-not-valid"));   
             return null;
         }
         
         if(this.user == null){
             this.user = new User();
         }
         
         this.user.setEmail(email);
         this.user.setFullName(name);
         
         return this.user;
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
        jLabelUser = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jLabelEmail = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldEmail = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

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

        jLabelUser.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jLabelUser.setText(bundle.getString("User")); // NOI18N

        jLabelName.setText(bundle.getString("Name")); // NOI18N

        jLabelEmail.setText(bundle.getString("Description")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(202, Short.MAX_VALUE)
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelName)
                    .addComponent(jLabelUser)
                    .addComponent(jLabelEmail))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldName)
                    .addComponent(jTextFieldEmail))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jLabelUser)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelName)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelEmail)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOK)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void myInitComponents() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts", Localization.getCurrentLocale()); // NOI18N
        jLabelUser.setText(bundle.getString("User")); // NOI18N
        jLabelName.setText(bundle.getString("Name")); // NOI18N
        jLabelEmail.setText(bundle.getString("Description")); // NOI18N
    }
    
    private void jButtonOKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonOKMouseClicked
        if(this.action== "add"){
            addUserWorker = new AddUserWorker();
            addUserWorker.execute();
        }
        if(this.action == "update"){
            updateUserWorker = new UpdateUserWorker();
            updateUserWorker.execute();
        }
        context.setEnabled(true);
    }//GEN-LAST:event_jButtonOKMouseClicked

    private void jButtonCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCancelMouseClicked
        this.dispose();
    }//GEN-LAST:event_jButtonCancelMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        context.setEnabled(true);
    }//GEN-LAST:event_formWindowClosed

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
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabelEmail;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldName;
    // End of variables declaration//GEN-END:variables
}
