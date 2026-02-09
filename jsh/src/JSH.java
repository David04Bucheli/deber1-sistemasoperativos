import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;


public class JSH {
    // Variables 
    public Scanner scan;
    public Path actual_folder;
    public Queue<String> history;
    public Boolean is_running;
    public Boolean is_running_background;
    public HashMap<String, Function<String[], String[]>> command_source;
    public HashMap<String, Function<String[], String[]>> addons;


    public JSH(){
        scan = new Scanner(System.in);
        actual_folder = Path.of("").toAbsolutePath();
        history = new LinkedList<>();
        is_running = true;
        
        HashMap<String, Function<String[], String[]>> builtIns = new HashMap<>();
        HashMap<String, Function<String[], String[]>> addons = new HashMap<>(); 
        initializeBuiltIns(builtIns);
        initializeAddons(addons);
        
        command_source.putAll(builtIns);
        command_source.putAll(addons);
    }

    // Helper initializers
    private void initializeBuiltIns(HashMap<String, Function<String[], String[]>> builtIns) {
        // exit
        builtIns.put("exit", (args) -> {
            this.is_running = false;
            return new String[]{"See you!"};
        });

        // pwd
        builtIns.put("pwd", (args) -> {
            return new String[]{actual_folder.toAbsolutePath().toString()};
        });

        // cd
        builtIns.put("cd", (args) -> {
            if (args.length < 2) {
                // Usually cd goes to home, but for simplicity:
                throw new IllegalArgumentException("Usage: cd <path>"); 
            }
            String dir_path_string = args[1];
            Path dir_path = Path.of(actual_folder.toString(), dir_path_string);
            
            // Handle absolute paths vs relative paths
            if (dir_path.isAbsolute()) {
                dir_path = Path.of(dir_path_string);
            }

            if (Files.isDirectory(dir_path)) {
                try {
                    // Normalize removes ".." and "."
                    dir_path = dir_path.normalize();
                    return new String[]{};
                } catch (Exception e) {
                    throw new RuntimeException("Error resolving path: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("jsh: cd: " + dir_path + ": No such file or directory");
            }
        });

        // history
        builtIns.put("history", (args) -> {
            String[] lines = new String[history.size()];
            int i = 0;
            for (String entry : history) {
                lines[i] = (i + 1) + " " + entry;
                i++;
            }
            return lines;
        });
    }

    private void initializeAddons(HashMap<String, Function<String[], String[]>> addons) {
        //Implement here the commands with Builder
    }


    //Helper functions
    private boolean isOperator(String token) {
       return token.equals("=>") || token.equals("^^");
    }

    private List<String> parseInput(String input) {     // Analizador sintactico para determinar argumentos
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean insideDoubleQuotes = false;
        boolean insideSingleQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (insideSingleQuotes) {
                if (c == '\'') {
                    insideSingleQuotes = false; // Cierra comilla simple
                } else {
                    currentToken.append(c); // Agrega contenido literal
                }
            } else if (insideDoubleQuotes) {
                if (c == '"') {
                    insideDoubleQuotes = false; // Cierra comilla doble
                } else {
                    currentToken.append(c); // Agrega contenido literal
                }
            } else {
                // Fuera de comillas
                if ((c == ' ' || c == '\t') && (currentToken.length() > 0)) {   // Se termina el token actual
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); 
                } else if (c == '"') {         // Empieza comilla doble
                    insideDoubleQuotes = true; 
                } else if (c == '\'') {        // Empieza comilla simple
                    insideSingleQuotes = true; 
                } else {                       // continua token actual
                    currentToken.append(c);
                }
            }
        }
        
        // Agregar el último token si quedó algo en el buffer
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }   

