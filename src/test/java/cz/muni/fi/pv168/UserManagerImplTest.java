package cz.muni.fi.pv168;

import cz.muni.fi.pv168.common.DBUtils;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by xplacht on 10.3.17.
 */
public class UserManagerImplTest {

    private UserManager manager;
    private DataSource dataSource;
    private static final Comparator<User> USER_ID_COMPARATOR =
            (u1, u2) -> u1.getId().compareTo(u2.getId());

    @org.junit.Before
    public void setUp() throws Exception {
        dataSource = DBUtils.initDB();
        manager = new UserManagerImpl(dataSource);
    }

    @org.junit.After
    public void cleanUp() throws Exception {
        DBUtils.executeSqlScript(dataSource, Main.class.getResourceAsStream("/dropTables.sql"));
        dataSource = null;
    }

    @org.junit.Test
    public void createUser() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        manager.createUser(user);

        Long userId = user.getId();
        assertNotNull(userId);
        User result = manager.getUser(userId);
        assertEquals(user, result);
        assertNotSame(user, result);
        assertDeepEquals(user, result);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void createUserNull() throws Exception {
        manager.createUser(null);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void createUserExistingId() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setId(42L);
        manager.createUser(user);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void createUserExistingEmail() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        manager.createUser(user);
        user.setId(null);
        manager.createUser(user);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void createUserNullEmail() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setEmail(null);
        manager.createUser(user);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void createUserEmptyEmail() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setEmail("");
        manager.createUser(user);
    }

    @org.junit.Test
    public void updateUser()throws Exception {
        User u1 = newUser("theBestUser", "theBestUser@muha.ha");
        User u2 = newUser("theSecondBestUser", "theSecondBestUser@muha.ha");
        manager.createUser(u1);
        manager.createUser(u2);
        Long userId = u1.getId();


        u1 = manager.getUser(userId);
        u1.setEmail("theBestUser2@muha.ha");
        manager.updateUser(u1);
        u1 = manager.getUser(userId);
        assertEquals("theBestUser", u1.getFullName());
        assertEquals("theBestUser2@muha.ha".toLowerCase(), u1.getEmail());

        u1 = manager.getUser(userId);
        u1.setFullName("theBestUser2");
        manager.updateUser(u1);
        u1 = manager.getUser(userId);
        assertEquals("theBestUser2", u1.getFullName());
        assertEquals("theBestUser2@muha.ha".toLowerCase(), u1.getEmail());
        assertDeepEquals(u2, manager.getUser(u2.getId()));

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void updateUserNull()throws Exception {
        manager.updateUser(null);
    }

    @org.junit.Test(expected = IllegalArgumentException.class) //change exception type
    public void updateUserWithNonExistingId() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setId(42L);
        manager.updateUser(user);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void updateUserWithNullId() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setId(null);
        manager.updateUser(user);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void updateUserNullEmail() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        manager.createUser(user);
        user.setEmail(null);
        manager.updateUser(user);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void updateUserEmptyEmail() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setEmail("");
        manager.updateUser(user);
    }

    @org.junit.Test
    public void deleteUser()throws Exception {
        User u1 = newUser("theBestUser", "theBestUser@muha.ha");
        User u2 = newUser("theSecondBestUser", "theSecondBestUser@muha.ha");
        manager.createUser(u1);
        manager.createUser(u2);

        assertNotNull(manager.getUser(u1.getId()));
        assertNotNull(manager.getUser(u2.getId()));

        manager.deleteUser(u1);

        assertNull(manager.getUser(u1.getId()));
        assertNotNull(manager.getUser(u2.getId()));
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void deleteUserNull()throws Exception {
        manager.deleteUser(null);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void deleteUserWithNullId() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setId(null);
        manager.deleteUser(user);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void deleteUserWithNonExistingId() throws Exception {
        User user = newUser("theBestUser", "theBestUser@muha.ha");
        user.setId(42L);
        manager.deleteUser(user);
    }

    @org.junit.Test
    public void getUserByEmailIgnoreCase() throws Exception {
        User u1 = newUser("theBestUser", "theBestUser@muha.ha");
        manager.createUser(u1);
        User result = manager.getUserByEmail("thebestuser@muha.ha");

        assertEquals(u1, result);
        assertDeepEquals(u1, result);

    }

    @org.junit.Test
    public void getUserByNameIgnoreCase() throws Exception {
        User u1 = newUser("theBestUser", "theBestUser@muha.ha");
        User u2 = newUser("theSecondBestUser", "theSecondBestUser@muha.ha");
        manager.createUser(u1);
        manager.createUser(u2);

        List<User> expected = Arrays.asList(u1);
        List<User> actual = manager.getUserByName("thebestuser");

        Collections.sort(expected, USER_ID_COMPARATOR);
        Collections.sort(actual, USER_ID_COMPARATOR);

        assertDeepEquals(expected, actual);
    }

    @org.junit.Test
    public void getAllUsers() throws Exception {
        User u1 = newUser("theBestUser", "theBestUser@muha.ha");
        User u2 = newUser("theSecondBestUser", "theSecondBestUser@muha.ha");
        manager.createUser(u1);
        manager.createUser(u2);

        List<User> expected = Arrays.asList(u1,u2);
        List<User> actual = manager.getAllUsers();

        Collections.sort(expected, USER_ID_COMPARATOR);
        Collections.sort(actual, USER_ID_COMPARATOR);

        assertDeepEquals(expected, actual);


    }

    private User newUser(String fullName, String email) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        return user;
    }

    private void assertDeepEquals(User expected, User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFullName(), actual.getFullName());
    }

    private void assertDeepEquals(List<User> expectedList, List<User> actualList) {
        assertEquals(expectedList.size(), actualList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            assertDeepEquals(expectedList.get(i), actualList.get(i));
        }
    }
}