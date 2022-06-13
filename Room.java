import java.util.ArrayList;
import java.util.Collection;

public class Room {
 private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

 @SuppressWarnings("unchecked")
 public void addClient(Client client) {
  clients.addAll((Collection<? extends ClientHandler>) client);
 }

 public void setGroupName(String groupName) {
  for (ClientHandler client : clients) {
   client.groupName = groupName;
  }
 }

 public void broadcastMessage(String message) {
  try {
   for (@SuppressWarnings("unused")
   ClientHandler client : clients) {
    // send message to all clients after 200 milliseconds
    for (int i = 0; i < clients.size(); i++) {
     Thread.sleep(200);
     clients.get(i).bufferedWriter.write(message + "\n");
     clients.get(i).bufferedWriter.flush();

    }

   }
  } catch (Exception e) {
   e.printStackTrace();
  }

 }

}
