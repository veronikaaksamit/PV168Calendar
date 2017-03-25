package cz.muni.fi.pv168;

import java.util.List;

/**
 * Created by xaksamit on 10.3.17.
 */
public interface UserManager {

    /**
     * Creates new User
     * @exception IllegalArgumentException
     * @param user to be created
     */
    void createUser(User user);

    /**
     * Updates existing User
     * @exception IllegalArgumentException
     * @param user to be updated to
     */
    void updateUser(User user);

    /**
     * Deletes existing User
     * @exception IllegalArgumentException
     * @param user to be deleted
     */
    void deleteUser(User user);

    /**
     * Finds user with specific id
     * @exception IllegalArgumentException
     * @param id with which User should be deleted
     * @return User with specific id
     */
    User getUser(Long id);

    /**
     * Finds user with specific email
     * @exception IllegalArgumentException
     * @param email with which user will be returned
     * @return user with specific email
     */
    User getUserByEmail(String email);

    /**
     * Finds list of users with specific name
     * @exception IllegalArgumentException
     * @param name with which user will be returned
     * @return List of users with specific name
     */
    List<User> getUserByName(String name);

    /**
     * Finds all users
      * @return list of users
     */
    List<User> getAllUsers();
}
