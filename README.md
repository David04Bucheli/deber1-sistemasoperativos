# deber1-sistemasoperativos

# Integrantes del equipo

- David Sebastián Bucheli Cabezas (00329939)
- Carlos Flores (329746)
- Alex Luna (00328215)

# Contribución de cada integrante

Carlos Flores:

- Diseño de la arquitectura general del JSH.
- Implementación del loop principal del shell (run()).
- Implementación del parser de entrada:
- Tokenización de comandos.
- Manejo de comillas simples y dobles.
- Separación de operadores `=>` y `^^`.
- Implementación de validaciones de sintaxis.
- Implementación de la expansión del historial (`!n`, `!#`).
- Manejo del almacenamiento del historial.

David Bucheli:

- Implementación de la clase interna Command.
- Manejo de ejecución en:
  - Foreground (secuencial con `=>`).
  - Background (con `^^`).
- Implementación de `ExecutorService` para manejo concurrente.
- Asignación y control de `job_id`.
- Obtención e impresión de PID de procesos.
- Implementación de ejecución de procesos externos usando `ProcessBuilder`.
- Manejo del formato de salida requerido para procesos en primer plano.

Alex Luna:

- Implementación de comandos built-in.
- Manejo correcto del cambio de directorio del shell.
- Implementación del prompt dinámico `jsh:ruta>>`.
- Creación del Dockerfile.
- Configuración de la estructura de directorios requerida.
- Construcción y publicación de la imagen en Docker Hub.
- Pruebas funcionales completas del sistema.

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

Compilar:
> javac JSH.java

Ejecutar:
> java JSH

Construir la imagen:
> docker build -t <usuario>/homework-1:javashell .

Subir a Docker Hub
> docker push <usuario>/homework-1:javashell

Ejecutar el contenedor
> docker run -it <usuario>/homework-1:javashell