    public void run(){

        // Loop de menajo de comandos
        while(true) {
            // Mostrar mensaje 
            System.out.print("jsh:"+ actual_folder.toString() +">> ");
            
            // Recibir comando/s
            String input_string = scan.nextLine();
            if (history.size() >= 20){history.poll();}  // Drop the last elemnt of the history
            history.add(input_string);                  // Store the line

            // Pasing
            // Dividir el input usando parseInput
            List<String> input_words = parseInput(input_string);
            
            List<List<String>> commands_array = new ArrayList<>();
            List<String> current_command_args = new ArrayList<>(); 

            for (String word : input_words) {
                if (isOperator(word)) {
                    current_command_args.add(word);
                    commands_array.add(current_command_args);
                    current_command_args = new ArrayList<>();
                } else {
                    current_command_args.add(word);
                }
            }
            if (!current_command_args.isEmpty()) {
                commands_array.add(current_command_args);
            }

            //Verificaciones de Integridad
            
            boolean syntaxValid = true;

            if (commands_array.isEmpty()) {
                continue; 
            }

            for (List<String> command_args : commands_array) {
                if (command_args.isEmpty()) continue;

                String firstToken = command_args.get(0);

                // Verificacion: primer elemento no operador
                if (isOperator(firstToken)) {
                    System.err.println("jsh: syntax error near unexpected token '" + firstToken + "'");
                    syntaxValid = false;
                    break;
                }

                // Verificar estructura interna (Comando - Parametros - [Operador])
                for (int i = 0; i < command_args.size() - 1; i++) {
                    if (isOperator(command_args.get(i))) {
                        System.err.println("jsh: error: operator '" + command_args.get(i) + "' must be at the end of the command block.");
                        syntaxValid = false;
                        break;
                    }
                }
                if (!syntaxValid) break;
            }

            // Cancelar ejecución si hay errores
            if (!syntaxValid) {
                continue;
            }


            
            // Determinar secuencia de comandos según operadores
                // Todos los comandos se ejecutaran secuencialmente, pero aquellos con ^^ se lanzaran en el background
                // Crear una serie de Commands, los cuales verificaran los argumentos introducidos y si se deben correr en background o no
                // 
                
                
            // Ejecutar comandos
                // Usar command.execute()
                // Si alguno de esos comandos es exit, esperar los commands en background e ignorar los siguientes 

        }
    
    }


    // Inner class command for calling built-in / OS commands in sequence/parallel
    class Command {
        private List<String> commandArgs;
        private String commandName;
        private boolean isBackground;

        public Command(List<String> args) {
            this.commandArgs = new ArrayList<>(args);
            this.isBackground = false;

            if (!commandArgs.isEmpty()) {
                String lastToken = commandArgs.get(commandArgs.size() - 1);
                if (lastToken.equals("^^")) {this.isBackground = true;}
                commandArgs.remove(commandArgs.size() - 1); // Remover operador
            }
            else {throw new IllegalArgumentException("Empty command");}
            
            this.commandName = commandArgs.get(0);
        }

        public void execute() {
            if (command_source.containsKey(commandName)) {
                try {
                    String[] argsArray = commandArgs.toArray(new String[0]);
                    String[] result = command_source.get(commandName).apply(argsArray);
                    
                    // Imprimir resultado de builtins (si tienen output)
                    if (result != null && result.length > 0) {
                        for (String line : result) {
                            System.out.println(line);
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } 
            else {
                try {
                    if (isBackground) {
                        executeInBackground();
                    } else {
                        executeInSequence();
                    }
                } catch (IOException | InterruptedException e) {
                    System.err.println("jsh: error executing command: " + e.getMessage());
                }
            }
        }

        // Ejecución en Primer Plano (=>)
        private void executeInSequence() throws IOException, InterruptedException {
            ProcessBuilder pb = new ProcessBuilder(commandArgs);
            
            // IMPORTANTE: Establecer el directorio de trabajo del proceso
            pb.directory(actual_folder.toFile());

            // Iniciar proceso
            Process process = pb.start();

            // Requisito: Imprimir "<comando>: stdout..."
            System.out.println(String.join(" ", commandArgs) + ":");

            // Leer stdout
            try (Scanner sc = new Scanner(process.getInputStream())) {
                while (sc.hasNextLine()) {
                    System.out.println(sc.nextLine());
                }
            }

            // Esperar a que termine
            int exitCode = process.waitFor();

            // Si hubo error (exitCode != 0), imprimir stderr
            if (exitCode != 0) {
                try (Scanner scErr = new Scanner(process.getErrorStream())) {
                    while (scErr.hasNextLine()) {
                        System.err.println(scErr.nextLine());
                    }
                }
            }
        }

        // Ejecución en Segundo Plano (^^)
        private void executeInBackground() throws IOException {
            ProcessBuilder pb = new ProcessBuilder(commandArgs);
            pb.directory(actual_folder.toFile());

            // Para background, permitimos que la salida se herede directamente a la consola
            // de forma asíncrona, o se podría descartar. Heredar es más visual para 'sleep'.
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            
            // Requisito: Imprimir [job_id] PID
            System.out.println("[" + (backgroundJobId++) + "] " + process.pid());
            
            // NO usamos waitFor(), el proceso corre solo.
        }
    }
    public static void main(String[] args) throws Exception {
        JSH shell = new JSH();
        shell.run();
    }


}



