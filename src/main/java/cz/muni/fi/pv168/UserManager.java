package cz.muni.fi.pv168;

import java.util.List;

/**
 * Created by xaksamit on 10.3.17.
 */
public interface UserManager {

    void createUser(User user);
    void updateUser(User user);
    void deleteUser(User user);

    User getUserById(Long id);
    User getUserByEmail(String email);
    List<User> getUserByName(String name);
}
