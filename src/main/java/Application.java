import com.google.gson.Gson;
import http.HttpApi;
import http.models.Follower;
import http.models.FullUser;
import http.models.User;
import http.models.UserFollowers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Application {
    public static int CPU_CORES = Runtime.getRuntime().availableProcessors();
    private final HttpApi httpApi;
    private final TaskInProgressExecutorWrapping httpWorkers = new TaskInProgressExecutorWrapping(Executors.newFixedThreadPool(CPU_CORES));

    private final Map<String, User> userMap = Collections.synchronizedMap(new HashMap<>());
    private final UserFollowMap userFollowersMap = new UserFollowMap();
    private final UserFollowMap userFollowsMap = new UserFollowMap();
    private final Set<String> discoverUserSet = Collections.synchronizedSet(new HashSet<>());
    long startTime;
    private boolean printFullLogs;

    public Application(String userName, String password, boolean printFullLogs) throws IOException {
        startTime = System.currentTimeMillis();
        httpApi = new HttpApi(userName, password,printFullLogs);
        System.out.println("Starting get the users profiles..");
        startDiscover();
        waitingForFinishAndSummarize();
        System.exit(0);

    }

    // Start discover all users profiles by getting the first user.
    private void startDiscover() {
        discoverUser("me");
    }

    // Discover user, Adding him to User Map, Adding and discover all his followers.
    private void discoverUser(String userId) {
        httpWorkers.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    //Checking if the user discovery is already started.
                    if (discoverUserSet.add(userId)) {
                        //Getting the user by get http request.
                        User user = httpApi.getUser(userId);
                        String actualUserId = user.getId();
                        // Adding the user to the User Map
                        userMap.put(actualUserId, user);
                        UserFollowers userFollowers = httpApi.getAllFollowers(actualUserId);
                        // Getting all the user followers.
                        for (Follower follower : userFollowers.getFollowers()) {
                            // Adding the follower to user.
                            userFollowersMap.put(actualUserId, follower.getId());
                            // Adding the user as follower to the follow.
                            userFollowsMap.put(follower.getId(), actualUserId);
                            //  Discover the follower user as long the discover has not yet started.
                            if (!discoverUserSet.contains(follower.getId())) {
                                discoverUser(follower.getId());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //  Waiting for all threads to finish.
    private void waitingForFinishAndSummarize() {
        while (httpWorkers.hasTasksInProgress()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeUsersAsJsonToFile();
        printSummarize();

    }

    private ArrayList<FullUser> getFullUsers() {
        ArrayList<FullUser> userList = new ArrayList<>();
        for (Map.Entry<String, User> userEntry : userMap.entrySet()) {
            String userId = userEntry.getKey();
            User user = userEntry.getValue();
            Set<String> userFollowersSet = userFollowersMap.getMap().get(userId);
            Set<String> userFollowsSet = userFollowsMap.getMap().get(userId);
            userList.add(new FullUser(user, userFollowersSet, userFollowsSet));
        }
        return userList;
    }

    private void printSummarize() {
        long totalTime = System.currentTimeMillis() - startTime;
        long minutes = (totalTime / 1000) / 60;
        long seconds = (totalTime / 1000) % 60;
        System.out.println("###############################################################");
        System.out.println("###############################################################");
        System.out.println("Total Users: " + userMap.size());
        System.out.println("Total Followers Relationship: " + userFollowersMap.getUsersSize());
        System.out.format("Total Time: %d minutes and %d seconds.\n", minutes, seconds);
        System.out.println("###############################################################");
        System.out.println("###############################################################");
    }

    public void writeUsersAsJsonToFile() {
        ArrayList<FullUser> userList = getFullUsers();
        String newFileName = checkFileExistAndGetProperName("Users");
        try (FileWriter file = new FileWriter(newFileName)) {
            Gson gson = new Gson();
            file.write(gson.toJson(userList));
            System.out.println("Successfully Copied JSON user map to file -  " + new File(newFileName).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String checkFileExistAndGetProperName(String fileName) {
        int fileNameQuantity = 1;
        String tempFileName = fileName;
        File userDataFile = new File(fileName + ".txt");
        while (userDataFile.exists()) {
            tempFileName = fileName + "_" + fileNameQuantity++ + ".txt";
            userDataFile = new File(tempFileName);
        }
        return tempFileName;
    }
}
