/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba6;

/**
 *
 * @author ANGILI PALACIOS
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ExplorarM {
   private final String rutaBitacora = "bitacora.log";
    private final String rutaConfig = "config.txt";

    public void GuardarLog(String accion, String ruta) {
        try (FileWriter escritor = new FileWriter(rutaBitacora, true);
             BufferedWriter buffer = new BufferedWriter(escritor)) {
            String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            buffer.write("[" + fechaHora + "] " + accion + " | " + ruta);
            buffer.newLine();
        } catch (IOException ex) {
            System.err.println("Escribir bien la bitacora porfavor: " + ex.getMessage());
        } 
        }
        public String LeerLog() {
        File archivoLog = new File(rutaBitacora);
        if (!archivoLog.exists()) {
            return "La bitacora esta sin nada";
        }
        StringBuilder texto = new StringBuilder();
        try (FileReader lector = new FileReader(archivoLog);
             BufferedReader buffer = new BufferedReader(lector)) {
            String linea;
            while ((linea = buffer.readLine()) != null) {
                texto.append(linea).append("\n");
            }
        } catch (IOException ex) {
            texto.append("No se pudo leer");
        }
        return texto.toString();
    }

    public void GuardarConf(String rutaRaiz, String extension, String tamMinKb, String tamMaxKb) {
        try (FileWriter escritor = new FileWriter(rutaConfig, false)) {
            escritor.write("ruta=" + rutaRaiz + "\n");
            escritor.write("ext=" + extension + "\n");
            escritor.write("min=" + tamMinKb + "\n");
            escritor.write("max=" + tamMaxKb + "\n");
        } catch (IOException ex) {
            System.err.println("No se pudo guardar la configuracion " + ex.getMessage());
        }
    }

    public Map<String, String> CargarConf() {
        Map<String, String> datosConfig = new HashMap<>();
        File archivoConfig = new File(rutaConfig);
        if (!archivoConfig.exists()) {
            return datosConfig;
        }
        try (FileReader lector = new FileReader(archivoConfig);
             BufferedReader buffer = new BufferedReader(lector)) {
            String linea;
            while ((linea = buffer.readLine()) != null) {
                String[] partes = linea.split("=", 2);
                if (partes.length == 2) {
                    datosConfig.put(partes[0], partes[1]);
                }
            }
        } catch (IOException ex) {
            System.err.println("No se pudo cargar la configuracion: " + ex.getMessage());
        }
        return datosConfig;
    }

    public void RevisarC(File directorio) throws MalaRuta {
        if (directorio == null || directorio.getPath().trim().isEmpty()) {
            throw new MalaRuta("Favor rellenar ruta, esta esta vacia");
        }
        if (!directorio.exists()) {
            throw new MalaRuta("Esa ruta no existe actualmente");
        }
        if (!directorio.isDirectory()) {
            throw new MalaRuta("Esa ruta no es la de la carpeta");
        }
    }

    public void Recorrer(File directorio, int profundidadActual, int profundidadMax, Estadistica estadistica) {
        if (directorio == null || !directorio.exists() || (profundidadMax > 0 && profundidadActual > profundidadMax)) {
            return;
        }

        File[] elementos = directorio.listFiles();
        if (elementos == null) {
            GuardarLog("No tiene permiso", directorio.getAbsolutePath());
            return;
        }

        if (elementos.length == 0) {
            estadistica.SumarC(true);
            return;
        } else {
            estadistica.SumarC(false);
        }

        for (File elemento : elementos) {
            if (elemento.isHidden()) {
                continue;
            }
            if (elemento.isDirectory()) {
                Recorrer(elemento, profundidadActual + 1, profundidadMax, estadistica);
            } else if (elemento.isFile()) {
                estadistica.SumarA(elemento, profundidadActual);
            }
        }
    }

    public void BuscarDup(File directorio, Map<String, List<File>> gruposDuplicados) {
        File[] elementos = directorio.listFiles();
        if (elementos == null) {
            return;
        }
        for (File elemento : elementos) {
            if (elemento.isHidden()) {
                continue;
            }
            if (elemento.isDirectory()) {
                BuscarDup(elemento, gruposDuplicados);
            } else if (elemento.isFile()) {
                String clave = elemento.getName().toLowerCase() + "_" + elemento.length();
                gruposDuplicados.computeIfAbsent(clave, k -> new ArrayList<>()).add(elemento);
            }
        }
    }

    public void CopiarA(File archivoOrigen, File archivoDestino) throws IOException {
        try (FileReader lector = new FileReader(archivoOrigen);
             FileWriter escritor = new FileWriter(archivoDestino)) {
            char[] buffer = new char[1024];
            int caracteresLeidos;
            while ((caracteresLeidos = lector.read(buffer)) != -1) {
                escritor.write(buffer, 0, caracteresLeidos);
            }
        }
    }

    public String LeerA(File archivo) throws IOException {
        StringBuilder texto = new StringBuilder();
        try (FileReader lector = new FileReader(archivo);
             BufferedReader buffer = new BufferedReader(lector)) {
            String linea;
            while ((linea = buffer.readLine()) != null) {
                texto.append(linea).append("\n");
            }
        }
        return texto.toString();
    }

    public void EscribirA(File archivo, String contenido) throws IOException {
        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write(contenido);
        }
    }
}
