package edu.lamport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class LamportUtil {
	public static int CLIENT_SERVER_CONN = 1;
	public static int PEER_TO_PEER_CONN = 2;
	
	public static String READ_CMD = "read";
	public static String WRITE_CMD = "write";
	public static String ENQUIRE_CMD = "enquire";
	public static String TERMINATE_CMD = "terminate";
	public static String REQUEST_CMD = "request";
	public static String RELEASE_CMD = "release";
	public static String REPLY_CMD = "reply";
	
	public static String SUCCESS = "success";
	public static String FAILURE = "failure";
	public static String[] COMMANDS = {LamportUtil.READ_CMD, LamportUtil.WRITE_CMD};
	
	public static void write(BufferedWriter bw, String message) throws IOException { 
		bw.write(message);
		bw.newLine();
		bw.flush();
	}
	public static String read(BufferedReader br) throws IOException { 
		return br.readLine();
	}
}
