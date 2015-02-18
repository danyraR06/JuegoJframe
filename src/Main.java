/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author danyrmz
 */

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main extends JFrame implements Runnable, KeyListener
{
    private final int iMAXANCHO = 10; // maximo numero de personajes por ancho
    private final int iMAXALTO = 8;  // maxuimo numero de personajes por alto
    private int iDireccion;  //direccion de las flechas
    private int iSpeed;  //velocidad de la nena
    private int iVidas; //vidas del juego
    private int iScore; //contador de puntos
    private int iGhostSpeed;  //velocidad de los fantasmas
    private int iJuanillosSpeed; //velocidad de los juanillos
    private int iContJuanillos; //contador de juanillos
    private boolean bolEnd;  //boleana de final
    private boolean bolPausa;  //boleana de pausa
    private Base basNena;         // Objeto principal
    private Base basFantasmita;         // Objeto malo
    private Base basJuanillo; 
    private LinkedList <Base> lklFantasmas;  //lista de fantasmillas
    private LinkedList <Base> lklJuanillos;  //lista de juanillos
    private String nombreArchivo;    //Nombre del archivo.
    private String[] arr;    //Arreglo del archivo divido.

    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image imaImagenApplet;   // Imagen a proyectar en Applet	
    private Image imaOver;  
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private Sonidos adcSonidoChimpy;   // Objeto sonido de Chimpy
    private Sonidos adcSonido2; //sonido cuanddo choca con juanillo

    public Main() {
        nombreArchivo = "Puntaje.txt";
        iDireccion = 0;
        
        iSpeed = 3;
        
        iVidas = (int) (Math.random() * 2) + 3;
        
        iScore = 0;
             
        iGhostSpeed = (int) (Math.random() * 2) + 3;
        
        iJuanillosSpeed = 1;
        
        URL urlImagenOver= this.getClass().getResource("gameover.jpg"); 
	imaOver = Toolkit.getDefaultToolkit().getImage(urlImagenOver);
        
	URL urlImagenPrincipal = this.getClass().getResource("chimpy.gif");
                
        // se crea el objeto para principal 
	basNena = new Base(0, 0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenPrincipal));

        // se posiciona a principal  en la esquina superior izquierda del Applet 
        basNena.setX(getWidth() / 2 - basNena.getAncho() / 2);
        basNena.setY(getHeight() - basNena.getAlto());
        
        
        // se crea el objeto para malo 
        int iPosX = (iMAXANCHO - 1) * getWidth() / iMAXANCHO;
        int iPosY = (iMAXALTO - 1) * getHeight() / iMAXALTO;        
	
       
        //creo la lista de fantasmas
        lklFantasmas = new LinkedList();
        
        //se crea una variable random para determinar la cantidad de fantasmas que se pueden
        //agregar al grupito o a la linkedlist
        int iAzar = (int) (Math.random() * 2) + 8;
        
        //se hace un ciclo para ir agregando los fantasmitas respetando el límite del grupo
        for (int iI = 0; iI < iAzar; iI ++) {
            //la posición de x será un número aleatorio con un int negativo para que el fantasma
            //entre desde fuera del applet
            iPosY = -(int) (Math.random() * (getWidth() * 2));   
            //la posición de y será un número aleatorio 
            iPosX = (int) (Math.random() * (getHeight() / 4));  
            
            //se crea el url de la imagen del fantasma
            URL urlImagenFantasmita = this.getClass().getResource("fantasmita.gif");
            // se crea el objeto fantasmita
            basFantasmita = new Base(iPosX,iPosY, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenFantasmita));
            
            //se genera un numero al azar dentro del rango del alto del applet menos el alto del fantasma
            //para que no se salga
            int iAzarY = (int) (Math.random() * (getHeight() - basFantasmita.getAlto()));
            
            //pongo el fantasma que acabo de crear en la posición al azar que se generó
            basFantasmita.setY(iAzarY);
            
            //agrego los fantasmas a la lista que estaba vacía
            lklFantasmas.add(basFantasmita);
        }
        
        lklJuanillos = new LinkedList();
        
        int iAzar2 = (int) (Math.random() * 5) + 10;
        for (int iI = 0; iI < iAzar2; iI ++) {
            //la posición de x será un número aleatorio con un int negativo para que el juanillo
            //entre desde fuera del applet
            iPosX = (int) (Math.random() * (getWidth()));  
            //la posición de y será un número aleatorio 
            iPosY = (int) (0);   
            
            //se crea el url de la imagen del Juanillo
            URL urlImagenJuanillo = this.getClass().getResource("juanito.gif");
            // se crea el objeto fantasmita
            basJuanillo = new Base(iPosX,iPosY, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenJuanillo));
            
            //se genera un numero al azar dentro del rango del alto del applet menos el alto del fantasma
            //para que no se salga
            int iAzarY2 = (int) (Math.random() * (getHeight() - basJuanillo.getAlto()));
            
            //pongo el fantasma que acabo de crear en la posición al azar que se generó
            basJuanillo.setY(iAzarY2);
            
            //agrego los fantasmas a la lista que estaba vacía
            lklJuanillos.add(basJuanillo);
        }
        adcSonidoChimpy = new Sonidos("monkey1.wav");
        adcSonido2 = new Sonidos("monkey2.wav");
        
        addKeyListener(this);
        
    }
    
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
