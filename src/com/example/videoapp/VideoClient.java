package com.example.videoapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

public class VideoClient{
	 private Socket server;
	 private UpToReader in;
	 private Writer out;
	 
	 void connect() throws UnknownHostException, IOException{
		 server = new Socket("bismarck.sdsu.edu", 8008);
		 in = reader();
		 out = writer();
	 }	

	 private UpToReader reader() throws UnsupportedEncodingException, IOException {
		 return new UpToReader(new InputStreamReader(server.getInputStream(), "UTF8"));
	 }
		
	 private Writer writer() throws UnsupportedEncodingException, IOException {
		 return new OutputStreamWriter(server.getOutputStream(), "UTF8");
	 }
	 
	 void send(String message) throws IOException {
		 if (out == null) {
			 connect();
		 }
		 out.append(message);
		 out.flush();
	 }
		 
	 public String readServerResponse() throws IOException {
		 return in.upTo(";;");
	 }
	 
	 
	 void closeSocket() throws IOException{
			send(new Logout().getMessage());
			server.close();
	 }
	 
	 boolean isConnected(){
		 return server.isConnected();
	 }
}
