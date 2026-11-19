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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
public class ExploradorC {
  private final Principal vista;
    private final ExplorarM modelo;
    private SwingWorker<Void, Integer> hilo;

    public ExploradorC(Principal vista, ExplorarM modelo) {
        this.vista = vista;
        this.modelo = modelo;
        Eventos();
        CargarInicio();
    }

    private void Eventos() {
        vista.btnExaminar.addActionListener(e -> ElegirC());
        vista.btnAnalizar.addActionListener(e -> CorrerAnalisis());
        vista.btnCancelar.addActionListener(e -> PararAnalisis());
        vista.btnCarp.addActionListener(e -> CrearC());
        vista.btnArch.addActionListener(e -> CrearA());
        vista.btnBorrar.addActionListener(e -> BorrarA());
        vista.btnEditar.addActionListener(e -> EditarA());
        vista.btnExportar.addActionListener(e -> ExportarR());
    }

    private void CargarInicio() {
        Map<String, String> conf = modelo.CargarConf();
        if (conf.containsKey("ruta")) {
            vista.txtRuta.setText(conf.get("ruta"));
        }
        if (conf.containsKey("ext")) {
            vista.txtExtension.setText(conf.get("ext"));
        }
        if (conf.containsKey("min")) {
            vista.txtTamMin.setText(conf.get("min"));
        }
        if (conf.containsKey("max")) {
            vista.txtTamMax.setText(conf.get("max"));
        }
        vista.areaBitacora.setText(modelo.LeerLog());
    }

    private void ElegirC() {
        JFileChooser selector = new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (selector.showOpenDialog(vista) == JFileChooser.APPROVE_OPTION) {
            vista.txtRuta.setText(selector.getSelectedFile().getAbsolutePath());
        }
    }

    private void CorrerAnalisis() {
        File directorio = new File(vista.txtRuta.getText().trim());
        try {
            modelo.RevisarC(directorio);
        } catch (MalaRuta ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modelo.GuardarConf(
                vista.txtRuta.getText(),
                vista.txtExtension.getText(),
                vista.txtTamMin.getText(),
                vista.txtTamMax.getText()
        );

        vista.barraProgreso.setIndeterminate(true);
        vista.btnAnalizar.setEnabled(false);
        vista.btnCancelar.setEnabled(true);

        int profMax = (Integer) vista.spProfundidad.getValue();

        hilo = new SwingWorker<Void, Integer>() {
            private Estadistica stats = new Estadistica();
            private DefaultMutableTreeNode raiz;

            @Override
            protected Void doInBackground() {
                modelo.GuardarLog("INICIO ANALISIS", directorio.getAbsolutePath());
                raiz = new DefaultMutableTreeNode(directorio.getName() + " (Carpeta)");
                LlenarArbol(directorio, raiz, 1, profMax);
                modelo.Recorrer(directorio, 1, profMax, stats);
                return null;
            }

            @Override
            protected void done() {
                vista.barraProgreso.setIndeterminate(false);
                vista.btnAnalizar.setEnabled(true);
                vista.btnCancelar.setEnabled(false);

                if (isCancelled()) {
                    modelo.GuardarLog("ANALISIS CANCELADO", directorio.getAbsolutePath());
                    JOptionPane.showMessageDialog(vista, "Análisis cancelado.");
                    return;
                }

                vista.arbolDirectorios.setModel(new DefaultTreeModel(raiz));
                MostrarStats(stats);
                modelo.GuardarLog("FIN ANALISIS", directorio.getAbsolutePath());
                vista.areaBitacora.setText(modelo.LeerLog());
            }
        };
        hilo.execute();
    }

    private void LlenarArbol(File actual, DefaultMutableTreeNode padre, int prof, int profMax) {
        if (profMax > 0 && prof > profMax) {
            return;
        }
        File[] hijos = actual.listFiles();
        if (hijos == null) {
            return;
        }
        for (File hijo : hijos) {
            if (hijo.isHidden()) {
                continue;
            }
            DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(
                    hijo.getName() + (hijo.isDirectory() ? " (Carpeta)" : "")
            );
            padre.add(nodo);
            if (hijo.isDirectory()) {
                LlenarArbol(hijo, nodo, prof + 1, profMax);
            }
        }
    }

    private void MostrarStats(Estadistica s) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADISTICAS ===\n");
        sb.append("Total archivos: ").append(s.TotalArchivos()).append("\n");
        sb.append("Tamaño total: ").append(Estadistica.FormatoB(s.TotalBytes())).append("\n\n");
        sb.append(".TXT  -> ").append(s.getContTxt()).append(" | ").append(Estadistica.FormatoB(s.getBytesTxt())).append("\n");
        sb.append(".JAVA -> ").append(s.getContJava()).append(" | ").append(Estadistica.FormatoB(s.getBytesJava())).append("\n");
        sb.append(".PDF  -> ").append(s.getContPdf()).append(" | ").append(Estadistica.FormatoB(s.getBytesPdf())).append("\n");
        sb.append("Otros -> ").append(s.getContOtros()).append(" | ").append(Estadistica.FormatoB(s.getBytesOtros())).append("\n\n");
        sb.append("Prof. máxima: ").append(s.getProfundidadMax()).append("\n");
        sb.append("Carpetas: ").append(s.getTotalCarpetas()).append(" (Vacías: ").append(s.getCarpetasVacias()).append(")\n");
        sb.append("Más grande: ").append(s.getArchivoGrande() != null ? s.getArchivoGrande().getAbsolutePath() : "N/A").append("\n");
        sb.append("Más reciente: ").append(s.getArchivoReciente() != null ? s.getArchivoReciente().getAbsolutePath() : "N/A").append("\n");
        vista.areaEstadisticas.setText(sb.toString());
    }

