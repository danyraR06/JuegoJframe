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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;
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
    private static final int WIDTH = 1000;    //Ancho del JFrame
    private static final int HEIGHT = 600;    //Alto del JFrame
    private Base basNena;         // Objeto principal
    private Base basFantasmita;         // Objeto malo
    private Base basJuanillo; 
    private LinkedList <Base> lklFantasmas;  //lista de fantasmillas
    private LinkedList <Base> lklJuanillos;  //lista de juanillos
    private String nombreArchivo;    //Nombre del archivo.
    private String[] arr;    //Arreglo del archivo divido.
    private Vector vec;    // Objeto vector para agregar el puntaje.

    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image dbImage;   // Imagen a proyectar en Applet	
    private Image imaOver;  
    private Graphics dbg;	// Objeto grafico
    private Sonidos adcSonidoChimpy;   // Objeto sonido de Chimpy
    private Sonidos adcSonido2; //sonido cuanddo choca con juanillo

    public Main() {
        nombreArchivo = "Puntaje.txt";
        vec = new Vector();
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
	basNena = new Base(0, 0, WIDTH / iMAXANCHO,
                HEIGHT / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenPrincipal));

        // se posiciona a principal  en la esquina superior izquierda del Applet 
        basNena.setX(WIDTH / 2 - basNena.getAncho() / 2);
        basNena.setY(HEIGHT - basNena.getAlto());
        
        
        // se crea el objeto para malo 
        int iPosX = (iMAXANCHO - 1) * WIDTH / iMAXANCHO;
        int iPosY = (iMAXALTO - 1) * HEIGHT / iMAXALTO;        
	
       
        //creo la lista de fantasmas
        lklFantasmas = new LinkedList();
        
        //se crea una variable random para determinar la cantidad de fantasmas que se pueden
        //agregar al grupito o a la linkedlist
        int iAzar = (int) (Math.random() * 2) + 8;
        
        //se hace un ciclo para ir agregando los fantasmitas respetando el límite del grupo
        for (int iI = 0; iI < iAzar; iI ++) {
            //la posición de x será un número aleatorio con un int negativo para que el fantasma
            //entre desde fuera del applet
            iPosY = -(int) (Math.random() * (WIDTH * 2));   
            //la posición de y será un número aleatorio 
            iPosX = (int) (Math.random() * (HEIGHT / 4));  
            
            //se crea el url de la imagen del fantasma
            URL urlImagenFantasmita = this.getClass().getResource("fantasmita.gif");
            // se crea el objeto fantasmita
            basFantasmita = new Base(iPosX,iPosY, WIDTH / iMAXANCHO,
                HEIGHT / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenFantasmita));
            
            //se genera un numero al azar dentro del rango del alto del applet menos el alto del fantasma
            //para que no se salga
            int iAzarY = (int) (Math.random() * (HEIGHT - basFantasmita.getAlto()));
            
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
            iPosX = (int) (Math.random() * (WIDTH));  
            //la posición de y será un número aleatorio 
            iPosY = (int) (0);   
            
            //se crea el url de la imagen del Juanillo
            URL urlImagenJuanillo = this.getClass().getResource("juanito.gif");
            // se crea el objeto fantasmita
            basJuanillo = new Base(iPosX,iPosY, WIDTH / iMAXANCHO,
                HEIGHT / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenJuanillo));
            
            //se genera un numero al azar dentro del rango del alto del applet menos el alto del fantasma
            //para que no se salga
            int iAzarY2 = (int) (Math.random() * (HEIGHT - basJuanillo.getAlto()));
            
            //pongo el fantasma que acabo de crear en la posición al azar que se generó
            basJuanillo.setY(iAzarY2);
            
            //agrego los fantasmas a la lista que estaba vacía
            lklJuanillos.add(basJuanillo);
        }
        adcSonidoChimpy = new Sonidos("monkey1.wav");
        adcSonido2 = new Sonidos("monkey2.wav");
        
        addKeyListener(this);
        // Declaras un hilo
        Thread t = new Thread (this);
	// Empieza el hilo
	t.start ();
        
    }
    public void run () {
        while (iVidas>0) {
            actualiza();    //actualiza la posicion del raton.
            checaColision();    //checa colision del elefante y raton ademas de con el JFrane.
            repaint();    // Se actualiza el <code>JFrame</code> repintando el contenido.
            try	{
            // El thread se duerme.
                    Thread.sleep (20);
            }
                catch (InterruptedException ex)	{
                    System.out.println("Error en " + ex.toString());
            }
        }
    }
    
    public void actualiza(){
        for (Base basFantasmita : lklFantasmas) {   //para cadafantasma
           //establezco que solo se actualizará la posición de x para que avance de lado
            basFantasmita.setX(basFantasmita.getX() + iGhostSpeed); 
        }
        for(Base basJuanillo : lklJuanillos) {
            basJuanillo.setY(basJuanillo.getY() + iJuanillosSpeed);
        }
        
        switch(iDireccion){  //en base a la direccion
            case 1: {    //se mueve hacia arriba
               basNena.setY(basNena.getY() -iSpeed);
               break;
            }
            case 2: {    //se mueve hacia abajo
                basNena.setY(basNena.getY() +iSpeed);
                break;
            }
            case 3: {    //se mueve hacia la izquierda
                basNena.setX(basNena.getX() -iSpeed);
                break;
            }
            case 4: {    //se mueve hacia la derecha
                basNena.setX(basNena.getX() +iSpeed);
                break;
            }
        }
        if(iContJuanillos == 5){
            iContJuanillos = 0;
            iVidas --;
            iJuanillosSpeed ++;
        }
        if(iVidas == 0){
            bolEnd = !bolEnd;
        }
    }
    
    public void checaColision(){
        switch(iDireccion){
            case 1: { // si se mueve hacia arriba 
                if(basNena.getY() < 0) { // y esta pasando el limite
                    iDireccion = 2;     // se cambia la direccion para abajo
                }
                break;    	
            }     
            case 2: { // si se mueve hacia abajo
                // y se esta saliendo del applet
                if(basNena.getY() + basNena.getAlto() > getHeight()) {
                    iDireccion = 1;     // se cambia la direccion para arriba
                }
                break;    	
            } 
            case 3: { // si se mueve hacia izquierda 
                if(basNena.getX() < 0) { // y se sale del applet
                    iDireccion = 4;       // se cambia la direccion a la derecha
                }
                break;    	
            }    
            case 4: { // si se mueve hacia derecha 
                // si se esta saliendo del applet
                if(basNena.getX() + basNena.getAncho() > getWidth()) { 
                    iDireccion = 3;       // se cambia direccion a la izquierda
                }
                break;    	
            }			
        }
        for (Base basFantasmita : lklFantasmas) {   //para cada fantasma dentro de la lista
            //checo la colision entre los fantasmas y susanita
            
             //para evitar que los fantasmitallas se salgan del applet de abajo
            if(basFantasmita.getY() + basFantasmita.getAlto() > getHeight()){
                basFantasmita.setY(this.getHeight() - basFantasmita.getAncho());
            }
            //para evitar que los fantasmillas se salgan de arriba
            if(basFantasmita.getY() < 0){
                basFantasmita.setY(0);
            }
            if (basNena.intersecta(basFantasmita)) {  //si se inersecta a susana con el fantasma
                iScore ++;  //si hay colisión se resta 1 punto
                adcSonidoChimpy.play();
                basFantasmita.setX((int) Math.random() * getWidth()); //se reposiciona el fantasma en x = 0
                basFantasmita.setY(-32); //se reposiciona afuera del applet
            }
            //si la imagen del fantasma llega a sobrepasar el ancho del applet
            if(basFantasmita.getX() + basFantasmita.getAncho() > getWidth()) {
                basFantasmita.setX(0); //la x se inicializa en 0
                basFantasmita.setY((int) (Math.random() * getHeight()) -  //l
                        basFantasmita.getAlto());
            }
        }
        for (Base basJuanillo : lklJuanillos) {   //para cada fantasma dentro de la lista
            //checo la colision entre los fantasmas y susanita
            
             //para evitar que los fJuanillos se salgan del applet de la derecha
            if(basJuanillo.getX() + basJuanillo.getAncho() > getWidth()){
                basJuanillo.setX(this.getWidth() - basJuanillo.getAlto());
            }
            //para evitar que los fantasmillas se salgan de la izquierda
            if(basJuanillo.getX() < 0){
                basJuanillo.setX(0);
            }
            if (basNena.intersecta(basJuanillo)) {  //si se inersecta a susana con el el juanete
                adcSonido2.play();
                iContJuanillos ++;
                basJuanillo.setY((int) Math.random() * WIDTH); //se reposiciona el fantasma en x = 0
                basJuanillo.setX(-32); //se reposiciona afuera del applet
            }
            //si la imagen del fantasma llega a sobrepasar el largo del applet
            if(basJuanillo.getY() + basJuanillo.getAlto() > getHeight()) {
                basJuanillo.setY(0); //la x se inicializa en 0
                basJuanillo.setX((int) (Math.random() * HEIGHT) -  //l
                        basJuanillo.getAncho());
            }
        }
    }
    public void paint(Graphics g) {
		// Inicializan el DoubleBuffer
		if (dbImage == null) {
			dbImage = createImage(this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics ();
		}
		// Actualiza la imagen de fondo.
		dbg.setColor(getBackground ());
		dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
		// Actualiza el Foreground.
		dbg.setColor(getForeground());
		paint1(dbg);
		// Dibuja la imagen actualizada
		g.drawImage(dbImage, 0, 0, this);
    }
    public void paint1(Graphics graDibujo) {
        // si la imagen ya se cargo
        if(!bolEnd){  //si el juego aun continúa
            if (basNena != null && lklFantasmas != null && lklJuanillos != null) {
                    graDibujo.fillRect(0, 0, WIDTH, HEIGHT);
                    //Dibuja la imagen de principal en el Applet
                    basNena.paint(graDibujo, this);
                
                    // pinto cada fantasma de la lista
                    for (Base basFantasmitas : lklFantasmas) {
                    //Dibuja la imagen de LOS fantasmitas en el Applet
                        basFantasmitas.paint(graDibujo, this);
                    }
                    for (Base basJuanillo : lklJuanillos) {
                    //Dibuja la imagen de LOS fantasmitas en el Applet
                        basJuanillo.paint(graDibujo, this);
                    }
                    //Dibuja la imagen de malo en el Applet
                    basJuanillo.paint(graDibujo, this);
                    graDibujo.setColor(Color.red);  //se establece el color de la letra en rojo
                    graDibujo.drawString("Vidas = " + iVidas, 15, 15);
                    graDibujo.drawString("Puntos = " + iScore, 700,15);
            } // sino se ha cargado se dibuja un mensaje 
            else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
            }
        }else {
                graDibujo.drawImage(imaOver,150,0,this); 
            }  
    }
    public static void main(String[] args) {
    	// TODO code application logic here
    	Main score = new Main();
    	score.setSize(WIDTH, HEIGHT);
    	score.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	score.setVisible(true);
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
     if (e.getKeyCode() == KeyEvent.VK_W) {    //Presiono flecha arriba
            iDireccion = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {    //Presiono flecha abajo
	    iDireccion = 2;
	} else if (e.getKeyCode() == KeyEvent.VK_A) {    //Presiono flecha izquierda
	    iDireccion = 3;
	} else if (e.getKeyCode() == KeyEvent.VK_D) {    //Presiono flecha derecha
	    iDireccion = 4;
        } else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){  //si la boleana de esc falsa
            bolEnd = !bolEnd;
        } else if(e.getKeyCode() == KeyEvent.VK_P){  //si la boleana de pausa es falsa
            bolPausa = !bolPausa;          
        }
    }
    
}
