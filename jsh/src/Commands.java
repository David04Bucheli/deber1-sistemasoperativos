import java.util.HashMap;
import java.util.function.Function;

public class Commands {

    public static HashMap<String, Function<String[], String[]>> commands_list = new HashMap<>();

    static {

        // echo (implementado como OS command normalmente, pero lo podemos dejar como wrapper)
        commands_list.put("echo", (args) -> {
            if (args.length < 2) return new String[]{""};

            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]);
                if (i != args.length - 1) sb.append(" ");
            }
            return new String[]{sb.toString()};
        });

        // date
        commands_list.put("date", (args) -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("date");
                Process p = pb.start();
                return new java.util.Scanner(p.getInputStream())
                        .useDelimiter("\\A")
                        .next()
                        .lines()
                        .toArray(String[]::new);
            } catch (Exception e) {
                return new String[]{"Error executing date"};
            }
        });

        // sleep
        commands_list.put("sleep", (args) -> {
            try {
                if (args.length < 2)
                    return new String[]{"Usage: sleep <seconds>"};

                int seconds = Integer.parseInt(args[1]);
                Thread.sleep(seconds * 1000L);
                return new String[]{};
            } catch (Exception e) {
                return new String[]{"Error executing sleep"};
            }
        });

        // ls (delegamos al OS)
        commands_list.put("ls", (args) -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(args);
                Process p = pb.start();
                return new java.util.Scanner(p.getInputStream())
                        .useDelimiter("\\A")
                        .next()
                        .lines()
                        .toArray(String[]::new);
            } catch (Exception e) {
                return new String[]{"Error executing ls"};
            }
        });

        // ip addr
        commands_list.put("ip", (args) -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(args);
                Process p = pb.start();
                return new java.util.Scanner(p.getInputStream())
                        .useDelimiter("\\A")
                        .next()
                        .lines()
                        .toArray(String[]::new);
            } catch (Exception e) {
                return new String[]{"Error executing ip"};
            }
        });
    }
}
