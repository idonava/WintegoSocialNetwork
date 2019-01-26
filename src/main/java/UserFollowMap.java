import java.util.*;

public class UserFollowMap {
    private Map<String, Set<String>> userFollowMap = new HashMap<>();
    private int usersSize = 0;

    //Adding a new follower to the user. from=userID, to=followerUserId.
    public synchronized void put(String from, String to) {
        if (!userFollowMap.containsKey(from)) {
            userFollowMap.put(from, new HashSet<>());
        }
        Set<String> ids = userFollowMap.get(from);
        ids.add(to);
        usersSize++;
    }

    public int getUsersSize() {
        return usersSize;
    }

    public synchronized Map<String, Set<String>> getMap() {
        return new HashMap<>(userFollowMap);
    }
}
