/*--------------------------------------------------------

1. Name / Date: David Guo 9/15/2020

2. Java version used, if not the official version for the class:

JDK 9

3. Precise command-line compilation examples / instructions:

e.g.:

> javac InetClient.java


4. Precise examples / instructions to run this program:

e.g.:

In separate shell windows:

> java InetServer
> java InetClient



5. List of files needed for running the program.

e.g.:

a. InetServer.java
b. InetClient.java
c. InetChecklist.html

5. Notes:


----------------------------------------------------------*/


import java.io.*; // java libraries
import java.net.*;
public class InetClient{
    public static void main (String args[]) {
        String serverName;
        if (args.length < 1) serverName = "localhost"; //finds server name from the arguments,
            // otherwise use localhost if none provided
        else serverName = args[0];

        System.out.println("Inet Server.\n");
        System.out.println("Using server: " + serverName + ", Port: 1565");
        //Use BufferedReader to read from server
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name;
            do {
                //communicates with console after initial run of program
                System.out.print
                        ("Enter a hostname or an IP address, (quit) to end: ");
                System.out.flush ();
                name = in.readLine ();
                //quits if quit is entered
                if (name.indexOf("quit") < 0)
                    getRemoteAddress(name, serverName);
            } while (name.indexOf("quit") < 0); // quitSmoking.com?
            System.out.println ("Cancelled by user request.");
        } catch (IOException x) {x.printStackTrace ();}
    }

    static String toText (byte ip[]) { /* Make portable for 128 bit format */
        StringBuffer result = new StringBuffer ();
        for (int i = 0; i < ip.length; ++ i) {
            if (i > 0) result.append (".");
            result.append (0xff & ip[i]);
        }
        return result.toString ();
    }

    static void getRemoteAddress (String name, String serverName){
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;

        try{
            //tries to open a connection, don't go below 1025
            sock = new Socket(serverName, 1565);

            // reads data from connection
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            toServer = new PrintStream(sock.getOutputStream());
            // prints data, ip address or server name
            toServer.println(name);
            toServer.flush();

            //gets response from server, reads up to three lines of responses
            for (int i = 1; i <=3; i++){
                textFromServer = fromServer.readLine();
                if (textFromServer != null) System.out.println(textFromServer);
            }
            sock.close();
        } catch (IOException x) {
            System.out.println ("Socket error.");
            x.printStackTrace ();
        }
    }
}