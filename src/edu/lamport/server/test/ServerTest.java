package edu.lamport.server.test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import edu.lamport.server.*;

public class ServerTest {
	public static void allFilesTest() {
		ServerUtil su = ServerUtil.getInstance();
		String path = Paths.get(File.separator,"Users","anandb","eclipse","Lamport-Mutex","server-roots","server-1").toString();
		List<String> files = su.getAllFiles(path);
		Iterator<String> it = files.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
	}
	
	public static void readFileTest() {
		ServerUtil su = ServerUtil.getInstance();
		String path = Paths.get(File.separator,"Users","anandb","eclipse","Lamport-Mutex","server-roots","server-1","file-1").toString();
		String files = su.readFile(path);
		System.out.println(files);
	}
	
	public static void writeFileTest() {
		ServerUtil su = ServerUtil.getInstance();
		String path = Paths.get(File.separator,"Users","anandb","eclipse","Lamport-Mutex","server-roots","server-1","file-1").toString();
		su.appendToFile(path,"Balan");
	}
}
