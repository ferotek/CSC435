/* 

David Guo

2020-11-08

-----------------------------------------------------------------------

JDK 9 Used

-----------------------------------------------------------------------
Compile: type  javac HostServer.java on the console 
-----------------------------------------------------------------------

Execute: 

Execute by type java HostServer on the terminal (for mac)
Launch a web browser (perferably Firefox) at localhost:4242
Enter the text in the text box. Type "migrate" to migrate to a different host

-----------------------------------------------------------------------------------



  -------------------------------------------------------------------------------*/


import java.io.BufferedReader; // reads text from inputstream from the socket 
import java.io.IOException; // handles exceptions using try catch
import java.io.InputStreamReader; //reads byte input streams from the socket 
import java.io.PrintStream; // prints data from the outputstream 
import java.net.ServerSocket; // creates a server socket for getting information at port 4242
import java.net.Socket; //creates sockets for the client 
/**

 */

/**
 * AgentWorker
 *
*
 *A worker class for the agent that reads a request from the client. Sends the request
 *to a new chosen host. The client is switched to a new port, and sends an HTML response to the user indicating
 *whether or not the process was successful. The agentworker then kills the parent looper/listener. 
 */


class AgentWorker extends Thread {
	
  Socket sock; //socket which connect to the client
  agentHolder parentAgentHolder; //holdes the counter for the socket and state, maintains the agentstate 
  int localPort; //port number used by the agentlistener
  
  //basic constructor that contains the socket, localport number, and agentholder
  AgentWorker (Socket s, int prt, agentHolder ah) {
    sock = s;
    localPort = prt;
    parentAgentHolder = ah;
  }
  public void run() {
    
    //PrintStream and BufferedReader variables initialized to null
    PrintStream out = null;
    BufferedReader in = null;
    //need to hardcode in the localhost 
    String NewHost = "localhost";
    //holds the newport number wor the worker and creates the socket for connecting with the client
    int NewHostMainPort = 4242;		
    String buf = "";
    int newPort;
    Socket clientSock;
    BufferedReader fromHostServer;
    PrintStream toHostServer;
    
    try {
      //creates printstream, BufferedReader to print getOutputStream and read the InputStreamReader
      out = new PrintStream(sock.getOutputStream());
      in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      
      //read lines from the client 
      String inLine = in.readLine();
      //creates a StringBuilder object so the program can be used on other browsers besides ie
      StringBuilder htmlString = new StringBuilder();
      
      //prints request
      System.out.println();
      System.out.println("Request line: " + inLine);
      
      if(inLine.indexOf("migrate") > -1) {
	//checks to see if the request contains the string "migrate"
	
	//if it does, a new socket on 4242 will be created
	clientSock = new Socket(NewHost, NewHostMainPort);
	fromHostServer = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
	//sends a quest to port 4242 using getOutputStream to get the open port
	toHostServer = new PrintStream(clientSock.getOutputStream());
	toHostServer.println("Please host me. Send my port! [State=" + parentAgentHolder.agentState + "]");
	toHostServer.flush();
	
	
  //reads lines frome the response and look for a valid port to use
	for(;;) {
	 
	  buf = fromHostServer.readLine();
	  if(buf.indexOf("[Port=") > -1) {
	    break;
	  }
	}
	
	
  //find the port number by looking for the substring of the response 
	String tempbuf = buf.substring( buf.indexOf("[Port=")+6, buf.indexOf("]", buf.indexOf("[Port=")) );

  //convert the port number string into an integer
	newPort = Integer.parseInt(tempbuf);
	//log the obtained port number to the  console
	System.out.println("newPort is: " + newPort);
	
	//creates an html response header 
	htmlString.append(AgentListener.sendHTMLheader(newPort, NewHost, inLine));
	//tells the user that migration has completed and submits the html
	htmlString.append("<h3>We are migrating to host " + newPort + "</h3> \n");
	htmlString.append("<h3>View the source of this page to see how the client is informed of the new location.</h3> \n");
	htmlString.append(AgentListener.sendHTMLsubmit());
	
	//logs to the console that the program is killing the parent listening loop
	System.out.println("Killing parent listening loop.");
	//saves the serversocket at the old port and closes the port
	ServerSocket ss = parentAgentHolder.sock;
	ss.close();
	
	//if the response contains a person, increase the statecounter 
      } else if(inLine.indexOf("person") > -1) {
	
	parentAgentHolder.agentState++;
	//the html is sent back to the user 
	htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
  //the html will display the agent state 
	htmlString.append("<h3>We are having a conversation with state   " + parentAgentHolder.agentState + "</h3>\n");
	htmlString.append(AgentListener.sendHTMLsubmit());
	
      } else {
	//if there is no person string, then tells the user that the input is invalid
	htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
	htmlString.append("You have not entered a valid request!\n");
	htmlString.append(AgentListener.sendHTMLsubmit());		
	
	
      }
      //sets the html string and closes the socket 
      AgentListener.sendHTMLtoStream(htmlString.toString(), out);
      sock.close();
      
      
    } catch (IOException ioe) {
      System.out.println(ioe);
    }
  }
  
}

/**
 *
 * 
 * A holder for the agent that tracks information of the agent state
 *Sent from the listener to the worker via the given socket 
 *It also holds the current state, its information, and resources
 *
 */
