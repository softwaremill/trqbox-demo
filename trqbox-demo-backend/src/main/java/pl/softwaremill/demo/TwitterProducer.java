package pl.softwaremill.demo;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import javax.enterprise.inject.Produces;

/**
 * User: szimano
 */
public class TwitterProducer {

    @Produces
    public Twitter produceTwitter() {
        return new TwitterFactory().getInstance();
    }
}
