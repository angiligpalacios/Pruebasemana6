/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba6;

/**
 *
 * @author ANGILI PALACIOS
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class Principal extends JFrame{
    public JTextField txtRuta = new JTextField(25);
    public JButton btnExaminar = new JButton("Examinar...");
    public JTextField txtBusqueda = new JTextField(10);
    public JTextField txtExtension = new JTextField(5);
    public JTextField txtTamMin = new JTextField(5);
    public JTextField txtTamMax = new JTextField(5);
    public JSpinner spProfundidad = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

    public JButton btnAnalizar = new JButton("Analizar");
    public JButton btnCancelar = new JButton("Cancelar");
    public JProgressBar barraProgreso = new JProgressBar();

    public JTree arbolDirectorios = new JTree(new DefaultMutableTreeNode("Seleccione una carpeta"));
    public JTable tablaResultados;
    public DefaultTableModel modeloTabla;
    public JTextArea areaEstadisticas = new JTextArea();
    public JTextArea areaBitacora = new JTextArea();

    public JButton btnCarp = new JButton("Nueva Carpeta");
    public JButton btnArch = new JButton("Nuevo Archivo");
    public JButton btnBorrar = new JButton("Eliminar");
    public JButton btnEditar = new JButton("Abrir/Editar");
    public JButton btnExportar = new JButton("Exportar");

    public Principal() {
        setTitle("Explorador y Analizador de Archivos");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel filaRuta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaRuta.add(new JLabel("Carpeta raiz:"));
        filaRuta.add(txtRuta);
        filaRuta.add(btnExaminar);
        filaRuta.add(new JLabel("Prof. max:"));
        filaRuta.add(spProfundidad);

        JPanel filaFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaFiltros.add(new JLabel("Buscar:"));
        filaFiltros.add(txtBusqueda);
        filaFiltros.add(new JLabel("Ext:"));
        filaFiltros.add(txtExtension);
        filaFiltros.add(new JLabel("Min KB:"));
        filaFiltros.add(txtTamMin);
        filaFiltros.add(new JLabel("Max KB:"));
        filaFiltros.add(txtTamMax);
        filaFiltros.add(btnAnalizar);
        filaFiltros.add(btnCancelar);

        panelSuperior.add(filaRuta);
        panelSuperior.add(filaFiltros);
        add(panelSuperior, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Arbol", new JScrollPane(arbolDirectorios));

        String[] columnas = {"Nombre", "Ruta", "Tamaño", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaResultados = new JTable(modeloTabla);
        pestanas.addTab("Resultados", new JScrollPane(tablaResultados));

        areaEstadisticas.setEditable(false);
        pestanas.addTab("Estadisticas", new JScrollPane(areaEstadisticas));

        areaBitacora.setEditable(false);
        pestanas.addTab("Bitacora", new JScrollPane(areaBitacora));

        add(pestanas, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotones.add(btnCarp);
        panelBotones.add(btnArch);
        panelBotones.add(btnEditar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnExportar);

        panelInferior.add(barraProgreso, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);
    }
}
