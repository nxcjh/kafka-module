package com.autohome.kafka.core.upstream.imp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import kafka.producer.KeyedMessage;

import com.autohome.kafka.ha.FileOffsetObj;
import com.autohome.kafka.instrumentation.SourceCounter;
/**
 * 读取文件类 通过nio进行读取
 * 
 * @author yaogang
 *
 */
public class CursorForBatchList {
	  private static final Logger LOG = Logger.getLogger(CursorForBatchList.class);
	  File file=null;
	  //新缓冲区的位置将为零，其界限将为其容量，其标记是不确定的。无论它是否具有底层实现数组，其标记都是不确定的。 
	  ByteBuffer buf = ByteBuffer.allocateDirect(Short.MAX_VALUE);
	  private FileOffsetObj foo = null;
	  RandomAccessFile raf = null;
	  final BlockingQueue<List<kafka.producer.KeyedMessage<String, String>>> sync;
	  private  volatile boolean madeProgress = false;
	  FileChannel in = null;
	  private List<kafka.producer.KeyedMessage<String, String>> list = null;
	  private String fullfilename;
	  private String topic;
	  private int count = 0;
	  long offset = 0l;
	  
	  private SourceCounter sourceCounter;
	  
	  
	  public CursorForBatchList(String fullfilename,String topic,BlockingQueue<List<kafka.producer.KeyedMessage<String, String>>> sync,long offset){
		  this.sync = sync;//BlockingQueue<List<kafka.producer.KeyedMessage<String, String>>>
		  this.topic = topic;
		  this.offset = offset;
		  this.fullfilename = fullfilename;
		  this.file = new File(fullfilename);
		  
		  //配置文件属性OffsetObject
		  this.foo = new FileOffsetObj();
		  foo.setFullfileName(this.fullfilename);
		  foo.setOffset(this.offset);
		  foo.setOn(true);
		  try {
			init(this.offset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		  if (sourceCounter == null) {
		      sourceCounter = new SourceCounter("read");
		 }
		sourceCounter.start();
	  }
	 
	  public void init() throws IOException{
		  init(0);
		  
	  }
	  /**
	   * 读文件初始化
	   * @param offset
	   * @throws IOException
	   */
	  public void init(long offset) throws IOException{
		  raf = new RandomAccessFile(file,"r");
		  LOG.info(this.fullfilename+" offset is "+offset);
		  //直接将文件指针移动到offset后面
		  this.raf.seek(offset);
		  //// return the channel of the file
		  in = raf.getChannel();
		  
		  
	  }
	  
	  
	  
	  
	 /**
	  * 逐行解析读入的二进制文件
	  * @param buf
	  * @return
	  * @throws UnsupportedEncodingException
	  * @throws InterruptedException
	  */
	private boolean extractLines(ByteBuffer buf) throws UnsupportedEncodingException, InterruptedException{
		   madeProgress = false;
		  int start = buf.position();
		  buf.mark();// 标记结果
		  this.list = new ArrayList<kafka.producer.KeyedMessage<String, String>>();
		  while(buf.hasRemaining()){
		    	 byte b = buf.get();
		    	 if(b =='\n'||b=='~'){// ~符号是针对app数据的
//     	    	 if(b =='\n'){// ~符号是针对app数据的
		    		 int end = buf.position();
		    		 int sz = end - start;
		    		 byte[] body = new byte[sz - 1];
		    	     buf.reset(); // go back to mark
		    	     buf.get(body, 0, sz - 1); // read data body is fill with data
		    	     buf.get(); // read '\n'
		    	     buf.mark(); // new mark.
		    	     start = buf.position(); //读取完这一行之后重新定位、n位置。
		    	     list.add(new KeyedMessage<String, String>(topic,count+"",new String(body,"utf-8")));
		    	     count++;
		    	     if(count==100){
//		    	    	 	LOG.info("&& 100 send "+list.size());
		    	    	 	sync.put(list); 
		    	    	 	this.list = new ArrayList<kafka.producer.KeyedMessage<String, String>>();
		    	    	 	count=0;
		    	     }
		    		 madeProgress = true;
		    	 }
		    }
		  	sync.put(list); // 发送batch list
		  	
		    sourceCounter.incrementAppendBatchReceivedCount();
		    sourceCounter.addToEventReceivedCount(list.size());
		  	this.list = new ArrayList<kafka.producer.KeyedMessage<String, String>>();
		  	count=0;
		    buf.reset();
		    buf.compact();
		    return madeProgress;
	  }
      /**
       * 从Channel中 读数据	
       * @return
       * @throws IOException
       * @throws InterruptedException
       */
	  boolean readDataFromChannle() throws IOException, InterruptedException{
		int rd=0;
		boolean falge = false;
		if(buf.position()== buf.capacity()){// 当缓冲区数据没有 “\n” 或是 "~" 的情况下 造成无法读取数据，因此需要加入这样的考虑
			buf.clear();
		}
	    while ((rd = in.read(buf)) > 0) {
	    	offset+=rd;// 记录文件偏移量。
	    	//反转缓冲区。首先将限制设置为当前位置，然后将位置设置为 0。如果已定义了标记，则丢弃该标记。
	    	//常与compact方法一起使用。通常情况下，在准备从缓冲区中读取数据时调用flip方法。
		 	buf.flip();
		 	falge = extractLines(buf);
		 	foo.setOffset(offset);
	    }
	    return falge;
	  }
	  /**
	   * 读文件
	   * @return offset file
	 * @throws InterruptedException 
	 * @throws IOException 
	   */
	  public boolean tailReadFile() throws IOException, InterruptedException{
		  
			  if(in==null){
				  LOG.error(" file read channel is not init");
			  }
			return readDataFromChannle();
		
		 
	  }
	  public void flush(){
		  if (raf != null) {
		      try {
		        raf.close(); // release handles
		      } catch (IOException e) {
		        LOG.error("problem closing file " + e.getMessage(), e);
		      }
		    } 
		  buf.flip(); // buffer consume mode
		  int remaining = buf.remaining();
		  if (remaining > 0) {
			   byte[] body = new byte[remaining];
			   buf.get(body, 0, remaining);
		  }
		  in = null;
		  buf.clear();
	  }
	  public long getOffset(){
		  return this.offset;
	  }
	
	  public String getFullfilename() {
		return fullfilename;
	}

	public void setFullfilename(String fullfilename) {
		this.fullfilename = fullfilename;
	}

	public void close() throws IOException{
		    if (raf != null) {
		        try {
		          raf.close(); // release handles
		        } catch (IOException e) {
		          LOG.error("problem closing file " + e.getMessage(), e);
		        }
		      }
		      offset = 0;
		      in = null;
		      buf.clear();
		    }

	public FileOffsetObj getFoo() {
		return foo;
	}
}