    private void PararAnalisis() {
        if (hilo != null && !hilo.isDone()) {
            hilo.cancel(true);
        }
    }

    private void CrearC() {
        String nombreCarpeta = JOptionPane.showInputDialog(vista, "Nombre de carpeta:");
        if (nombreCarpeta != null && !nombreCarpeta.trim().isEmpty()) {
            File nuevaCarpeta = new File(vista.txtRuta.getText(), nombreCarpeta);
            if (nuevaCarpeta.mkdirs()) {
                modelo.GuardarLog("CREAR CARPETA", nuevaCarpeta.getAbsolutePath());
                vista.areaBitacora.setText(modelo.LeerLog());
                JOptionPane.showMessageDialog(vista, "Carpeta creada.");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al crear carpeta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void CrearA() {
        String nombreArchivo = JOptionPane.showInputDialog(vista, "Nombre de archivo:");
        if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
            String contenido = JOptionPane.showInputDialog(vista, "Contenido:");
            File nuevoArchivo = new File(
                    vista.txtRuta.getText(),
                    nombreArchivo.endsWith(".txt") ? nombreArchivo : nombreArchivo + ".txt"
            );
            try {
                modelo.EscribirA(nuevoArchivo, contenido != null ? contenido : "");
                modelo.GuardarLog("CREAR ARCHIVO", nuevoArchivo.getAbsolutePath());
                vista.areaBitacora.setText(modelo.LeerLog());
                JOptionPane.showMessageDialog(vista, "Archivo creado");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(vista, "No se pudo crear el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void BorrarA() {
        String rutaObjetivo = JOptionPane.showInputDialog(vista, "Ruta que va a eliminar ");
        if (rutaObjetivo != null && !rutaObjetivo.trim().isEmpty()) {
            File elemento = new File(rutaObjetivo);
            if (!elemento.exists()) {
                JOptionPane.showMessageDialog(vista, "La ruta no existe", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int opcion = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Eliminar:\n" + elemento.getAbsolutePath() + "?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );
            if (opcion == JOptionPane.YES_OPTION) {
                if (elemento.delete()) {
                    modelo.GuardarLog("BORRAR", elemento.getAbsolutePath());
                    vista.areaBitacora.setText(modelo.LeerLog());
                    JOptionPane.showMessageDialog(vista, "Eliminado.");
                } else {
                    JOptionPane.showMessageDialog(vista, "No se pudo eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void EditarA() {
        String rutaArchivo = JOptionPane.showInputDialog(vista, "Ruta del archivo:");
        if (rutaArchivo != null && !rutaArchivo.trim().isEmpty()) {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || !archivo.isFile()) {
                JOptionPane.showMessageDialog(vista, "Archivo inválido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (archivo.length() > 2 * 1024 * 1024) {
                JOptionPane.showMessageDialog(vista, "Archivo muy grande", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String texto = modelo.LeerA(archivo);
                JTextArea editor = new JTextArea(texto, 20, 50);
                int ok = JOptionPane.showConfirmDialog(
                        vista,
                        new JScrollPane(editor),
                        "Editar: " + archivo.getName(),
                        JOptionPane.OK_CANCEL_OPTION
                );
                if (ok == JOptionPane.OK_OPTION) {
                    modelo.EscribirA(archivo, editor.getText());
                    modelo.GuardarLog("EDITAR ARCHIVO", archivo.getAbsolutePath());
                    vista.areaBitacora.setText(modelo.LeerLog());
                    JOptionPane.showMessageDialog(vista, "Guardado.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(vista, "No se pudo editar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ExportarR() {
        try (FileWriter escritor = new FileWriter("reporte_estadisticas.txt")) {
            escritor.write(vista.areaEstadisticas.getText());
            modelo.GuardarLog("EXPORTAR REPORTE", "reporte_estadisticas.txt");
            vista.areaBitacora.setText(modelo.LeerLog());
            JOptionPane.showMessageDialog(vista, "Reporte guardado");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(vista, "Error al exportar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
  
}
