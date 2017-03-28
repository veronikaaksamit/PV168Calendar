package cz.muni.fi.pv168;

import com.sun.istack.internal.NotNull;

/**
 * Created by xaksamit on 10.3.17.
 */
public class User {

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
        this.email = (email == null) ? null : email.toLowerCase();
    }

    @Override
    public String toString() {
        return "User{" + id + ": " + fullName + ", " + email + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj.getClass() != this.getClass()) return false;

        final User other = (User) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
