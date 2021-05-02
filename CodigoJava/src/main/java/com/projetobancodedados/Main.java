package main.java.com.projetobancodedados;

import java.sql.*;

/**
 *
 * @author Alexandre
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TelaBD menuPrincipal = new TelaBD();
        
        menuPrincipal.setVisible(true);
        menuPrincipal.iniciarPagina();
    }
    
}
