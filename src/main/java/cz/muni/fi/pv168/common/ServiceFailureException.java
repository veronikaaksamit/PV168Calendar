package cz.muni.fi.pv168.common;

/**
 * Created by veronika on 30. 3. 2017.
 */
public class ServiceFailureException extends RuntimeException {

    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
