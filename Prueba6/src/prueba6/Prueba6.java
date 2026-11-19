/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prueba6;

/**
 *
 * @author ANGILI PALACIOS
 */
import javax.swing.SwingUtilities;
public class Prueba6 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       SwingUtilities.invokeLater(() -> {
            Principal vista = new Principal();
            ExplorarM modelo = new ExplorarM();
            new ExploradorC(vista, modelo);
            vista.setVisible(true);
        });
    } // TODO code application logic here
    
    
}
