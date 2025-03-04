package application;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientName;
    private Server server;
    private boolean isClosed = false;

    public ClientHandler(Socket clientSocket, Server server) {
        try {
            this.clientSocket = clientSocket;
            this.server = server;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.clientName = in.readLine();
            
            // notify everyone that a new user joind
            server.broadcast(clientName + " has joined the chat!");
            // update the server UI
            server.updateMessages(clientName + " has joined the chat!");
        } catch (IOException e) {
            closeConnection();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
            	server.broadcast(message);
            	server.updateMessages(message);
            }
        } catch (IOException e) {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            closeConnection();
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void closeConnection() {
    	if(isClosed) return;
    	isClosed = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        	if (server != null) {
        		// remove the client from the active list first
        		server.removeClient(this);
        		
            	server.broadcast(clientName + " has left the chat.");
                server.updateMessages(clientName + " has left the chat.");
            }
        }
    }
}
