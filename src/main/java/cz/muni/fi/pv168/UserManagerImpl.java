package cz.muni.fi.pv168;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * Created by xaksamit on 10.3.17.
 */
public class UserManagerImpl implements UserManager {

    private final DataSource dataSource;

    public UserManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(user.getId() != null) throw new IllegalArgumentException("creating user with existing id");
        if(user.getEmail() == null) throw new IllegalArgumentException("user email is null");
        if(user.getEmail().trim().isEmpty()) throw new IllegalArgumentException("user email is empty");

        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO USERS (FULLNAME, EMAIL) VALUES (?,?)",
                                                             PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,user.getFullName());
                ps.setString(2,user.getEmail().toLowerCase());
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
                throw new IllegalArgumentException("user email already exists");
            }
            ex.printStackTrace();
        }
    }

    @Override
    public void updateUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(user.getId() == null) throw new IllegalArgumentException("updating user with null id");
        if(user.getEmail() == null) throw new IllegalArgumentException("user email is null");
        if(user.getEmail().trim().isEmpty()) throw new IllegalArgumentException("user email is empty");

        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET FULLNAME=?, EMAIL=? WHERE ID = ?")) {
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getEmail().toLowerCase());
                ps.setLong(3, user.getId());
                int n = ps.executeUpdate();
                if(n == 0) {
                    throw new IllegalArgumentException("updating user with nonexisting id");
                }
                if(n != 1) {
                    //TODO throw exception
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void deleteUser(User user) {
        if(user == null) throw new IllegalArgumentException("user is null");
        if(user.getId() == null) throw new IllegalArgumentException("user id is null");

        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("DELETE FROM USERS WHERE ID = ?")) {
                ps.setLong(1, user.getId());
                int n = ps.executeUpdate();
                if(n == 0) {
                    throw new IllegalArgumentException("deleting user with nonexisting id");
                }
                if(n != 1) {
                    //TODO throw exeption
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public User getUser(Long id) {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE ID = ?")) {
                ps.setLong(1, id);
                return getUserFromQuery(ps);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE LOWER(EMAIL) = LOWER(?)")) {
                ps.setString(1, email);
                return getUserFromQuery(ps);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getUserByName(String name) {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE LOWER(FULLNAME) = LOWER(?)")) {
                ps.setString(1, name);
                return getUserListFromQuery(ps);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS")) {
                return getUserListFromQuery(ps);
            }
        } catch (SQLException ex) {
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
