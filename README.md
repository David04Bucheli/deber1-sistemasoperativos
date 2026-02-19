# deber1-sistemasoperativos

# Descripción general del proyecto

Este proyecto implementa un shell personalizado llamado jsh, desarrollado en Java. El shell:
- Interpreta comandos ingresados por el usuario.
- Ejecuta comandos en primer plano (foreground) usando '=>'.
- Ejecuta comandos en segundo plano (background) usando '^^'.
- Mantiene un historial de los últimos 20 comandos.
- Permite reejecutar comandos usando '!n' y '!#'.
- Se ejecuta dentro de un contenedor Docker con estructura de directorios predefinida.

El shell muestra el prompt en el formato:
jsh:ruta_actual>>

# Comandos soportados

Built-ins (implementados directamente en el shell)
- 'cd'
- 'pwd'
- 'history'
- 'exit'

Comandos externos (ejecutados con ProcessBuilder)
- 'ls'
- 'echo'
- 'ip addr'
- 'sleep'
- 'date'

Los comandos externos se ejecutan como procesos del sistema operativo usando ProcessBuilder.
