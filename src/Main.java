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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int iCont;
    private int iGhostSpeed;  //velocidad de los fantasmas
    private int iJuanillosSpeed; //velocidad de los juanillos
    private int iCantJuanillos; //contador de juanillos
    private int iCantGhost;
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
        
        iCont = 0;
        
        bolPausa = false;
             
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
        int iCantGhost = (int) (Math.random() * 3) + 8;
        
        //se hace un ciclo para ir agregando los fantasmitas respetando el límite del grupo
        for (int iI = 0; iI < iCantGhost; iI ++) {
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
        
        int iCantJuanillos = (int) (Math.random() * 5) + 10;
        for (int iI = 0; iI < iCantJuanillos; iI ++) {
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
        while (!bolEnd) {
            if(!bolPausa)
            {
                actualiza();    //actualiza la posicion del raton.
                checaColision();    //checa colision del elefante y raton ademas de con el JFrane.
                repaint();    // Se actualiza el <code>JFrame</code> repintando el contenido. 
            }
            try	{
            // El thread se duerme.
                    Thread.sleep (20);
            }
                catch (InterruptedException ex)	{
                    System.out.println("Error en " + ex.toString());
            }
        }
        try{
            leeArchivo();    //lee el contenido del archivo
            vec.add(iVidas);    //Agrega el contenido del nuevo puntaje al vector.
            grabaArchivo();    //Graba el vector en el archivo.
        }catch(IOException e){
            System.out.println("Error en " + e.toString());
        }
        
    }
    
    public void actualiza(){
                for (Base basJuanillo : lklJuanillos) {
            //actualizo al 2 dependiendo de donde anda el1
            if(basJuanillo.getY() + basJuanillo.getAlto() > getHeight())
            {
                int iPosX = (int) (Math.random() * (getWidth() 
                        - basJuanillo.getAncho()));    
                int iPosY = (int) (Math.random() * (-(getHeight())));
                basJuanillo.setX(iPosX);
                basJuanillo.setY(iPosY);
            }
            else
            {
               basJuanillo.setY(basJuanillo.getY() + iJuanillosSpeed); 
            }
        }
        
        //se actualiza posiciones de los fantasmas
        for (Base basFantasma : lklFantasmas) {
            //actualizo al 2 dependiendo de donde anda el1
            if(basFantasma.getX() + basFantasma.getAncho() > getWidth())
            {
                int iPosX = (int) (Math.random() * (-(getWidth())));    
                int iPosY = (int) (Math.random() * (getHeight() - basFantasma.getAlto()));
                basFantasma.setX(iPosX);
                basFantasma.setY(iPosY);
            }
            else
            {
               basFantasma.setX(basFantasma.getX() + iGhostSpeed); 
            }
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
        if(iVidas == 0){
            bolEnd = true;
        }
    }
    
    public void checaColision(){
        switch(iDireccion){
            case 1: { // si se mueve hacia arriba 
                if(basNena.getY() < 0) { // y esta pasando el limite
                    iDireccion = 0;     // se para
                }
                break;    	
            }     
            case 2: { // si se mueve hacia abajo
                // y se esta saliendo del applet
                if(basNena.getY() + basNena.getAlto() > getHeight()) {
                    iDireccion = 0;     // se para
                }
                break;    	
            } 
            case 3: { // si se mueve hacia izquierda 
                if(basNena.getX() < 0) { // y se sale del applet
                    iDireccion = 0;       // se para
                }
                break;    	
            }    
            case 4: { // si se mueve hacia derecha 
                // si se esta saliendo del applet
                if(basNena.getX() + basNena.getAncho() > getWidth()) { 
                    iDireccion = 0;       // se para
                }
                break;    	
            }			
        }
        for (Base basJuanillo : lklJuanillos) {
            
            //checo la colision entre nena y juanitos
            if (basNena.intersecta(basJuanillo)) {
                int iPosX = (int) (Math.random() * (getWidth() - basJuanillo.getAncho()));    
                int iPosY = (int) (Math.random() * (-(getHeight())));
                basJuanillo.setX(iPosX);
                basJuanillo.setY(iPosY);
                iCont++;
                adcSonidoChimpy.play();
                if(iCont==5)
                {
                    iVidas--;
                    iJuanillosSpeed++;
                    iCont=0;
                }
            }
        }
        
        for (Base basFantasma : lklFantasmas) {
            //checo la colision entre nena y fantasmas
            if (basNena.intersecta(basFantasma)) {
                int iPosX = (int) (Math.random() * (-(getWidth())));    
                int iPosY = (int) (Math.random() * (getHeight() - basFantasma.getAlto()));
                basFantasma.setX(iPosX);
                basFantasma.setY(iPosY);
                iScore++;
                adcSonido2.play();
            }
        }

    }
    public void paint(Graphics g) {
		// Inicializan el DoubleBuffer
		if (dbImage == null) {
			dbImage = createImage(this.getSize().width, this.getSize().height);
			dbg = dbImage.getGraphics ();
		}
                URL urlImagenFondo = this.getClass().getResource("Ciudad.png");
                Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
                dbg.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);
		
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
                    graDibujo.setColor(Color.red);
                    Font fontF = new Font("Serif", Font.BOLD, 30);
                    graDibujo.setFont(fontF);
                    basJuanillo.paint(graDibujo, this);
                    graDibujo.setColor(Color.red);  //se establece el color de la letra en rojo
                    graDibujo.drawString("Vidas = " + iVidas, 40, 70);
                    graDibujo.drawString("Puntos = " + iScore, 40,100);
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
    
    private void leeArchivo() throws IOException{
        BufferedReader fileIn;
    	try{
    		fileIn = new BufferedReader(new FileReader(nombreArchivo));
    	} catch (FileNotFoundException e){
    		File puntos = new File(nombreArchivo);
    		PrintWriter fileOut = new PrintWriter(puntos);
    		fileOut.println("100,demo");
    		fileOut.close();
    		fileIn = new BufferedReader(new FileReader(nombreArchivo));
    	}
        
    	String dato = fileIn.readLine();

        // Lee la cantidad de vidas
        iVidas = (Integer.parseInt(dato));
        
        dato = fileIn.readLine();
        
        // Lee el score
        iScore = (Integer.parseInt(dato));
        
        // Lee la direccion
        dato = fileIn.readLine();
        iDireccion = (Integer.parseInt(dato));
 
        // Lee a Nena
        dato = fileIn.readLine();
        
        arr = dato.split(" ");
        basNena.setX(Integer.parseInt(arr[0]));
        basNena.setY(Integer.parseInt(arr[1]));
        
        String dato2 = new String();
        dato2 = fileIn.readLine();
        
        
        // Lee los fantasmas
        for (int iI = 0; iI < (Integer.parseInt(dato2)); iI++) {
            dato = fileIn.readLine();
            arr = dato.split(" ");
            lklFantasmas.get(iI).setX(Integer.parseInt(arr[0]));
            lklFantasmas.get(iI).setY(Integer.parseInt(arr[1]));
        }

         dato2 = fileIn.readLine();
        // Lee los juanitos
        for (int iI = 0; iI < (Integer.parseInt(dato2)); iI++) {
            dato = fileIn.readLine();
            arr = dato.split(" ");
            lklJuanillos.get(iI).setX(Integer.parseInt(arr[0]));
            lklJuanillos.get(iI).setY(Integer.parseInt(arr[1]));
        }
        //velocidad juanillos
        dato = fileIn.readLine();
        iJuanillosSpeed = (Integer.parseInt(dato));
        
        //velocidad ghosts
        dato = fileIn.readLine();
        iGhostSpeed = (Integer.parseInt(dato));
        
        // Lee si esta pausado o no
        dato = fileIn.readLine();
        bolPausa = (Boolean.parseBoolean(dato));
        
    
    	fileIn.close();
    }
    /**
     * Metodo graba información en un archivo.
     *
     * @throws IOException
     */
    private void grabaArchivo() throws IOException {
        PrintWriter fileOut = new PrintWriter(new FileWriter(nombreArchivo));
        
        // Guarda cantidad de vidas
        fileOut.println(iVidas);
        
        // Guarda el score
        fileOut.println(iScore);
        
        // Guarda la dirección
        fileOut.println(iDireccion);
        
        // Guarda a Nena
        fileOut.println(basNena.getX() + " " + basNena.getY());
        
        // Guarda la cantidad de Fantasmas
        fileOut.println(iCantGhost);
        
        // Guarda cada Fantasma
        for (Base basFantasma : lklFantasmas) {
            fileOut.println(basFantasma.getX() + " " + basFantasma.getY());
        }
        
        // Guarda la cantidad de Juanitos
        fileOut.println(iCantJuanillos);
        
        // Guarda cada Juanito
        for (Base basJuanito : lklJuanillos) {
            fileOut.println(basJuanito.getX() + " " + basJuanito.getY());
        }
        
        fileOut.println(iJuanillosSpeed);
        
        fileOut.println(iGhostSpeed);
        
        // Guarda si está pausado o no
        fileOut.println(bolPausa);
        
    	fileOut.close();	        
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
            if (bolPausa)
                bolPausa = false;
            else
                bolPausa = true;         
        } else if(e.getKeyCode() == KeyEvent.VK_G){
         try {
             grabaArchivo();
         } catch (IOException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
        }else if(e.getKeyCode() == KeyEvent.VK_C){
         try {
             leeArchivo();
         } catch (IOException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
        }
    }
    
}
