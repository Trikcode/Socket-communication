import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
 // static arraylist
 public static ArrayList<ClientHandler> clientGroup = new ArrayList<>();
 private Socket socket;
 private BufferedReader bufferedReader;
 BufferedWriter bufferedWriter;
 private String clientUsername;
 String groupName;

 public ClientHandler(Socket socket2) {
  try {
   this.socket = socket2;
   this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()));
   this.bufferedReader = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
   this.clientUsername = bufferedReader.readLine();
   this.groupName = bufferedReader.readLine();
   clientGroup.add(this);
   broadcastMessage("New user: " + this.clientUsername + " has joined the group: " + this.groupName);
   // send messages after 200 milliseconds
   for (int i = 0; i < clientGroup.size(); i++) {
    Thread.sleep(200);
    clientGroup.get(i).bufferedWriter.write("welcome welcome \n");
    clientGroup.get(i).bufferedWriter.flush();
   }
  } catch (Exception e) {
   closeEverything(socket, bufferedReader, bufferedWriter);
  }
 }

 @Override
 public void run() {
  String message;
  while (socket.isConnected()) {
   try {
    message = bufferedReader.readLine();
    broadcastMessage(this.clientUsername + ": " + message);
   } catch (Exception e) {
    closeEverything(socket, bufferedReader, bufferedWriter);
    break;
   }
  }
 }

 public void broadcastMessage(String message) {
  for (ClientHandler clientHandler : clientGroup) {
   try {
    if (clientHandler.clientUsername.equals(this.clientUsername)) {
     clientHandler.bufferedWriter.write(message + "\n");
     clientHandler.bufferedWriter.flush();
    }
   } catch (Exception e) {
    closeEverything(socket, bufferedReader, bufferedWriter);
    break;
   }
  }
 }

 public void removeClientGroup() {
  clientGroup.remove(this);
  broadcastMessage("SERVER: " + this.clientUsername + " has left the chat");
 }

 public void closeEverything(Socket socket2, BufferedReader bufferedReader2, BufferedWriter bufferedWriter2) {
  try {
   if (socket2 != null) {
    socket2.close();
   }
   if (bufferedReader2 != null) {
    bufferedReader2.close();
   }
   if (bufferedWriter2 != null) {
    bufferedWriter2.close();
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

}
