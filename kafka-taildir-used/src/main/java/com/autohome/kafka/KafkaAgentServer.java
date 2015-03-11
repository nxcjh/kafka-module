package com.autohome.kafka;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import org.apache.log4j.Logger;
import com.autohome.kafka.comm.ParamterObj;
import com.autohome.kafka.core.StateMechine;
import com.autohome.kafka.core.downstream.SendProxy;
import com.autohome.kafka.core.downstream.SendRunnalbeProxy;
import com.autohome.kafka.core.downstream.imp.KafkaProduceSendListImp;
import com.autohome.kafka.core.periodic.PeriodicRunnable;
import com.autohome.kafka.core.upstream.TailFile;
import com.autohome.kafka.core.upstream.imp.CursorForBatchList;
import com.autohome.kafka.ha.DumpMeta;
import com.autohome.kafka.tools.RegexFileFilter;
import com.autohome.kafka.watch.DirWatcher;
/**
 * 服务提供这，初始化资源，线程池。
 * //		Runtime.getRuntime().addShutdownHook(null);
	// 创建代理 创建runable代理，创建线程池，启动线程，注册推出清理操作，java异常推出时候是不是执行hook任务
 * @author yaogang
 *
 */
public class KafkaAgentServer {
	private static final Logger LOG = Logger.getLogger(KafkaAgentServer.class);
	private BlockingQueue<List<kafka.producer.KeyedMessage<String, String>>> sync = new SynchronousQueue<List<kafka.producer.KeyedMessage<String, String>>>(true);
	private PeriodicRunnable dirProxy= null;
	private PeriodicRunnable haProxy= null;
	private PeriodicRunnable tailProxy= null;
	private SendRunnalbeProxy<List<kafka.producer.KeyedMessage<String, String>>>  sendProxy = null;	// 初始化资源
	private ParamterObj param = null;
	private TailFile tf = null;
	private DumpMeta dm = null;
	private DirWatcher dirWatcher= null;
	private SendProxy sp = null;
	private StateMechine st = null;
	@SuppressWarnings("unchecked")
	private final ExecutorService exec = Executors.newFixedThreadPool(4);
	public KafkaAgentServer(final ParamterObj param,Properties prop){
		st = new StateMechine();
		
		this.tf = new TailFile(0,param,st);
		//把state 设置为TRUE
		this.dm = new DumpMeta(st,tf);
		this.param = param;
		//文件夹 监控
		this.dirWatcher=new DirWatcher(param.getDir(), new RegexFileFilter(param.getPattfile()), 30000l).addHandler(
				new com.autohome.kafka.watch.DirChangeHandler(){
					 Map<String, CursorForBatchList> curmap = new ConcurrentHashMap<String, CursorForBatchList>();
					
					 public void fileDeleted(File f) {
						LOG.info("getFullFileName(f) "+getFullFileName(f));
						
						if(curmap.get(getFullFileName(f))!=null){
							tf.removeCourse(curmap.get(getFullFileName(f)));
						}
						curmap.remove(getFullFileName(f));
					}
					 
					/**
					 * 创建一个CursorForBatchList,读取nio, 放入 Map<String, CursorForBatchList> curmap 中
					 */
					public void fileCreated(File f) {
						
						if(tf.getFileMeta().get(getFullFileName(f))==null){
							CursorForBatchList cfb = new CursorForBatchList(getFullFileName(f),param.getTopic(),sync,0);
							curmap.put(getFullFileName(f), cfb);
							//在TailFile 读文件中加入该CursorForBatchList
							tf.addCourse(cfb);
						}
					}
				});
		//kafka send 代理
		sp = new SendProxy<List<kafka.producer.KeyedMessage<String, String>>>();
		KafkaProduceSendListImp ksi = new KafkaProduceSendListImp(prop);
		//producer 初始化, 获得producer实例
		ksi.init();
		//把 producer实例 交给 代理
		sp.setRealSend(ksi);
		
	}
	public String getFullFileName(File f){
		if(param.getDir().endsWith("/")){
			return param.getDir()+f.getName();
		}else{
			return param.getDir()+"/"+f.getName();
		}
	}
	public void reinit() throws ClassNotFoundException, IOException{
		tf.reConstructorCursors(sync);
		init();
		
	}
	public void init(){
		sendProxy = new SendRunnalbeProxy<List<kafka.producer.KeyedMessage<String, String>>>(sp,sync);
		dirProxy = new PeriodicRunnable(dirWatcher);
		tailProxy =  new PeriodicRunnable(tf);
		haProxy = new PeriodicRunnable(dm);
	}
	
	public void start(){	
		LOG.info(" server is start ... ");
		exec.execute(sendProxy);
		exec.execute(dirProxy);
		exec.execute(tailProxy);
		exec.execute(haProxy);
		
	}
	public void stop(){
		exec.shutdown();
	}
	public void addShutdownHook(){
		
	}
	
	

}
