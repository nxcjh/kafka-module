package com.autohome.kafka.core.upstream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.autohome.kafka.comm.ParamterObj;
import com.autohome.kafka.conf.Configuration;
import com.autohome.kafka.core.StateMechine;
import com.autohome.kafka.core.periodic.PeriodicObject;
import com.autohome.kafka.core.upstream.imp.CursorForBatchList;
import com.autohome.kafka.ha.FileOffsetObj;
import com.autohome.kafka.tools.FileTools;

public class TailFile implements PeriodicObject {
	private static final Logger LOG = Logger.getLogger(TailFile.class);
	final List<CursorForBatchList> cursors = new ArrayList<CursorForBatchList>();
	private final List<CursorForBatchList> newCursors = new ArrayList<CursorForBatchList>();
	private final List<CursorForBatchList> rmCursors = new ArrayList<CursorForBatchList>();
	private Map<String,FileOffsetObj> metaMap = null;
	private boolean madeProgress = false;
	private ParamterObj param = null;
	private long sleep_ms;
	private StateMechine st = null;
	
	public TailFile(){
		this(0);
	}
	public TailFile(long sleep_ms){
		this.sleep_ms = sleep_ms;
		metaMap = new ConcurrentHashMap<String,FileOffsetObj>();// 恢复map file 
		
	}
	public TailFile(long sleep_ms,ParamterObj param){
		this(0);
		this.param = param;
	}
	public TailFile(long sleep_ms,ParamterObj param,StateMechine st){
		this(0,param);
		this.st = st;
	}
	
	/**
	 * 定其进行的任务在这调度
	 */
	public void periodic() {
			this.sleep_ms = 0;
			 synchronized (newCursors) {
		            cursors.addAll(newCursors);
		            newCursors.clear();
		          }
		          synchronized (rmCursors) {
		        	//删除游标
		            cursors.removeAll(rmCursors);
		            for (CursorForBatchList c : rmCursors) {
		            	//从meta中删除不用的游标
		            	metaMap.remove(c.getFullfilename());
		            	c.flush();
		              LOG.info("Remove Cursor from NowCursors :"+c.getFullfilename());
		              try {
						c.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		           }
		            rmCursors.clear();
		          }
		          madeProgress = false;
		          for (CursorForBatchList c : cursors) {
		            try {
						if (c.tailReadFile()) {
							// 更新offset偏移两
							
						  if(metaMap.get(c.getFullfilename())!=null){
							  metaMap.get(c.getFullfilename()).setOffset(c.getOffset());
							  LOG.info(c.getFullfilename()+"="+c.getFoo().getOffset());
							  st.setMadeProgress(true);
						  }
						  madeProgress = true;
						}else{
							 if(metaMap.get(c.getFullfilename())!=null){
								 metaMap.get(c.getFullfilename()).setOn(false);
							 }
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
		          }
		          if (!madeProgress) {// 如果某次没有读取到数据 那么进行休眠
						this.sleep_ms = 1500;
		          }    
		
		
	}

	public long sleep_ms() {
		return this.sleep_ms;
	}
	public TailFile addHandler(TailFileHandler tfh){
		return this;
	}
	/**
	 * 暴露mata信息给外部对象，用于重启服务时候数据定位。
	 * @return
	 */
	public synchronized Map<String,FileOffsetObj> getFileMeta(){
		return this.metaMap;
	}
	
	/**
	 * 添加读取文件 的NIO对象
	 * @param cursor
	 */
	public synchronized void addCourse(CursorForBatchList cursor){
		//Map<String, FileOffsetObj> metaMap
		if(this.metaMap.containsKey(cursor.getFullfilename())){
			return;
		}else{
			//filename  FileOffsetObj实例
			this.metaMap.put(cursor.getFullfilename(),cursor.getFoo() );
		}
		
		 if (Thread.currentThread() == null) {
			 LOG.info(" Thread currentThead is null");
		      cursors.add(cursor);
		      LOG.debug("Unstarted Tail has added cursor: " + cursor.getFullfilename());
		    } else {
		      synchronized (newCursors) {
		        newCursors.add(cursor);
		      }
		      LOG.debug("Tail added new cursor to new cursor list: "+ cursor.getFullfilename());
		    }
	}
	
	
	public synchronized void removeCourse(CursorForBatchList cursor){
		if(this.metaMap.containsKey(cursor.getFullfilename())){
			this.metaMap.remove(cursor.getFullfilename());
		}
		 if (Thread.currentThread() == null) {
			 LOG.info(" Thread currentThead is null");
		      cursors.remove(cursor);
		    } else {
		      synchronized (rmCursors) {
		        rmCursors.add(cursor);
		        LOG.info(" rm cursors "+cursor.getFullfilename());
		      }
		    }
	}
	public synchronized void reConstructorCursors(BlockingQueue<List<kafka.producer.KeyedMessage<String, String>>> sync) 
			throws ClassNotFoundException, IOException{
		Map map = FileTools.readObject(Configuration.FILE_HA_META+Configuration.HA_MEAT_FILENAME);
		LOG.info("meta:"+Configuration.FILE_HA_META+Configuration.HA_MEAT_FILENAME+":::"+map.toString());
		cursors.clear();
		
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			String tmp = (String) it.next();
			FileOffsetObj foo = (FileOffsetObj) map.get(tmp);
			if(new File(tmp).exists()){
				CursorForBatchList cursor=	new CursorForBatchList(tmp,param.getTopic(),sync,foo.getOffset());
				if(new File(cursor.getFullfilename()).exists()){
					LOG.info("reconstructor "+cursor.getFullfilename());
					cursors.add(cursor);
					this.metaMap.put(cursor.getFullfilename(),cursor.getFoo());
				}else{
					LOG.info("not exist "+cursor.getFullfilename());
				}
			}
		}
	}
	/**
	 * 得到是否读数据的状态
	 * @return
	 */
	public synchronized boolean getProgress(){
		return this.madeProgress;
	}

}
