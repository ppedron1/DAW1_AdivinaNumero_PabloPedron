import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// @Pablo Pedrón López

public class main {

    private static final String FICHERO_XML = "scores.xml";
    private static final int MAX_REGISTROS = 1000;

    //METODO QUE GENERA UN NUMERO ALEATORIO
    public static int generarNumeroAleatorio() {
        Random numerorandom = new Random();
        int minRange = 1;
        int maxRange = 100;
        int value = numerorandom.nextInt(maxRange - minRange + 1) + minRange;
        return value;
    }

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
                System.out.println("[!] No se ha ingresado ningún nombre. Se te ha asignado por defecto: " + nombre);
                break;
            }

            // Verificar longitud
            if (nombre.length() < 3 || nombre.length() > 20) {
                System.out.println("[!]Error: el nombre debe tener entre 3 y 20 caracteres.");
                continue;
            }

            // Verificar solo letras y espacios
            if (!nombre.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
                System.out.println("[!]Error: el nombre solo puede contener letras y espacios (sin números ni símbolos).");
                continue;
            }

            //Si pasa todas las validaciones
            System.out.println("Nombre válido: " + nombre);
            break;
        }
        return nombre;
    }

//----METODO PARA COMPARAR EL NUMERO SECRETO CON EL NUMERO INTRODUCIDO POR EL USUARIO (VALIDA LA ENTRADA)
public static int compararnumeros(int numeroSecreto) {
    Scanner sc = new Scanner(System.in);
    int intentos = 0;
    boolean acertado = false;

    System.out.println("Adivina el número entre 1 y 100");
    System.out.println("==================================");

    while (!acertado) {
        int numeroUsuario;

        // Bucle de validación de entrada
        while (true) {
            System.out.print("Intento #" + (intentos + 1) + " Introduce tu número: ");

            // Verifica si es un entero
            if (!sc.hasNextInt()) {
                System.out.println(" [!]Entrada no válida. Debes ingresar un número entero.");
                sc.next(); // descarta la entrada incorrecta
                continue; // NO cuenta como intento
            }

            numeroUsuario = sc.nextInt();

            // Verifica rango
            if (numeroUsuario < 1 || numeroUsuario > 100) {
                System.out.println("[!] Entrada fuera de rango. Debe estar entre 1 y 100.");
                continue; // NO cuenta como intento
            }

            // Número válido
            System.out.println("Número validado: " + numeroUsuario);
            break; // sale del bucle de validación
        }

        intentos++; // Solo incrementa si es un número válido dentro del rango

        // Comparación con el número secreto
        if (numeroUsuario == numeroSecreto) {
            System.out.println("¡¡¡HAS ADIVINADO EL NÚMERO SECRETO!!!!!");
            System.out.println("LO HAS CONSEGUIDO EN " + intentos + " INTENTOS");
            acertado = true;
        } else if (numeroUsuario < numeroSecreto) {
            System.out.println("HAS FALLADO - El número es MAYOR");
        } else {
            System.out.println("HAS FALLADO - El número es MENOR");
        }
    }

    return intentos; // Devuelve el número de intentos
}

    
    public static void mostrarResultados(String nombre, int intentos) {
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        System.out.println("\n===== RESULTADOS DE LA PARTIDA =====");
        System.out.println("Jugador: " + nombre);
        System.out.println("Número de intentos: " + intentos);
        System.out.println("Fecha y hora: " + fechaActual.format(formato));
        System.out.println("====================================");
    }

    // =========================
    // 1. LECTURA Y ESCRITURA XML
    // =========================

    /**
     * Lee el fichero scores.xml y devuelve un Document. Si el fichero no existe y
     * crearSiNoExiste es true, crea un nuevo documento con raíz <ranking>.
     *
     * Si ocurre un error grave, devuelve null.
     */
    private static Document leerXML(boolean crearSiNoExiste)
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            File f = new File(FICHERO_XML);

            if (f.exists())
            {
                Document doc = db.parse(f);
                doc.getDocumentElement().normalize();
                return doc;
            } else if (crearSiNoExiste)
            {
                Document doc = db.newDocument();
                Element root = doc.createElement("ranking");
                doc.appendChild(root);
                return doc;
            } else
            {
                // No existe y no queremos crear
                return null;
            }
        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException e)
        {
            System.out.println("Error al leer/crear el XML: " + e.getMessage());
            return null;
        }
    }

    private static void escribirXML(Document doc)
    {
        if (doc == null)
            return;

        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FICHERO_XML));
            transformer.transform(source, result);

        } catch (TransformerException e)
        {
            System.out.println("Error al escribir el XML: " + e.getMessage());
        }
    }

    // =========================
    // 2. GUARDAR PUNTUACIÓN
    // =========================

    /**
     * Añade una nueva puntuación al XML: <registro> <nombre>...</nombre>
     * <intentos>...</intentos> <fecha>YYYY-MM-DD HH:mm:ss</fecha> </registro>
     */
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

    // =========================
    // 3. CARGAR REGISTROS EN ARRAYS
    // =========================

    /**
     * Carga todos los <registro> del XML en tres arrays paralelos: nombres,
     * intentos, fechas. Devuelve el número de registros cargados.
     */
    private static int cargarRegistrosDesdeXML(String[] nombres, int[] intentos, String[] fechas)
    {
        Document doc = leerXML(false);
        if (doc == null)
        {
            return 0; // no existe o hay error
        }

        NodeList listaRegistros = doc.getElementsByTagName("registro");
        int contador = 0;

        for (int i = 0; i < listaRegistros.getLength() && contador < MAX_REGISTROS; i++)
        {
            Node nodo = listaRegistros.item(i);
            if (nodo.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }

            Element reg = (Element) nodo;

            String nombre = obtenerTextoDeHijo(reg, "nombre");
            String strInt = obtenerTextoDeHijo(reg, "intentos");
            String fecha = obtenerTextoDeHijo(reg, "fecha");

            if (nombre == null || strInt == null || fecha == null)
            {
                continue; // registro incompleto
            }

            int numIntentos;
            try
            {
                numIntentos = Integer.parseInt(strInt.trim());
            } catch (NumberFormatException e)
            {
                continue; // intentos no es número
            }

            nombres[contador] = nombre.trim();
            intentos[contador] = numIntentos;
            fechas[contador] = fecha.trim();
            contador++;
        }

        return contador;
    }

    // =========================
    // 4. ORDENAR POR INTENTOS
    // =========================

    /**
     * Ordena los arrays (nombres, intentos, fechas) por intentos ascendente. Usa
     * burbuja sencilla para que el algoritmo sea fácil de seguir.
     */
    private static void ordenarPorIntentos(String[] nombres, int[] intentos, String[] fechas, int total)
    {
        for (int i = 0; i < total - 1; i++)
        {
            for (int j = i + 1; j < total; j++)
            {
                if (intentos[j] < intentos[i])
                {
                    // Intercambiar intentos
                    int tmpInt = intentos[i];
                    intentos[i] = intentos[j];
                    intentos[j] = tmpInt;

                    // Intercambiar nombres
                    String tmpNom = nombres[i];
                    nombres[i] = nombres[j];
                    nombres[j] = tmpNom;

                    // Intercambiar fechas
                    String tmpFecha = fechas[i];
                    fechas[i] = fechas[j];
                    fechas[j] = tmpFecha;
                }
            }
        }
    }

    // =========================
    // 5. MOSTRAR RANKING
    // =========================

    /**
     * Muestra el TOP 10 del ranking: Pos, Nombre, Intentos, Fecha (con
     * hora:min:seg).
     */

    public static void mostrarRankingXML()
    {
        String[] nombres = new String[MAX_REGISTROS];
        int[] intentos = new int[MAX_REGISTROS];
        String[] fechas = new String[MAX_REGISTROS];

        int total = cargarRegistrosDesdeXML(nombres, intentos, fechas);

        if (total == 0)
        {
            System.out.println("Aún no hay puntuaciones guardadas.");
            return;
        }

        ordenarPorIntentos(nombres, intentos, fechas, total);

        System.out.println("===== RANKING (TOP 10) =====");
        System.out.printf("%-4s %-20s %-10s %-19s%n", "Pos", "Nombre", "Intentos", "Fecha");

        int limite = Math.min(10, total);
        for (int i = 0; i < limite; i++)
        {
            System.out.printf("%-4d %-20s %-10d %-19s%n", (i + 1), nombres[i], intentos[i], fechas[i]);
        }
        System.out.println("============================");
    }

    // =========================
    // 6. AYUDANTE PARA LEER NODOS
    // =========================

    /**
     * Devuelve el texto de la primera etiqueta hija con nombre "nombreHijo". Ej:
     * obtenerTextoDeHijo(reg, "nombre"), "intentos" o "fecha".
     */
    private static String obtenerTextoDeHijo(Element padre, String nombreHijo)
    {
        NodeList lista = padre.getElementsByTagName(nombreHijo);
        if (lista.getLength() == 0)
        {
            return null;
        }
        Node nodo = lista.item(0);
        return nodo.getTextContent();
    }

    public static void main(String[] args) {
        int opcion;
        mostrarRankingXML();
        String nombreUsuario = validarNombre();
        int numeroSecreto = generarNumeroAleatorio();
        int intentos = compararnumeros(numeroSecreto);
        mostrarResultados(nombreUsuario, intentos);
        guardarPuntuacionXML(nombreUsuario, intentos);

        Scanner sc = new Scanner(System.in);

        do
        {
            System.out.println("====== MENÚ RANKING XML ======");
            System.out.println("1. Añadir puntuación de prueba");
            System.out.println("2. Mostrar ranking");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            while (!sc.hasNextInt())
            {
                System.out.print("Introduce un número válido: ");
                sc.next(); // descartamos lo que no es número
            }
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar salto de línea

            switch (opcion)
            {
                case 1:
                    System.out.print("Nombre del jugador: ");
                    String nombre = sc.nextLine();

                    System.out.print("Número de intentos: ");

                    while (!sc.hasNextInt())
                    {
                        System.out.print("Introduce un número de intentos válido: ");
                        sc.next();
                    }
                    intentos = sc.nextInt();
                    sc.nextLine(); // limpiar salto

                    guardarPuntuacionXML(nombre, intentos);
                    System.out.println("Puntuación guardada.\n");
                    break;

                case 2:
                    mostrarRankingXML();
                    System.out.println();
                    break;

                case 3:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida.\n");
            }

        } while (opcion != 3);

        sc.close();

    }
}
