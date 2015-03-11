package com.autohome.kafka.tools;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.autohome.kafka.conf.Configuration;

public class FileTools {
	public static void writeObject(Map filemeta,String filepath) throws FileNotFoundException{
		  try {
		   FileOutputStream out=new FileOutputStream(new File(filepath));
		   ObjectOutputStream objOut=new ObjectOutputStream(out);
		    objOut.writeObject(filemeta);
		    objOut.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
		 }
	 public static Map readObject(String filepath) throws ClassNotFoundException, IOException{
		   try {
		    FileInputStream out=new FileInputStream(new File(filepath));
		    ObjectInputStream objOut=new ObjectInputStream(out);
		    Map map = (Map) objOut.readObject();
		    objOut.close();
		    return map;
		   } catch (EOFException  e) {
			   
		   }
		return null;
		      
		 }
	 public static boolean renamefile(String fileFrom,String fileTo){
		 File f = new File(Configuration.FILE_HA_META+Configuration.HA_META_FILENAME_TMP);
		 try{
			 f.renameTo(new File(Configuration.FILE_HA_META+Configuration.HA_MEAT_FILENAME));
			
		 }catch(Exception e){
			 return false;
		 }
		 return true;
	 }
	 private static boolean copy(String fileFrom, String fileTo) {  
		 
	        try {  
	        	if(!new File(fileFrom).exists()){
	        		return false;
	        	}
	            FileInputStream in = new java.io.FileInputStream(fileFrom);  
	            FileOutputStream out = new FileOutputStream(fileTo);  
	            byte[] bt = new byte[1024];  
	            int count;  
	            while ((count = in.read(bt)) > 0) {  
	                out.write(bt, 0, count);  
	            }  
	            in.close();  
	            out.close();  
	            return true;  
	        } catch (IOException ex) {  
	            return false;  
	        }  
	    }  
}
