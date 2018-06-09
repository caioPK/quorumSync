import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServidorSocket extends Thread {
    private static Map<String, PrintStream> MAP_CLIENTES;
    private static Map<String, Integer> MAP_TEMPO;
    private static int media;
    private  static int tempo = 0;

    private Socket conexao;
    private String nomeCliente;
    private static int numeroClientes=0;
    private static List<String> LISTA_DE_NOMES = new ArrayList<String>();

//    ------------------------- CONSTRUTOR DA CLASSE
//    
    public ServidorSocket(Socket socket) {
        this.conexao = socket;
    }

//    ------------------------- SALVA O USUARIO NO MAPA
//    
    public boolean armazena(String newName) {
        for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
            if (LISTA_DE_NOMES.get(i).equals(newName))
                return true;
        }
        LISTA_DE_NOMES.add(newName);
        this.numeroClientes = (this.numeroClientes + 1);
        System.out.println(this.numeroClientes);
        return false;
    }

//    ------------------------- REMOVE O USUARIO DO MAPA
//    
    public void remove(String oldName) {
        for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
            if (LISTA_DE_NOMES.get(i).equals(oldName))
                LISTA_DE_NOMES.remove(oldName);
        }
         this.numeroClientes = (this.numeroClientes - 1);
        System.out.println(this.numeroClientes);
    }

//------------------------ FUNÇÃO TIMER
//    
  public static void startTimer(){
      
//      COMO FAZER ELA DORMIR ENQUANTO TIMER FOR 0?????????????????????
      while(true){
          System.err.print("");
        if(tempo !=0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                
            }
            tempo = (tempo - 1);
        }
    }
  }
  
//  ----------------------FUNÇÃO PARA SETAR O TEMPO
  public void setTime(String novoT){
      tempo = Integer.parseInt(novoT);
//      enviar(tempo);
  }
    
//    ------------------------- THREAD PRINCIPAL (OUVE CLIENTES)
//    
    public static void main(String args[]) {
        MAP_CLIENTES = new HashMap<String, PrintStream>();
        MAP_TEMPO = new HashMap<String, Integer>();
        
        try {
            ServerSocket server = new ServerSocket(5555);
            System.out.println("ServidorSocket rodando na porta 5555");

//        ------------------- INICIA O TIMER 
            new Thread(() -> {
                startTimer();
            }).start();
            
//        ----------------- OUVINDO CLIENTES
            while (true) {
                Socket conexao = server.accept();
                Thread t = new ServidorSocket(conexao);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

//    ------------------------- CALCULAR A MEDIA
     public void media(int novoT){
        int total;
        total = novoT + this.tempo;
        this.media = (total/this.numeroClientes);
        this.tempo=this.media;
        System.out.println("<<<<<< TOTAL = "+total+" media = "+this.media);
     }
    
//    ------------------------- THREAD PARA OUVIR MSGS
//    
    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            PrintStream saida = new PrintStream(this.conexao.getOutputStream());
            
            this.nomeCliente = entrada.readLine();
             if (this.nomeCliente == null) {
                return;
            } 
//            Armazena o cliente  
             if (armazena(this.nomeCliente)) {
                saida.println("Este nome ja existe! Conecte novamente com outro Nome.");
                this.conexao.close();
                return;
            } else {
                //mostra o nome do cliente conectado ao servidor
                System.out.println(this.nomeCliente + " : Conectado ao Servidor!");
            }

          
            //adiciona os dados de saida do cliente no objeto MAP_CLIENTES
            //A chave sera o nome e valor o printstream
            MAP_CLIENTES.put(this.nomeCliente, saida);
            


            
//            Recebe o tempo que a pessoa setou e salva 
            String[] msg = entrada.readLine().split(":");
            MAP_TEMPO.put(this.nomeCliente, Integer.parseInt(msg[0]));
//            this.tempo= Integer.parseInt(msg[0]);
            media(Integer.parseInt(msg[0]));
            System.out.println(">>> entrada "+ msg[0]);
            send(saida, " escreveu: ", msg);

//          FICA RECEBENDO O UPDATE DOS USUARIOS
            while (msg != null && !(msg[0].trim().equals(""))) {
                
                MAP_TEMPO.put(this.nomeCliente, Integer.parseInt(msg[0]));
                System.out.println(this.nomeCliente + " UPDATE NUMBER "+ msg[0]+ " tempo "+ this.tempo);
                msg = entrada.readLine().split(":");
                
            }
            
            
//            QUANDO A PESSOA SAIR
//
            System.out.println(this.nomeCliente + " saiu do bate-papo!");
            String[] out = {" do bate-papo!"};
            send(saida, " saiu", out);
            remove(this.nomeCliente);
            MAP_CLIENTES.remove(this.nomeCliente);
            this.conexao.close();
            
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

   
    public void send(PrintStream saida, String acao, String[] msg) {
        out:
//        ENVIANDO O TEMPO ATUAL PARA TODO MUNDO
        for (Map.Entry<String, PrintStream> cliente : MAP_CLIENTES.entrySet()) {
            PrintStream chat = cliente.getValue();
            chat.println(this.tempo);   
        }
    }

    
}