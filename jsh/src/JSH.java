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



    public void run(){

        // Loop de menajo de comandos
        while(true) {
            // Mostrar mensaje 
            System.out.print("jsh:"+ actual_folder.toString() +">> ");
            
            // Recibircomando/s
            String input_string = scan.nextLine();
            if (history.size() >= 20){history.poll();}  // Drop the last elemnt of the history
            history.add(input_string);                  // Store the line

            String[] input_array = input_string.trim().split("\\s+");
            ArrayList<String> input_array_list = new ArrayList<>(Arrays.asList(input_array));
            
            //Parsear commando 
            List<List<String>> commands_array = new ArrayList<>();
            List<String> current_command_args = new ArrayList<>(); 

            for (String word : input_array_list) {
                current_command_args.add(word);
                
                if ((word.equals("=>") || word.equals("^^")) && !current_command_args.isEmpty()) {
                    commands_array.add(current_command_args);
                    current_command_args = new ArrayList<>();
                }
            }
            if (!current_command_args.isEmpty()) {
                commands_array.add(current_command_args);
            }

            // Verificaciones de integridad. Revisar:
                // Que el primer elemento de un comando no sea un operador
                // Que todos los comandos sigan la estructura 
                    // "comando - parametros - operador"
                    // "comando - parametros"
                    // "comando - operador"
                // Que se haya ingresado al menos un comando
                // De no respetarse notificar al usuario y cancelar la ejecución de todos los comandos ingresados

            
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

        public Command(ArrayList<String> command_args){
            // Verficacion de comando adecuado
            if (!builtIns.containsKey(command_args.getFirst()) || !addons.containsKey(command_args.getFirst())){
                /// Primera palabra del comando debe estar dentro de las delimitadas
                throw new RuntimeException("The command specified doesnt exist");
            } else if (){
                /// determinar si orden de palabras correcto (Comando - argumentos - Secuencial (=>)\Paralelo (^^) O Comando - Secuencial (=>)\Paralelo (^^)) 
            
            } else if (){
                /// determinar si caracteres extraños dentro del input
            }
        }

        public String[] execute(){
            //Determines wheter to execute in sequence or background based on the operator
        }
        
        public String[] executeInSequence(){
            // Encontrar comando correspondiente de command_args[0] dentro de commands_source
            // De recibir una excepcion retornar el mensaje de error
            // 
        }

        public String[] executeInBackground(){
            // Encontrar comando correspondiente de command_args[0] dentro de commands_source
            // Levantar el commando de manera concurrente
            // Al retornar el resultado, imprimirlo inmediatamente en consola
            // De recibir una excepcion retornar el mensaje de error

            // Handling of command failure
        }
    }

    public static void main(String[] args) throws Exception {
        JSH shell = new JSH();
        shell.run();
    }


}



