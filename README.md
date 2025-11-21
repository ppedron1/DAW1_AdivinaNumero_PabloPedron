# DAW1_AdivinaNumero_PabloPedron
@PabloPedrón
# README - Adivina el número en java

-Este documento explica cómo compilar y ejecutar el programa **adivinanumeroaleatorio.java** desde la línea de comandos, pruebas realizadas y una breve explicación de los diferentes métodos y funcionalidades.

## Como ejecutar
---

## Requisitos previos

* Tener instalado **Java JDK** (versión 8 o superior).
* Verificar que los comandos `javac` y `java` funcionan en tu terminal.
* Asegurarte de que el archivo **adivinanumeroaleartorio.java** esté en tu carpeta de trabajo.

---

## 1. Verificar el archivo

Confirma que el archivo existe en el directorio:

```
adivinanumeroaleatorio.java
```

---

## 2. Compilar el programa

Ejecuta el compilador de Java para generar el archivo `.class`:

```bash
javac adivinanumeroaleatorio.java
```

Si no aparecen errores, se creará:

```
adivinanumeroaleatorio.class
```

---

## 3. Ejecutar el programa

Ejecuta el programa ya compilado usando el comando `java` (sin la extensión `.class`):

```bash
java adivinanumeroaleatorio
```

---

## Pruebas realizadas

### [1] Prueba introducir un nombre no valido

Para validar el nombre se ha creado un método que verifica la longitud para que tenga al menos 3 carácteres y máximo 20, y que solo se pueda introducir letras y espacios, sin números ni símbolos.

    //----METODO PARA VALIDAR EL NOMBRE DE USUARIO Y SI NO PONE NADA SE LE ASIGNA "ANONIMO"
    public static String validarNombre() {
        Scanner sc = new Scanner(System.in);
        String nombre;

        while (true) {
            System.out.print("Introduce tu nombre: ");
            nombre = sc.nextLine().trim();

            // Si el nombre está vacío, asignar "Anónimo"
            if (nombre.isEmpty()) {
                nombre = "Anónimo";
                System.out.println("!!! No se ha ingresado ningún nombre. Se te ha asignado por defecto: " + nombre);
                break;
            }

            // Verificar longitud
            if (nombre.length() < 3 || nombre.length() > 20) {
                System.out.println("Error: el nombre debe tener entre 3 y 20 caracteres.");
                continue;
            }

            // Verificar solo letras y espacios
            if (!nombre.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
                System.out.println("Error: el nombre solo puede contener letras y espacios (sin números ni símbolos).");
                continue;
            }

            //Si pasa todas las validaciones
            System.out.println("Nombre válido: " + nombre);
            break;
        }
        return nombre;
    }

Si introduces un nombre no válido como por ejemplo "Pablo$_123" te saldrá por pantalla "Error: el nombre solo puede contener letras y espacios (sin números ni símbolos)."

Si introduces un nombre corto o demasiado largo como "pa" o "pablopablopablopablopablopablopablo" te saldrá "Error: el nombre debe tener entre 3 y 20 caracteres."

Si no introduces nada se te asinará como nombre "Anónimo" y te saldra "!!! No se ha ingresado ningún nombre. Se te ha asignado por defecto Anónimo"

### [2] Prueba si no existe XML se crea uno nuevo

Si no existe un XML crea uno nuevo con este método el cual le dice que etiquetas debe de crear:
  
    public static void guardarPuntuacionXML(String nombre, int intentos)
    {
        if (nombre == null || nombre.trim().isEmpty())
        {
            nombre = "Anonimo";
        }
        nombre = nombre.trim();

        // Fecha y hora actuales
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fecha = ahora.format(fmt);

        // 1) Leer o crear el XML
        Document doc = leerXML(true);
        if (doc == null)
        {
            System.out.println("No se ha podido preparar el XML para guardar la puntuación.");
            return;
        }

        // 2) Obtener la raíz <ranking>
        Element root = doc.getDocumentElement(); // <ranking>

        // 3) Crear <registro> y sus hijos
        Element reg = doc.createElement("registro");

        Element eNombre = doc.createElement("nombre");
        eNombre.setTextContent(nombre);
        reg.appendChild(eNombre);

        Element eIntentos = doc.createElement("intentos");
        eIntentos.setTextContent(String.valueOf(intentos));
        reg.appendChild(eIntentos);

        Element eFecha = doc.createElement("fecha");
        eFecha.setTextContent(fecha);
        reg.appendChild(eFecha);

        // 4) Añadir <registro> a la raíz
        root.appendChild(reg);

        // 5) Guardar XML en disco
        escribirXML(doc);
    }

---

# Funcionalidades y breve descripción de los métodos

## Descripción

Este proyecto es un juego de consola en Java donde el usuario debe adivinar un número aleatorio entre 1 y 100.
El juego incluye:

* Validación del nombre del jugador.
* Control de intentos y retroalimentación si el número es mayor o menor.
* Guardado automático de puntuaciones en un archivo XML (`scores.xml`).
* Ranking de puntuaciones ordenado por menor número de intentos (TOP 10).
* Menú interactivo para añadir puntuaciones de prueba o mostrar el ranking.

---

## Tecnologías y Librerías

* **Java 8+**
* Librerías estándar de Java:

  * `java.time.LocalDateTime`
  * `java.time.format.DateTimeFormatter`
  * `java.util.Random`
  * `java.util.Scanner`
  * `java.io.File`
  * `javax.xml.parsers.*`
  * `javax.xml.transform.*`
  * `org.w3c.dom.*`

---

## Funcionalidades

### Juego

* Genera un número aleatorio entre 1 y 100.
* Valida el nombre del jugador:
  * Si está vacío, se asigna “Anónimo”.
  * Solo se permiten letras y espacios.
  * Longitud mínima 3 y máxima 20 caracteres.
* Permite al jugador adivinar el número y muestra si el intento es mayor o menor que el número secreto.
* Muestra los resultados finales con fecha y hora.

### Ranking

* Guarda puntuaciones en `scores.xml`.
* Carga los registros guardados.
* Ordena automáticamente por menor número de intentos.
* Muestra el ranking TOP 10.

### Menú interactivo

* Añadir puntuación de prueba.
* Mostrar ranking.
* Salir del programa.

---

## Métodos principales

| Método                                              | Descripción                                                    |
| --------------------------------------------------- | -------------------------------------------------------------- |
| `generarNumeroAleatorio()`                          | Genera y muestra un número aleatorio entre 1 y 100.            |
| `validarNombre()`                                   | Valida el nombre del usuario y asigna “Anónimo” si está vacío. |
| `compararnumeros(int numeroSecreto)`                | Controla el flujo del juego y devuelve el número de intentos.  |
| `mostrarResultados(String nombre, int intentos)`    | Muestra los resultados de la partida con fecha y hora.         |
| `guardarPuntuacionXML(String nombre, int intentos)` | Añade un registro al XML con nombre, intentos y fecha.         |
| `mostrarRankingXML()`                               | Muestra el ranking TOP 10.                                     |
| `adivinarnumero(String[] args)`                     | Flujo principal del programa y menú interactivo. (Es el main)  |

Métodos auxiliares privados para gestión de XML:
`leerXML`, `escribirXML`, `cargarRegistrosDesdeXML`, `ordenarPorIntentos`, `obtenerTextoDeHijo`.

---

## Estructura de archivos

```
/DAW1_AdivinaNumero_PabloPedronLopez
│
├─ src/
│  └─ adivinanumeroaleatorio.java
│
├─ scores.xml  (creado automáticamente)
└─ README.md
```

---
