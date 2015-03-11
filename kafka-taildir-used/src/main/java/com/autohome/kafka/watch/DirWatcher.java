package com.autohome.kafka.watch;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.autohome.kafka.core.periodic.PeriodicObject;
import com.autohome.kafka.tools.FileFilter;
/**
 * 文件夹监听，
 * @author root
 *
 */
public class DirWatcher implements PeriodicObject {
	static final Logger LOG = Logger.getLogger(DirWatcher.class);
	 private File dir;
	 private Set<File> previous = new HashSet<File>();
	 private FileFilter filter;
	 private DirChangeHandler dirChangeHandler = null;
	 private long sleep_ms = 0;
	 public DirWatcher(String fileDir,FileFilter filter,long sleep_ms){
		 this.dir = new File(fileDir);
		 this.filter = filter;
		 this.sleep_ms = sleep_ms;
	 }
	
	 public DirWatcher addHandler(DirChangeHandler dirChangeHandler2){
		 this.dirChangeHandler = dirChangeHandler2;
		 return this;
	 }
	 /**
	  * 检查文件夹下的文件，增减 进行相应的事件触发
	  */
	 public void check(){
		 File[] files = dir.listFiles();
		 
		 if (files == null) {
			 LOG.error(" the watched dir: "+dir.getAbsolutePath()+" is no exist ");
		 }
		  Set<File> newfiles = new HashSet<File>(Arrays.asList(files));
		   Set<File> addedFiles = new HashSet<File>(newfiles);
		    addedFiles.removeAll(previous);
		    for (File f : addedFiles) {
		    	//如果该文件不是目录并且符合正则
		        if (!f.isDirectory() && filter.isSelected(f)) {
		        	
		          fireCreatedFile(f);
		        } else {
		          newfiles.remove(f); // don't keep filtered out files
		        }
		      }
		      // figure out what was deleted
		      Set<File> removedFiles = new HashSet<File>(previous);
		      removedFiles.removeAll(newfiles);
		      for (File f : removedFiles) {
		        fireDeletedFile(f);
		      }
		      previous = newfiles;
		    }
	 public void fireCreatedFile(File f){
		 if(dirChangeHandler==null){
			 return ;
		 }
		 dirChangeHandler.fileCreated(f);
	 }
	 public void fireDeletedFile(File f){
		 if(dirChangeHandler==null){
			 return ;
		 }
		 dirChangeHandler.fileDeleted(f);
	 }

	 /**
	  * 定期执行的方法
	  */
	public void periodic() {
		this.check();
		
	}

	public long sleep_ms() {
		// TODO Auto-generated method stub
		return this.sleep_ms;
	}
	
}
