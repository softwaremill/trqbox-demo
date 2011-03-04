package pl.softwaremill.demo;

import twitter4j.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * User: szimano
 */
@ApplicationScoped
public class TwitterClient implements Serializable {

    private Twitter twitter;

    private List<User> users;
    private List<Status> statuses;

    @Inject
    public TwitterClient(Twitter twitter) {
        this.twitter = twitter;
    }

    public TwitterClient() { }

    public void loadTweets() throws TwitterException {
        statuses = twitter.getFriendsTimeline();
    }
                                              ;
    public void loadFriends() throws TwitterException {
        IDs followersIDs = twitter.getFollowersIDs(-1);

        users = twitter.lookupUsers(followersIDs.getIDs());
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public static void main(String[] args) throws TwitterException {

        // The factory instance is re-useable and thread safe.
        Twitter twitter = new TwitterFactory().getInstance();
        List<Status> statuses = twitter.getFriendsTimeline();
        System.out.println("Showing friends timeline.");
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":" +
                    status.getText());
        }

        IDs followersIDs = twitter.getFollowersIDs(-1);

        System.out.println("Arrays.toString(followersIDs.getIDs()) = " + Arrays.toString(followersIDs.getIDs()));

        ResponseList<User> users = twitter.lookupUsers(followersIDs.getIDs());

        for (User user : users) {
            System.out.println(user.getName());
        }

    }
}
