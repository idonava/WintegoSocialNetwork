package http.models;

import java.util.List;

public class Followers {
    public List<Follower> followers;
    public boolean more;

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    @Override
    public String toString() {
        return "Followers{" +
                "followers=" + followers +
                ", more=" + more +
                '}';
    }
}
