import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class JSH {
    public static void main(String[] args) throws Exception {
        // Variables 
        Scanner scan = new Scanner(System.in);
        Path actual_folder = Path.of("").toAbsolutePath();
        ArrayList history = new ArrayList();
        
        // Loop de menajo de comandos
        while(true) {
            // Mostrar mensaje 
            System.out.print("jsh:"+ actual_folder.toString() +"ruta>> ");
            
            // Recibir y parsear comando/s
            String input_string = scan.nextLine();
            String[] input_array = input_string.trim().split("\\s+");
            ArrayList<String> input_array_list = new ArrayList<>(Arrays.asList(input_array));

            List<List<String>> commands = new ArrayList<>();
            List<String> currentCommand = new ArrayList<>(); 

            for (String word : input_array_list) {
                if (delimiters.contains(word) && !currentCommand.isEmpty()) {
                    commands.add(currentCommand);
                    currentCommand = new ArrayList<>();
                }
                currentCommand.add(word);
            }
            if (!currentCommand.isEmpty()) {
                commands.add(currentCommand);
            }

            // Comprobar calidad de comandos
            // Revisar que el primer String de la cadena sea un comando valido
            // Revisar que el primer String de la cadena sea un comando valido

            // Instanciar y ejecutar comandos

            // Mostrar resultados
        }
    
    
    



    }


}



