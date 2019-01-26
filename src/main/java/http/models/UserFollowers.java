package http.models;

import java.util.*;

public class UserFollowers {
    private final String m_userId;
    private Set<Follower> m_followers;

    public UserFollowers(String userId) {
        m_userId = userId;
        m_followers = new HashSet<>();
    }

    public void addFollowes(List<Follower> followers) {
        for (Follower follower : followers) {
            m_followers.add(follower);
        }
    }

    public List<Follower> getFollowers() {
        return new ArrayList<>(m_followers);
    }
}
