import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class JSH {
    // Variables
    public Scanner scan;
    public Path actual_folder;
    public LinkedList<String> history;
    public Boolean is_running;
    
    public Map<String, Consumer<String[]>> command_source;
    
    // Manejo de concurrencia y jobs
    public ExecutorService executor;
    public AtomicInteger jobCounter = new AtomicInteger(0);

    public JSH() {
        scan = new Scanner(System.in);
        actual_folder = Path.of("").toAbsolutePath();
        history = new LinkedList<>();
        is_running = true;
        
        // Inicializar ExecutorService
        executor = Executors.newCachedThreadPool();

        command_source = new HashMap<>();
        initializeBuiltIns();
    }

    // Inicializadores de BuiltIns
    private void initializeBuiltIns() {
        // exit
        command_source.put("exit", (args) -> {
            this.is_running = false;
        });

        // pwd
        command_source.put("pwd", (args) -> {
            System.out.println(actual_folder.toAbsolutePath().toString());
        });

        // cd
        command_source.put("cd", (args) -> {
            if (args.length < 2) {
                System.err.println("jsh: cd: missing argument");
                return;
            }
            String dir_path_string = args[1];
            Path dir_path = Path.of(actual_folder.toString(), dir_path_string);

            if (Path.of(dir_path_string).isAbsolute()) {
                dir_path = Path.of(dir_path_string);
            }

            // Normalizar para resolver ".." y "."
            dir_path = dir_path.normalize();

            if (Files.isDirectory(dir_path)) {
                actual_folder = dir_path;
            } else {
                System.err.println("jsh: cd: " + dir_path_string + ": No such file or directory");
            }
        });

        // history
        command_source.put("history", (args) -> {
            int i = 0;
            for (String entry : history) {
                System.out.println((i + 1) + " " + entry);
                i++;
            }
        });
    }

    // Helper functions
    private boolean isOperator(String token) {
        return token.equals("=>") || token.equals("^^");
    }

    private List<String> parseInput(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean insideDoubleQuotes = false;
        boolean insideSingleQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (insideSingleQuotes) {
                if (c == '\'') {
                    insideSingleQuotes = false;
                } else {
                    currentToken.append(c);
                }
            } else if (insideDoubleQuotes) {
                if (c == '"') {
                    insideDoubleQuotes = false;
                } else {
                    currentToken.append(c);
                }
            } else {
                if ((c == ' ' || c == '\t') && (currentToken.length() > 0)) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                } else if (c == '"') {
                    insideDoubleQuotes = true;
                } else if (c == '\'') {
                    insideSingleQuotes = true;
                } else if (c == ' ' || c == '\t') {
                    // Ignorar espacios múltiples fuera de comillas
                } else {
                    currentToken.append(c);
                }
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        // Separar operadores en caso de que no haya separadores de espacios
        List<String> fixedTokens = new ArrayList<>();
        for (String t : tokens) {
            if (t.contains("=>") && !t.equals("=>")) {
                String[] parts = t.split("=>");
                for (int i = 0; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) fixedTokens.add(parts[i]);
                    if (i < parts.length - 1) fixedTokens.add("=>");
                }
            } else if (t.contains("^^") && !t.equals("^^")) {
                String[] parts = t.split("\\^\\^");
                for (int i = 0; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) fixedTokens.add(parts[i]);
                    if (i < parts.length - 1) fixedTokens.add("^^");
                }
            } else {
                fixedTokens.add(t);
            }
        }
        return fixedTokens;
    }

    // Lógica para recuperar comandos del historial (!n, !#)
    private String handleHistoryExpansion(String input) {
        input = input.trim();
        if (input.startsWith("!")) {
            if (input.equals("!#")) {
                if (!history.isEmpty()) {
                    return history.getLast();
                }
            } else {
                try {
                    int index = Integer.parseInt(input.substring(1)) - 1;
                    if (index >= 0 && index < history.size()) {
                        return history.get(index);
                    } else {
                        System.err.println("jsh: !" + (index + 1) + ": event not found");
                        return null; // Indicar error
                    }
                } catch (NumberFormatException e) {
                    // No es un número, ignorar
                }
            }
        }
        return input;
    }

    public void run() {
        while (is_running) {
            System.out.print("jsh:" + actual_folder.toString() + ">> ");
            
            if (!scan.hasNextLine()) break;
            String input_string = scan.nextLine().trim();
            if (input_string.isEmpty()) continue;

            // Manejo de historial (!n)
            String expandedInput = handleHistoryExpansion(input_string);
            if (expandedInput == null) continue; // Error en history
            input_string = expandedInput;

            // Guardar en historial
            if (history.size() >= 20) {
                history.poll();
            }
            history.add(input_string);

            // Parsing y creación de Commands
            List<String> tokens = parseInput(input_string);
            List<Command> commandQueue = new ArrayList<>();
            List<String> currentArgs = new ArrayList<>();

            for (String token : tokens) {
                if (isOperator(token)) {
                    if (!currentArgs.isEmpty()) {
                        boolean isBackground = token.equals("^^");
                        commandQueue.add(new Command(currentArgs, isBackground));
                        currentArgs = new ArrayList<>();
                    }
                } else {
                    currentArgs.add(token);
                }
            }
            // Agregar el último comando si no terminó en operador
            if (!currentArgs.isEmpty()) {
                commandQueue.add(new Command(currentArgs, false));
            }


            // Validar uso correcto de exit
            for (int i = 0; i < commandQueue.size(); i++) {
                Command c = commandQueue.get(i);
                if (c.commandName.equals("exit")) {
                    if (c.isBackground || commandQueue.size() > 1) {
                        System.err.println("jsh: exit cannot be used in sequences or background");
                        continue;
                    }
                }
            }

            // Ejecución de comandos
            for (Command cmd : commandQueue) {
                if (!is_running) break; // Si un comando anterior ejecutó exit
                cmd.execute();
            }
        }
        
        // Cerrar recursos al salir
        executor.shutdownNow();
    }

    // INNER CLASS COMMAND
    class Command {
        private String[] args;
        private boolean isBackground;
        private String commandName;

        public Command(List<String> argsList, boolean isBackground) {
            if (argsList == null || argsList.isEmpty()) {
                throw new IllegalArgumentException("Comando vacío detectado.");
            }
            this.args = argsList.toArray(new String[0]);
            this.commandName = args[0];
            this.isBackground = isBackground;
            
        }

        // Método principal de ejecución
        public void execute() {
            boolean isBuiltIn = command_source.containsKey(commandName);

            if (isBuiltIn) {
                if (isBackground) {
                    // Escenario: Comando JSH Paralelo
                    int myJobId = jobCounter.incrementAndGet();
                    executor.submit(() -> {
                        // Incrementar job counter para built-ins también si se desea, 
                        System.out.println("[" + myJobId + "] " + Thread.currentThread().getId());
                        command_source.get(commandName).accept(args);
                    });
                } else {
                    // Escenario: Comando JSH Secuencial
                    command_source.get(commandName).accept(args);
                }
            } else {
                // Comandos Externos
                runExternalProcess();
            }
        }

        private void runExternalProcess() {


            // Lambda wrapper para ProcessBuilder solicitado
            Runnable executeProcess = () -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder(args);
                    
                    // Transformar el path de actual folder de jsh a File
                    File directory = actual_folder.toFile();
                    pb.directory(directory);

                    // Configuración de salida
                    if (isBackground) {
                        pb.inheritIO();
                    } else {
                        System.out.println(String.join(" ", args) + ":");
                        pb.inheritIO();
                    }

                    Process process = pb.start();

                    // Si es paralelo, imprimir ID inmediatamente (dentro del hilo o antes)
                    if (isBackground) {
                        // Definimos el Job ID para este proceso
                        int myJobId = jobCounter.incrementAndGet();
                        System.out.println("[" + myJobId + "] " + process.pid());
                    }

                    int exitCode = process.waitFor();
                    
                    // Opcional: Notificar fin de tarea background (común en shells debug)
                    // System.out.println("Task " + myJobId + " finished with exit code: " + exitCode);

                } catch (Exception e) {
                    System.err.println("Error ejecutando comando '" + commandName + "': " + e.getMessage());
                    // e.printStackTrace();
                }
            };

            if (isBackground) {
                // Escenario: Comando Externo Paralelo
                executor.submit(executeProcess);
            } else {
                // Escenario: Comando Externo Secuencial
                executeProcess.run();
            }
        }
    }

    // MAIN ENTRY POINT (Para pruebas)
    public static void main(String[] args) {
        JSH shell = new JSH();
        shell.run();
    }
}
