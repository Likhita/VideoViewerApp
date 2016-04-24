package com.example.videoapp;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class UpToReader extends FilterReader {

	UpToReader(Reader in) {
		super(in);
	}

	//upTo(String) reads the message up to string ";;"
	String upTo(String end) throws IOException {
		String buffer= new String();
		String output;
		String nextWord="";
		while (!buffer.endsWith(end)){
				nextWord=upTo(';');
				buffer=buffer+nextWord;
		}
		output=buffer.toString(); 
		return output;
	}
		
	//upTo(char) reads the message up to character ';'  
	private String upTo(char end) throws IOException{
		int EOF=-1;
		StringBuffer buffer= new StringBuffer();
		String output;
		int nextChar;
		while ((nextChar=super.read())!=EOF){
			if (nextChar == end ){
				buffer.append((char)nextChar);
				break;
			}
			buffer.append((char)nextChar);
		}
		output=buffer.toString(); 
		return output;
	}
}

