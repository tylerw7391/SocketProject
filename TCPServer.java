   import java.io.*;
   import java.net.*;
import java.util.Arrays;

    public class TCPServer {
       public static void main(String[] args) throws IOException {
      	
			// Variables for setting up connection and communication
         Socket clientSocket = null; // socket to connect with ServerRouter
         PrintWriter out = null; // for writing to ServerRouter
         BufferedReader in = null; // for reading form ServerRouter
         OutputStream fout = null;
         InputStream fin = null;
         InputStream ffin = null;
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostAddress(); // Server machine's IP			
			//String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name - doesn't work
			String routerName = "192.168.1.146"; // ServerRouter host name
			int SockNum = 5555; // port number
			
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(5555);
				System.out.println("New server is listening.");
			}
			catch(IOException e)
			{
				System.err.println("Could not listen on port: 5555");
				System.exit(1);
			}
			
			// Tries to connect to the ServerRouter
			System.out.println("Hi");
         try {
        	 clientSocket = serverSocket.accept();
            //Socket = new Socket(routerName, SockNum);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            fout = clientSocket.getOutputStream();
            fin = clientSocket.getInputStream();
         } 
             catch (UnknownHostException e) {
               System.err.println("Don't know about router: " + routerName);
               System.exit(1);
            } 
             catch (IOException e) {
               System.err.println("Couldn't get I/O for the connection to: " + routerName);
               System.exit(1);
            }
				
      	// Variables for message passing			
         String fromServer; // messages sent to ServerRouter
         String fromClient; // messages received from ServerRouter      
 			//String address ="10.5.3.196"; // destination IP (Client) - changed
         String address ="127.0.0.1"; // destination IP (Client)
 			
			
			// Communication process (initial sends/receives)
			out.println(address);// initial send (IP of the destination Client)
			fromClient = in.readLine();// initial receive from router (verification of connection)
			System.out.println("ServerRouter: " + fromClient);
			
			//created with help from user207421 on stackoverflow.com
			byte[] bArray = new byte[4*1024];
			int i;
			int count = 0;
      	while((i = fin.read(bArray)) > 0)
      	{
      		if(count == 0)
      		{
      			Arrays.fill(bArray, (byte)0);
      		}
      		else
      		{
      			System.out.println("Recieved package starting with " + bArray[0] + " " + bArray[1]);
          		
          		System.out.println(count + ": Server transmit successful!");
          		fout.write(bArray, 0, i);
          		System.out.println("Sent package starting with " + bArray[0] + " " + bArray[1]);
      		}
      		
      		count++;
      		/**
      		System.out.println("Recieved package starting with " + bArray[0] + " " + bArray[1]);
      		count++;
      		System.out.println(count + ": Server transmit successful!");
      		fout.write(bArray, 0, i);
      		System.out.println("Sent package starting with " + bArray[0] + " " + bArray[1]);
      		**/
      	}
				
				
			/**
			// Communication while loop
      	while ((fromClient = in.readLine()) != null) {
            System.out.println("Client said: " + fromClient);
            if (fromClient.equals("Bye.")) // exit statement
					break;
				fromServer = fromClient.toUpperCase(); // converting received message to upper case
				System.out.println("Server said: " + fromServer);
            out.println(fromServer); // sending the converted message back to the Client via ServerRouter
         }
         **/
			
			// closing connections
         out.close();
         in.close();
         clientSocket.close();
      }
   }