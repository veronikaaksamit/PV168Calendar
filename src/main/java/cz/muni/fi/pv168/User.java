package cz.muni.fi.pv168;

import com.sun.istack.internal.NotNull;

/**
 * Created by xaksamit on 10.3.17.
 */
public class User {

    @NotNull
    private Long id;

    private String fullName;

    @NotNull
    private String email;







    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
