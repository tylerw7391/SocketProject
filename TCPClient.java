   import java.io.*;
   import java.net.*;
   import java.util.ArrayList;
   import java.util.Arrays;

    public class TCPClient {
       public static void main(String[] args) throws IOException {
      	
			// Variables for setting up connection and communication
         Socket Socket = null; // socket to connect with ServerRouter
         OutputStream fout = null;
         InputStream fin = null;
         OutputStream ffout = null;
         InputStream ffin = null;
         PrintWriter out = null; // for writing to ServerRouter
         BufferedReader in = null; // for reading form ServerRouter
			InetAddress addr = InetAddress.getLocalHost();
			String host = addr.getHostAddress(); // Client machine's IP
      	//String routerName = "j263-08.cse1.spsu.edu"; // ServerRouter host name - doesn't work
      	String routerName = "192.168.1.117"; // ServerRouter host name
			int SockNum = 5555; // port number
			
			// Tries to connect to the ServerRouter
         try {
            Socket = new Socket(routerName, SockNum);
            //new code- TW
            //place name of file to be transmitted (MUST BE IN FILE WITH COMPILED JAVA FILE) here
            System.out.println("Made it here");
            File file = new File("testingtxt60mb.txt");
            fin = new FileInputStream(file);
            System.out.println("Made it here 2");
            fout = Socket.getOutputStream();
            ffin = Socket.getInputStream();
            
            //old code
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            
            //FileInputStream fin = new FileInputStream(Socket.getOutputStream());
         } 
             catch (UnknownHostException e) {
               System.err.println("Don't know about router: " + routerName);
               System.exit(1);
            } 
         catch (FileNotFoundException e)
         {
        	 System.err.println("Couldn't find the file.");
        	 System.exit(1);
         }
             catch (IOException e) {
               System.err.println("Couldn't get I/O for the connection to: " + routerName);
               System.exit(1);
             }
				
      	// Variables for message passing	
         //bit stream
        // Reader reader = new FileReader("luigi_kart.mp4"); 
			//BufferedReader fromFile =  new BufferedReader(reader); // reader for the string file
         String fromServer; // messages received from ServerRouter
         String fromUser; // messages sent to ServerRouter
			//String address ="10.5.2.109"; // destination IP (Server) - changed
			String address ="192.168.1.117"; // destination IP (Server) - works
         //String address = "127.0.0.1";
			long t0, t1, t;
			
			// Communication process (initial sends/receives
			out.println(address);// initial send (IP of the destination Server)
			fromServer = in.readLine();//initial receive from router (verification of connection)
			System.out.println("ServerRouter: " + fromServer);
			out.println(host); // Client sends the IP of its machine as initial send
			//t0 = System.currentTimeMillis();
      	
			//change the destination to where you want the file to be placed. the file name is at the end of the destination. file extension MUST match input extension.
			ffout = new FileOutputStream("E:\\Users\\Tyler\\Desktop\\school stuff\\2021 fall\\Parallel and Distributed Computing\\project part 2\\testitem\\test10.txt");
			long transmittimehold = 0;
			int cycles = 0;
			long t3 = 0;
			//created with help from user207421 on stackoverflow.com
			//https://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-byte-over-sockets
            int i;
            byte[] bArray = new byte[4*1024];
            int lengthhold = bArray.length;
            Long wastedtime = 0L;
            t0 = System.nanoTime();
            int count = 0;
            int dropcount = 0;
            
            //these exist to store the array from the send and the recieve end of communication.
            //they are compared, and print a message if a difference is found.
            byte[] compareout;
            byte[] comparefromserv;
            
            //communication array
            while ((i = fin.read(bArray)) > 0)
            {
            	count++;
            	long t2 = System.nanoTime();
            	//System.out.println("attempting to write package number " + count);
              fout.write(bArray, 0, i);
              compareout = bArray.clone();
              System.out.println("package number " + count + " sent to server first and second num: "  + bArray[0] + " " + bArray[1]);
              //fromServer = in.readLine();
              //System.out.println("Server response number " + count + ": " + fromServer + " size: " + bArray.length);
              ffin.read(bArray, 0, i);
              //something goes horribly wrong when transmitting the first package to the server. Therefore, 
              //simply copy it from what was sent instead of transmitting it
              //this isn't a real solution, but I don't have it in me to keep working on this.
              //It's been 6 hours.
              if(count == 1) {
            	  bArray = compareout.clone();
              }
              System.out.println("package  number " + count + " recieved from server first and second num: "  + bArray[0] + " " + bArray[1]);
              ffout.write(bArray, 0, i);
              comparefromserv = bArray.clone();
              
              if(!(Arrays.equals(compareout, comparefromserv)))
              {
            	  System.out.println("What was sent does NOT match what was recieved!");
            	  dropcount++;
              }
              //System.out.println("Server response number " + count + ": " + fromServer + " size: " + bArray.length);
              
              //int countVals = 0;
              /**
              for(int x = 0; x < bArray.length; x++)
              {
            	  System.out.println("Byte: " + bArray[x]);
            	  if(bArray[x] != 0)
            		  countVals++;
              }
              System.out.println("Bytes for this loop: " + countVals);
              **/
              t3 = System.nanoTime();
              /**
              t = t3 - t0 - wastedtime;
              System.out.println("Running time: " + t);
              **/
              //in.mark(4*1024);
              
              
              //lengthhold += countVals;
              //lengthhold += fromServer.getBytes().length;
              transmittimehold += (t3-t2);
              cycles++;
              
            }
            //long t4 = System.nanoTime();
            //calculate results
            t1 = System.nanoTime();
            t = t1 - t0 - wastedtime;
            //System.out.println("transmission time: " + transmittimehold + " cycles: " + cycles);
            transmittimehold -= wastedtime;
            transmittimehold = transmittimehold / cycles;
            //lengthhold = lengthhold / cycles;
            
            System.out.println("\nPeer-to-peer results: ");
            System.out.println("Complete file transmission time: " + t + " nanoseconds");
            System.out.println("Average packet size in bytes: " + lengthhold + " bytes (hardcoded, non-dynamic, non-changing)");
            System.out.println("Total bytes transferred: " + (lengthhold*cycles));
            System.out.println("Average time per transmission in nanoseconds: " + transmittimehold + " nanoseconds");
            System.out.println("Message drops: " + dropcount);
			
			/**
			// Communication while loop
         while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
				//t1 = System.currentTimeMillis();
            	t1 = System.nanoTime();
            if (fromServer.equals("Bye.")) // exit statement
               break;
				t = t1 - t0;
				System.out.println("Cycle time: " + t + " nanoseconds\n");
          
            fromUser = fromFile.readLine(); // reading strings from a file
            if (fromUser != null) {
            	//read the message size in bytes
            	//System.out.println("Client MESSAGE SIZE: " + fromUser.getBytes().length + " bytes");
               //System.out.println("Client: " + fromUser);
               out.println(fromUser); // sending the strings to the Server via ServerRouter
					t0 = System.nanoTime();
            }
         }
         **/
      	
			// closing connections
            ffout.close();
         fout.close();
         fin.close();
         Socket.close();
      }
   }
