import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class ClienteSocket extends JFrame implements Runnable, ActionListener {
  static PrintStream os = null;
  JTextField textField;
  static JTextArea textArea;
  static int tempo=0;
  
  
//---------------------CONTRUTOR DA CLASSE
  ClienteSocket() {
    super("Cliente quorum");
    add(textField = new JTextField(20), BorderLayout.NORTH);
    add(textArea = new JTextArea(5, 20), BorderLayout.CENTER);
    textField.addActionListener(this);
    textArea.setEditable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
    setVisible(true);
  }

  
//  ------------------THREAD LISTENER DO TEXTO
//  
  public void actionPerformed(ActionEvent e) {
    os.println(textField.getText());
    textField.setText("");
  }

//------------------------ FUNÇÃO TIMER
  public static void startTimer(){
//      COMO FAZER ELA DORMIR ENQUANTO TIMER FOR 0?????????????????????
      while(true){
        if(tempo !=0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClienteSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            tempo = (tempo -1);
            textArea.setText(Integer.toString((tempo)));
            enviar(tempo);
        }else{
            textArea.setText("FIM DO TEMPO");
        }
    }
  }
  
//  ----------------------FUNÇÃO PARA SETAR O TEMPO
  public void setTime(String novoT){
      tempo = Integer.parseInt(novoT);
      enviar(tempo);
  }
  
//  ----------------------FUNÇÃO PARA ENVIAR MSG
  public static void  enviar(int msg){
      os.println(msg);
  }
  
//  ----------------------THREAD PRINCIPAL (TIMER)
//  
  public static void main(String[] args) {
    new Thread(new ClienteSocket()).start();
    startTimer();  
  }

  
//  ----------------THREAD PARA ESCUTAR O SERVER
//  
  public void run() {
    Socket socket = null;
    Scanner is = null;

    try {
        
      socket = new Socket("127.0.0.1", 5555);
      os = new PrintStream(socket.getOutputStream(), true);
      is = new Scanner(socket.getInputStream());
      
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host.");
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to host");
    }

    try {
      String inputLine;
      do {
        inputLine = is.nextLine();
        System.err.println("> "+inputLine );
        setTime(inputLine);
        
      } while (!inputLine.equals(""));

      os.close();
      is.close();
      socket.close();
    } catch (UnknownHostException e) {
      System.err.println("Trying to connect to unknown host: " + e);
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}
