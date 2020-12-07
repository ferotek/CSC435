/*--------------------------------------------------------

1. Name / Date: David Guo 9/15/2020

2. Java version used, if not the official version for the class:

JDK 9

3. Precise command-line compilation examples / instructions:

e.g.:

> javac InetServer.java


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
import java.io.*; //importing Java libraries
import java.net.*;


public class InetServer {

    public static void main(String a[]) throws IOException {
        int q_len = 6; ; /* same 10 millionth of a second,
                        if the operating system gets more than 6 connections to the server
                        it will throw the requests out*/

        int port = 1565; //where the user port is, don't go below 1025
        Socket sock; // socket programming

        ServerSocket servsock = new ServerSocket(port, q_len);

        System.out.println
                ("David Guos's Inet server 1.8 starting up, listening at port 1565.\n");
        while (true) {
            sock = servsock.accept(); //wait for server to accept connection
            new Worker(sock).start(); // accept sock, pass connect to thread, start sock
        }
    }
}