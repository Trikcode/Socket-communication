import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
 private Socket socket;
 private BufferedReader bufferedReader;
 private BufferedWriter bufferedWriter;
 private String clientUsername;

 public Client(Socket socket, String clientUsername) {
  try {
   this.socket = socket;
   this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
   this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
   this.clientUsername = clientUsername;
  } catch (Exception e) {
   closeEverything(socket, bufferedReader, bufferedWriter);
  }
 }

 public void sendMessage() {
  try {
   bufferedWriter.write(clientUsername + "\n");
   bufferedWriter.flush();
   try (Scanner scanner = new Scanner(System.in)) {
    while (socket.isConnected()) {
     String message = scanner.nextLine();
     bufferedWriter.write(clientUsername + ": " + message + "\n");
     bufferedWriter.flush();
    }
   }
  } catch (Exception e) {
   closeEverything(socket, bufferedReader, bufferedWriter);
  }
 }

 public void createNewGroup(String groupName) {
  Room room = new Room();
  room.addClient(this);
  room.setGroupName(groupName);
  try {
   bufferedWriter.write(groupName + "\n");
   bufferedWriter.flush();
  } catch (Exception e) {
   closeEverything(socket, bufferedReader, bufferedWriter);
  }

 }

 public void listenForMessage() {
  new Thread(new Runnable() {
   @Override
   public void run() {
    String message;
    try {
     while (socket.isConnected()) {
      message = bufferedReader.readLine();
      System.out.println(message);
     }
    } catch (Exception e) {
     closeEverything(socket, bufferedReader, bufferedWriter);
    }
   }
  }).start();
 }

 public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
  try {
   if (socket != null) {
    socket.close();
   }
   if (bufferedReader != null) {
    bufferedReader.close();
   }
   if (bufferedWriter != null) {
    bufferedWriter.close();
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 public static void main(String[] args) {
  try (Scanner scanner = new Scanner(System.in)) {
   System.out.println("Enter your username: ");
   String username = scanner.nextLine();
   System.out.println("Create A New Group: ");
   System.out.println("Enter group name: ");
   String groupName = scanner.nextLine();
   try {
    Socket socket = new Socket("localhost", 1234);
    Client client = new Client(socket, username);
    client.listenForMessage();
    client.sendMessage();
    client.createNewGroup(groupName);
   } catch (Exception e) {
    e.printStackTrace();
   }
  }
 }
}
