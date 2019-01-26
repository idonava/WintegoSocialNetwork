package http.models;

import java.util.Set;

public class FullUser {
    private User user;
    private Set<String> userFollowersSet;
    private Set<String> userFollowsSet;

    //  Full User
    public FullUser(User user, Set<String> userFollowersSet, Set<String> userFollowsSet) {
        this.user = user;
        this.userFollowersSet = userFollowersSet;
        this.userFollowsSet = userFollowsSet;
    }

    public User getUser() {
        return user;
    }

    public Set<String> getUserFollowersSet() {
        return userFollowersSet;
    }

    public Set<String> getUserFollowsSet() {
        return userFollowsSet;
    }
}
