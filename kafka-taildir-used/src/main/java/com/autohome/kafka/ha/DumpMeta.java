package com.autohome.kafka.ha;


import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.autohome.kafka.core.StateMechine;
import com.autohome.kafka.core.periodic.PeriodicObject;
import com.autohome.kafka.core.upstream.TailFile;
import com.autohome.kafka.conf.Configuration;
import com.autohome.kafka.tools.FileTools;
public class DumpMeta implements PeriodicObject{
	private static final Logger LOG = Logger.getLogger(DumpMeta.class);
	private  TailFile tf = null;
	private StateMechine st = null;
	
	public DumpMeta(){
		st.setMadeProgress(true);
	}
	public DumpMeta(final TailFile tf){
		this.tf = tf;
	}
	public DumpMeta(StateMechine st,final TailFile tf){
		this.tf = tf;
		this.st = st;
		this.st.setMadeProgress(true);
	}
	/**
	 *  异常在这里进行处理
	 * ，因为线程之间的一场不能够抛出到其他线程去处理。
	 * 新的文件写入到临时文件中，写入成功后复制
	 */
	public void periodic() {
		try {
			// 写入之前先要判断
			if(st.isMadeProgress()){
				st.setMadeProgress(false);
				FileTools.writeObject(this.tf.getFileMeta(),Configuration.FILE_HA_META+Configuration.HA_META_FILENAME_TMP);
				FileTools.renamefile(Configuration.FILE_HA_META+Configuration.HA_META_FILENAME_TMP,Configuration.FILE_HA_META+Configuration.HA_MEAT_FILENAME);
				LOG.info("meta is ok, new read ok");
			}else{
				Iterator it = this.tf.getFileMeta().keySet().iterator();
				LOG.error("*****one min  not set madeprogress ,current offset is**********");
				while(it.hasNext()){
					FileOffsetObj fo = this.tf.getFileMeta().get(it.next());
					
					LOG.info(fo.getFullfileName()+"\t"+fo.getOffset());
				}
				LOG.error("***************************");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
/**
 * sleep time 5mint
 */
	public long sleep_ms() {
		// TODO Auto-generated method stub
		return 10000*6;
	}
	
}
