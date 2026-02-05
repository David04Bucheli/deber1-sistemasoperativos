## Premisa
Diseñar e implementar un shell de línea de comandos personalizado en Java (jsh) que funcione dentro de un contenedor Docker
- Manejo de comandos predefinidos
	- parsing (análisis) de comandos y argumentos.
	- stdout y stder de los procesos.
- gestionar secuencias de ejecución (primer y segundo plano)
- mantener un historial de usuario.
	- Re-ejecución de comandos.


### Requisitos funcionales
- [ ] **Interfaz y Ciclo de Vida:**
    - [ ] Mostrar un prompt con el formato `jsh:ruta>>`.
    - [ ] La ruta del prompt debe actualizarse al cambiar de directorio (`cd`).
    - [ ] El shell debe ejecutarse en un bucle continuo hasta que el usuario ingrese el comando `exit`.
    - [ ] `exit` debe terminar la sesión (solo funciona como comando único, no en secuencias).
- [ ] **Comandos Soportados:**
    - [ ] Implementar "Builtins" (gestionados por el propio shell): `cd`, `pwd`, `history`, `exit`.
    - [ ] Ejecutar comandos externos (tipo Linux) usando `ProcessBuilder`: `ls`, `echo`, `ip addr`, `sleep`, `date`.
    - [ ] Manejar correctamente los argumentos de los comandos (incluyendo cadenas con espacios como `echo "hola mundo"`).
- [ ] **Operadores y Secuencias:**
    - [ ] **Operador => (Primer plano/Secuencial):**
        - [ ] Ejecutar múltiples comandos en orden (izquierda a derecha).
        - [ ] El shell debe esperar a que termine un comando antes de iniciar el siguiente.
    - [ ] **Operador `^^` (Segundo plano/Background):**
        - [ ] Lanzar comandos sin esperar su finalización.
        - [ ] Devolver el control inmediatamente al usuario.
        - [ ] Imprimir `[job_id] PID` al lanzar el proceso (donde `job_id` es un contador de sesión).
        - [ ] Debe funcionar como terminador del comando (similar al `&` en Bash).
- [ ] **Gestión del Historial:**
    - [ ] Almacenar los últimos 20 comandos/líneas ingresadas.
    - [ ] Numerar las entradas secuencialmente comenzando desde el más antiguo.
    - [ ] Guardar la línea completa, incluyendo operadores (`=>`, `^^`).
    - [ ] **Re-ejecución:**
        - [ ] `!n`: Ejecutar el comando número *n* del historial.
        - [ ] `!#`: Ejecutar el comando más reciente.
- [ ] **Formato de Salida:**
    - [ ] **Primer plano:** Imprimir `<comando y argumentos>:` seguido del *stdout* (si es exitoso) o *stderr* (si hay error) en líneas siguientes.
    - [ ] **Segundo plano:** No se exige formato específico de salida, pero debe permitir que la salida aparezca asíncronamente.

### Requisitos no funcionales
- [ ] **Entorno y Tecnología:**
    - [ ] Lenguaje de programación: Java.
    - [ ] Contenerización: La aplicación debe ejecutarse dentro de un contenedor Docker.
    - [ ] Nombre de la imagen Docker: `<usuario>/homework-1:javashell`.
    - [ ] Disponibilidad: La imagen debe estar subida a Docker Hub.
- [ ] **Estructura del Sistema de Archivos (Docker):**
    - [ ] El contenedor debe incluir una estructura de directorios anidados específica: `/folder1/folder2/folder3/folder4/folder5/folder6/folder7`.
    - [ ] Cada carpeta debe contener un archivo de texto vacío con el nombre correspondiente (ej. `/folder1/folder1.txt`).
    - [ ] El contenedor debe tener instalado lo necesario para ejecutar el comando `ip`.
- [ ] **Restricciones de Implementación:**
    - [ ] Uso estricto de los símbolos `=>` para secuencias y `^^` para background (su alteración conlleva penalización).
    - [ ] Los comandos no "builtins" deben ejecutarse como procesos del sistema operativo.
- [ ] **Entregables:**
    - [ ] Código fuente subido a D2L.
    - [ ] Archivo `README.txt` con integrantes, detalles de contribución y observaciones.

    