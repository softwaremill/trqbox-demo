package pl.softwaremill.demo;

import javax.inject.Inject;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class HelloWorld {
    private final SecondsCounter secondsCounter;

    @Inject
    public HelloWorld(SecondsCounter secondsCounter) {
        this.secondsCounter = secondsCounter;
    }

    public String getMessage() {
        return "Hello world! " + secondsCounter.getSeconds();
    }
}
