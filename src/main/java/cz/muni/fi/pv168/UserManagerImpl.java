package cz.muni.fi.pv168;

import javax.sql.DataSource;
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

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public User getUser(Long id) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<User> getUserByName(String name) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