class agentHolder {
  //has a ServerSocket and agentState
  ServerSocket sock;
  int agentState;
  
  //has a constructor for a server socket
  agentHolder(ServerSocket s) { sock = s;}
}
/**
 * AgentListener objects watch individual ports and respond to requests
 * made upon them(in this scenario from a standard web browser); Craeted 
 * by the hostserver when a new request is made to 4242
 *
 */
class AgentListener extends Thread {
  //instance vars
  Socket sock;
  int localPort;
  
  //basic constructor
  AgentListener(Socket As, int prt) {
    sock = As;
    localPort = prt;
  }
  //the default agentState is initialized to 0
  int agentState = 0;
  
  //when a request is made on the listening port, run using the start() command 
  public void run() {
    BufferedReader in = null;
    PrintStream out = null;
    String NewHost = "localhost";
    System.out.println("In AgentListener Thread");		
    try {
      String buf;
      out = new PrintStream(sock.getOutputStream());
      in =  new BufferedReader(new InputStreamReader(sock.getInputStream()));
      
      //reads in the first input line
      buf = in.readLine();
      
      //if the input line contains "state" 
      if(buf != null && buf.indexOf("[State=") > -1) {
	//looks for what the state is using the substring and stores it first using tempbuf
	String tempbuf = buf.substring(buf.indexOf("[State=")+7, buf.indexOf("]", buf.indexOf("[State=")));
	//convert the string into and int 
	agentState = Integer.parseInt(tempbuf);
	//prints the agentState onto the console
	System.out.println("agentState is: " + agentState);
	
      }
      
      System.out.println(buf);
      //creates a strinbuilder variable to store the html response information
      StringBuilder htmlResponse = new StringBuilder();

      //displays the HTML header information, port, and form to the browser 
      htmlResponse.append(sendHTMLheader(localPort, NewHost, buf));
      htmlResponse.append("Now in Agent Looper starting Agent Listening Loop\n<br />\n");
      htmlResponse.append("[Port="+localPort+"]<br/>\n");
      htmlResponse.append(sendHTMLsubmit());

      sendHTMLtoStream(htmlResponse.toString(), out);
      
      //creastes new serversocket with a queue of 2 and declares a new agentholder to store the socket
      ServerSocket servsock = new ServerSocket(localPort,2);
      agentHolder agenthold = new agentHolder(servsock);
      agenthold.agentState = agentState;
      
      //waiting for connections.
      while(true) {
	sock = servsock.accept();
	//prints message stating recieved a connection
	System.out.println("Got a connection to agent at port " + localPort);
	//create a new agent worker based on paramters, start the worker 
	new AgentWorker(sock, localPort, agenthold).start();
      }
      
    } catch(IOException ioe) {
      //catching an exception from switching a port
      System.out.println("Either connection failed, or just killed listener loop for agent at port " + localPort);
      System.out.println(ioe);
    }
  }

  /*
  *Creates an HTML form  with local port and message
  *doesn't send the response header but sends html headers 
  *
  */
  static String sendHTMLheader(int localPort, String NewHost, String inLine) {
    
    StringBuilder htmlString = new StringBuilder();
    
    htmlString.append("<html><head> </head><body>\n");
    //htmlString.append("<html><head><link rel=\"icon\" href=\"data:,\"> </head><body>\n");
    htmlString.append("<h2>This is for submission to PORT " + localPort + " on " + NewHost + "</h2>\n");
    htmlString.append("<h3>You sent: "+ inLine + "</h3>");
    htmlString.append("\n<form method=\"GET\" action=\"http://" + NewHost +":" + localPort + "\">\n");
    htmlString.append("Enter text or <i>migrate</i>:");
    htmlString.append("\n<input type=\"text\" name=\"person\" size=\"20\" value=\"YourTextInput\" /> <p>\n");
    
    return htmlString.toString();
  }
  //completes the html started by sendHTMLheader
  static String sendHTMLsubmit() {
    return "<input type=\"submit\" value=\"Submit\"" + "</p>\n</form></body></html>\n";
  }
  //gets the length of the html so the program can work with all browsers
  static void sendHTMLtoStream(String html, PrintStream out) {
    
    out.println("HTTP/1.1 200 OK");
    out.println("Content-Length: " + html.length());
    out.println("Content-Type: text/html");
    out.println("");		
    out.println(html);
  }
  
}
/**
 * 
 *
 *The hostserver class starts on port 4242, and starts a listener on port 3000+
 *
 */
public class HostServer {
  //port is initialized at 3000,
  //so start listening at port 3001
  public static int NextPort = 3000;
  
  public static void main(String[] a) throws IOException {
    int q_len = 6;
    int port = 4242;
    Socket sock;
    
    ServerSocket servsock = new ServerSocket(port, q_len);
    System.out.println("Elliott/Reagan DIA Master receiver started at port 4242.");
    System.out.println("Connect from 1 to 3 browsers using \"http:\\\\localhost:4242\"\n");
    //uses port 4242 to listen for new requests
    while(true) {
    
      NextPort = NextPort + 1;
      //creates a new  socket to accept request requests
      sock = servsock.accept();
      //prints startup message to console
      System.out.println("Starting AgentListener at port " + NextPort);
      //creates new AgentListener
      new AgentListener(sock, NextPort).start();
    }
    
  }
}
