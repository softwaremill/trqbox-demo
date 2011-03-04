package pl.softwaremill.demo;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@ApplicationScoped
public class SecondsCounter implements Serializable {
    private volatile int seconds;

    public void inc() {
        seconds++;
    }

    public int getSeconds() {
        return seconds;
    }
}
