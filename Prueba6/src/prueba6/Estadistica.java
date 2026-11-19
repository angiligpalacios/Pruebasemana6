/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba6;

/**
 *
 * @author ANGILI PALACIOS
 */
import java.io.File;
public class Estadistica {
    private long contTxt = 0, contJava = 0, contPdf = 0, contOtros = 0;
    private long bytesTxt = 0,bytesJava = 0,bytesPdf = 0, bytesOtros = 0;
    private File archivoGrande = null, archivoReciente = null;
    private int profundidadMax = 0, totalCarpetas = 0, carpetasVacias = 0;
    
  public void SumarA(File archivo, int profundidad) {
        if (archivoGrande == null || archivo.length() > archivoGrande.length()) {
            archivoGrande = archivo;
        }
        if (archivoReciente == null || archivo.lastModified() > archivoReciente.lastModified()) {
            archivoReciente = archivo;
        }
        if (profundidad > profundidadMax) {
            profundidadMax = profundidad;
        }

        String nombreMinuscula = archivo.getName().toLowerCase();
        long tamano = archivo.length();

        if (nombreMinuscula.endsWith(".txt")) {
            contTxt++;
            bytesTxt += tamano;
        } else if (nombreMinuscula.endsWith(".java")) {
            contJava++;
            bytesJava += tamano;
        } else if (nombreMinuscula.endsWith(".pdf")) {
            contPdf++;
            bytesPdf += tamano;
        } else {
            contOtros++;
            bytesOtros += tamano;
        }
    }

    public void SumarC(boolean estaVacia) {
        totalCarpetas++;
        if (estaVacia) {
            carpetasVacias++;
        }
    }

    public long TotalBytes() {
        return bytesTxt + bytesJava + bytesPdf + bytesOtros;
    }

    public long TotalArchivos() {
        return contTxt + contJava + contPdf + contOtros;
    }

    public static String FormatoB(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int nivel = (int) (Math.log(bytes) / Math.log(1024));
        char unidad = "KMGTPE".charAt(nivel - 1);
        return String.format("%.2f %cB", bytes / Math.pow(1024, nivel), unidad);
    }

    public long getContTxt() {
        return contTxt;
    }

    public long getContJava() {
        return contJava;
    }

    public long getContPdf() {
        return contPdf;
    }

    public long getContOtros() {
        return contOtros;
    }

    public long getBytesTxt() {
        return bytesTxt;
    }

    public long getBytesJava() {
        return bytesJava;
    }

    public long getBytesPdf() {
        return bytesPdf;
    }

    public long getBytesOtros() {
        return bytesOtros;
    }

    public File getArchivoGrande() {
        return archivoGrande;
    }

    public File getArchivoReciente() {
        return archivoReciente;
    }

    public int getProfundidadMax() {
        return profundidadMax;
    }

    public int getTotalCarpetas() {
        return totalCarpetas;
    }

    public int getCarpetasVacias() {
        return carpetasVacias;
    }
}
