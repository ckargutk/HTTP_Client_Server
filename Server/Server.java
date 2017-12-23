import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {

	private Socket client;

	public Server(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		String request;

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			request = br.readLine();

			System.out.println(request);
			String s[] = request.split(" ");
			String filename ;
			String fileData = "";
			if (s[0].equalsIgnoreCase("get")) {
				filename= s[1].split("/")[1];
				System.out.println(filename);
				File f = new File(filename);
				if (f.exists() && !f.isDirectory()) {
					fileData = new Scanner(new FileInputStream(filename)).useDelimiter("\\A").next();
//					System.out.println(fileData);
					sendResponse(fileData);
				} 
				else {
					sendError("File Not Found");
				}

			} else if (s[0].equalsIgnoreCase("put")) {

				request = br.readLine();
				request = br.readLine();
				request = br.readLine();
				while ((request = br.readLine()) != null) {
					if (request.length()<=0)
						break;
					fileData = fileData+request+"\n";
				}

				FileWriter fileWriter;
				fileWriter = new FileWriter(new File(s[1]));
				fileWriter.write(fileData);
				sendResponse("File saved successfully to Server\n");
				fileWriter.close();

			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	void sendResponse(String response) throws IOException {
		PrintWriter out = new PrintWriter(client.getOutputStream());
		out.println("HTTP/1.1 200 OK");
		out.println("Host: localhost: " +client.getLocalPort());
		System.out.println(response);
		out.println(response);
		out.println("Client Connection Closing...");
		out.flush();
		out.close();
		this.client.close();

	}

	void sendError(String error) throws IOException {
		PrintWriter out = new PrintWriter(client.getOutputStream());
		out.println("HTTP/1.1 404 Not Found");
		out.println("Host: localhost: " + client.getLocalPort());
		System.out.println(client.getLocalPort()+"\n"+client.getLocalSocketAddress());
		System.out.println(error);
		out.println(error);
		out.println("Client Connection Closing...");
		out.flush();
		out.close();
		this.client.close();
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 1){
			System.out.println("Invalid Input by user\n" + "Valid Input: Server <port>");
		} else if (args.length == 1) {
			ServerSocket server = new ServerSocket(Integer.parseInt(args[0]), 1, InetAddress.getByName("localhost"));
			System.out.println("Server waiting for connections on Socket address: "+server.getLocalSocketAddress());
			server.getReuseAddress();
			while (true) {
				Socket clientSocket = server.accept();
				System.out.println("Connected to client "+clientSocket.toString()+"\n");
				new Thread(new Server(clientSocket)).start();;
			}

		} else {
			System.out.println("Invalid input by User\nValid input: Server <port>");
		}
	}
}
