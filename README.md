# deber1-sistemasoperativos

# Descripción general del proyecto

Este proyecto implementa un shell personalizado llamado jsh, desarrollado en Java. El shell:
- Interpreta comandos ingresados por el usuario.
- Ejecuta comandos en primer plano (foreground) usando `=>`.
- Ejecuta comandos en segundo plano (background) usando `^^`.
- Mantiene un historial de los últimos 20 comandos.
- Permite reejecutar comandos usando `!n` y `!#`.
- Se ejecuta dentro de un contenedor Docker con estructura de directorios predefinida.

El shell muestra el prompt en el formato:
jsh:ruta_actual>>

# Comandos soportados

Built-ins (implementados directamente en el shell)
- `cd`
- `pwd`
- `history`
- `exit`

Comandos externos (ejecutados con ProcessBuilder)
- `ls`
- `echo`
- `ip addr`
- `sleep`
- `date`

Los comandos externos se ejecutan como procesos del sistema operativo usando ProcessBuilder.

# Operadores personalizados

Operador `=>` (Foreground - Secuencial)
- Ejecuta comandos en orden.
- El shell espera a que termine cada comando antes de ejecutar el siguiente.
- Equivalente a `;` en Bash.

Operador `^^` (Background - Segundo plano)
- Lanza el comando y no espera a que termine.
- Devuelve inmediatamente el control al usuario.
- Equivalente a `&` en Bash.
- Cada comando en background debe terminar con `^^`.

# Validaciones implementadas
- `exit` no puede usarse en secuencias ni en background.
- Si se usa `^^`, cada comando en background debe terminar con `^^`.
- Manejo de comillas simples y dobles
- cd cambia el directorio del shell (no del proceso hijo).
- Se normalizan rutas para manejar `.` y `..`.

# Cómo compilar y ejecutar el proyecto (Local)
