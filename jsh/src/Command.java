import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Command {
    public static Set<String> allowed_commands = Set.of("cd", "pwd", "history", "exit", "ls", "echo", "ip address", "sleep", "date");
    
    public Command(ArrayList<String> command){
        
        // Verficacion de comando adecuado
        if (!allowed_commands.contains(command.getFirst())){
            /// Primera palabra del comando debe estar dentro de las delimitadas
            throw new RuntimeException("The command specified doesnt exist");
        } else if (){
            /// determinar si orden de palabras correcto (Comando - argumentos - Secuencial (=>)\Paralelo (^^) O Comando - Secuencial (=>)\Paralelo (^^)) 
        
        } else if (){
            /// determinar si caracteres extraños dentro del input
        }
    }

    public String execute(){
        if ()
    }

    public String executeInBackground(){

        // 

        // Handling of command failure
    }
}
