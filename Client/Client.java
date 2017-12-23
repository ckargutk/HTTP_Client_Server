import java.io.*;
import java.net.*;
import java.util.*;
import java.io.*;
public class Client {

	public static Socket clientSocket;

	public static void recvData() throws IOException {
		BufferedReader br;
		br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	public static void main(String[] args) throws IOException {
		String hostname = "", command = "", filename = "";
		int port = 0;
		if (args.length != 4) {
			System.out.println("Invalid arguments by user \nValid arguments are <hostname> <port> <command> <filename>");
		} else {
			hostname = args[0];
			port = Integer.parseInt(args[1]);
			command = args[2];
			filename = args[3];

			clientSocket = new Socket(hostname, port);
			PrintWriter out = new PrintWriter(Client.clientSocket.getOutputStream());

			if (command.equalsIgnoreCase("get")) {
				String fileData;
				fileData = new Scanner(new FileInputStream(filename)).useDelimiter("\\A").next();
				out.println("GET " +"/"+ filename + " HTTP/1.1\n");
				out.println("Host: " + hostname + ":" + port);
				out.println("User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/44.0");
				out.write("Accept: text/plain, text/html, text/*\r\n\r\n");
				out.println(fileData);
				out.flush();
				recvData();
			} else if (command.equalsIgnoreCase("put")) {
				String fileData;
				fileData = new Scanner(new FileInputStream(filename)).useDelimiter("\\A").next();
				out.println("PUT " + filename + " HTTP/1.1\n");
				out.println("Host: localhost:" + clientSocket.getLocalPort()+"\n");
				out.println(fileData);
				System.out.println(fileData);
				out.flush();
				recvData();

			}
			else 
				System.out.println("Invalid Command Entered. Enter Valid GET/PUT Command.");
		}
	}
}
