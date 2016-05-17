package recsys;

import recsys.core.RecommenderSystem;
import recsys.domain.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Command line interface for the recommender system.
 *
 * Contains basic commands such as:
 * > predict <user-id> <item-id>
 * > recommend <user-id> <limit-items>
 */
public class CLI {

    private static final String PREDICT_USAGE = "predict <user-id> <item-id>";
    private static final String RECOMMEND_USAGE = "recommend <user-id> <limit-results>";

    private RecommenderSystem<User, Integer> system;

    public CLI(RecommenderSystem<User, Integer> system) {
        this.system = system;
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Recommender system CLI");
        System.out.println("Please run a command. Type help for list of commands.");
        while (true) {
            System.out.print("> ");
            String command = null;
            try {
                command = reader.readLine();
            } catch (IOException e) {
                System.err.println("Error reading command: " + e.getMessage());
                break;
            }

            if (command == null)
                break;
            command = command.trim();
            List<String> args = getCommandArguments(command);

            if (command.startsWith("quit")) {
                break;
            } else if (command.startsWith("help")) {
                System.out.println("Available commands:");
                System.out.println(PREDICT_USAGE);
                System.out.println(RECOMMEND_USAGE);
            } else if (command.startsWith("predict")) {
                try {
                    User user = new User(Integer.parseInt(args.get(0)));
                    int item = Integer.parseInt(args.get(1));
                    double result = system.predictRating(user, item);
                    System.out.println("Predicted rating: " + result);
                } catch (Exception e) {
                    System.out.println("Usage: " + PREDICT_USAGE);
                }
            } else if (command.startsWith("recommend")) {
                try {
                    User user = new User(Integer.parseInt(args.get(0)));
                    int limitItems = Integer.parseInt(args.get(1));
                    Collection<Integer> result = system.getRecommendedItems(user, limitItems);
                    System.out.println("Recommended items (" + result.size() + "):");
                    for (Integer item : result) {
                        System.out.println(item);
                    }
                    System.out.println("--end--");
                } catch (Exception e) {
                    System.out.println("Usage: " + RECOMMEND_USAGE);
                }
            } else {
                System.out.println("Unknown command. Type help for list of commands.");
            }
        }
    }

    private static List<String> getCommandArguments(String command) {
        String[] splitted = command.split("\\W");
        List<String> args = new LinkedList<>(Arrays.asList(splitted));
        args.remove(0);
        return args;
    }
}
