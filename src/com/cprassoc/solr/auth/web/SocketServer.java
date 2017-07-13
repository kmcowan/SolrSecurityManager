/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.web;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author kevin
 */
public class SocketServer extends ServerSocket {

    public SocketServer() throws IOException {

    }

    public static void main(String[] args) {
        ServerSocket myService = null;
        Socket clientSocket = null;
        DataInputStream input = null;
        PrintStream output = null;
        String line = "";
        try {
            myService = new ServerSocket(8081);
            clientSocket = myService.accept();
            input = new DataInputStream(clientSocket.getInputStream());
            output = new PrintStream(clientSocket.getOutputStream());
            
             while (true) {
             line = input.readLine();
             System.out.println(line);
             output.write(new String("Request Received \n\r").getBytes());
           }
             
             
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (myService != null) {
                    myService.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }
}
