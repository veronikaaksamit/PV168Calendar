package cz.muni.fi.pv168;

import cz.muni.fi.pv168.common.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xaksamit on 10.3.17.
 */
public class UserManagerImpl implements UserManager {

    private final DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(UserManagerImpl.class);

    public UserManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(user.getId() != null) throw new IllegalArgumentException("creating user with existing id");
        if(user.getEmail() == null) throw new IllegalArgumentException("user email is null");
        if(user.getEmail().trim().isEmpty()) throw new IllegalArgumentException("user email is empty");

        log.debug("Starting creation of user");
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO USERS (FULLNAME, EMAIL) VALUES (?,?)",
                                                             PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,user.getFullName());
                ps.setString(2,user.getEmail().toLowerCase());
                log.debug("Starting creation of user...starting update");
                ps.executeUpdate();

                try(ResultSet keys = ps.getGeneratedKeys() ) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        user.setId(id);
                    }
                }
            }
        } catch(SQLException ex) {
            if(ex.getErrorCode() == 30000) {
                log.error("user email already exists " + ex.getMessage());
                throw new IllegalArgumentException("user email already exists");
            }
            ex.printStackTrace();
        }
        log.debug("user was created");
    }

    @Override
    public void updateUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(user.getId() == null) throw new IllegalArgumentException("updating user with null id");
        if(user.getEmail() == null) throw new IllegalArgumentException("user email is null");
        if(user.getEmail().trim().isEmpty()) throw new IllegalArgumentException("user email is empty");

        log.debug("Starting update of user");
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET FULLNAME=?, EMAIL=? WHERE ID = ?")) {
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getEmail().toLowerCase());
                ps.setLong(3, user.getId());
                int n = ps.executeUpdate();
                if(n == 0) {
                    log.error("Could not update user with nonexisting id ");
                    throw new IllegalArgumentException("updating user with nonexisting id");
                }
                if(n != 1) {
                    log.error("Could not update user with id " + user.getId());
                    throw new ServiceFailureException("not updated user with id " + user.getId());
                }
            }
        } catch (SQLException ex) {
            log.error("Could not update user ...SQLException" + ex.getMessage());
            ex.printStackTrace();
        }

    }

    @Override
    public void deleteUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(user.getId() == null) throw new IllegalArgumentException("user id is null");

        log.debug("Starting deletion of user with id " + user.getId());
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM USERS WHERE ID = ?")) {
                ps.setLong(1, user.getId());
                int n = ps.executeUpdate();
                if(n == 0) {
                    log.error("Deletion of user with nonexisting id " + user.getId());
                    throw new IllegalArgumentException("deleting user with nonexisting id");
                }
                if(n != 1) {
                    log.error("NOT deleted user with id " + user.getId());
                    throw new ServiceFailureException("not deleted user with id " + user.getId());
                }
            }
        } catch (SQLException ex) {
            log.error("Could not delete user with id " + user.getId() + "SQLException " + ex.getMessage() );
            ex.printStackTrace();
        }
    }

    @Override
    public User getUser(Long id) {
        log.debug("Getting user with id " + id);
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE ID = ?")) {
                ps.setLong(1, id);
                return getUserFromQuery(ps);
            }
        } catch (SQLException ex) {
            log.error("Could not get user with id " + id + "SqlException " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        log.debug("Getting user with email " + email);
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE LOWER(EMAIL) = LOWER(?)")) {
                ps.setString(1, email);
                return getUserFromQuery(ps);

            }
        } catch (SQLException ex) {
            log.error("Could not get user with email" + email + "SqlException " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getUserByName(String name) {
        log.debug("Getting user with name " + name);
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE LOWER(FULLNAME) = LOWER(?)")) {
                ps.setString(1, name);
                return getUserListFromQuery(ps);
            }
        } catch (SQLException ex) {
            log.error("Could not get user with name" + name + "SqlException " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Getting all users");
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS")) {
                return getUserListFromQuery(ps);
            }
        } catch (SQLException ex) {
            log.error("Could not get all users ...SqlException " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private User getUserFromQuery(PreparedStatement ps) throws SQLException{
        User user = new User();
        try(ResultSet rs = ps.executeQuery()) {
            if(rs.next()) {
                user.setId(rs.getLong(1));
                user.setFullName(rs.getString(2));
                user.setEmail(rs.getString(3));
                return user;
            } else {
                return null;
            }
        }
    }

    private List<User> getUserListFromQuery(PreparedStatement ps) throws SQLException{
       List<User> userList = new ArrayList<>();
        try(ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getLong(1));
                user.setFullName(rs.getString(2));
                user.setEmail(rs.getString(3));
                userList.add(user);
            }
            return userList;
        }
    }
}
