package cz.muni.fi.pv168;

/**
 * Created by xaksamit on 10.3.17.
 */
public enum Category {

    BIRTHDAY,
    NAMEDAY,
    PERSONAL,
    WORK,
    OTHER;

    public static Category fromInteger(int x) {
        switch(x) {
            case 0:
                return BIRTHDAY;
            case 1:
                return NAMEDAY;
            case 2:
                return PERSONAL;
            case 3:
                return WORK;
            case 4:
                return OTHER;
        }
        return null;
    }
}
