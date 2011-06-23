package bupt.longlong.utils.thread;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import bupt.longlong.qunar.database.dataexport.SightExport;

public class FunctionThreadPoolTask implements Runnable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7403797216355001525L;
	private static Logger log = Logger.getLogger(FunctionThreadPoolTask.class);
	private String info;
	//    private BoundedJobBlockingQueue queue;
	private BlockingQueue<String> queue;

	////	ThreadLocal写日志
	//	private List<String> completeLog = Collections.synchronizedList(new ArrayList<String>());
	//	private static ThreadLocal<List<String>> threadLog = new ThreadLocal<List<String>>() {
	//
	//		@Override
	//		protected List<String> initialValue() {
	//			return new ArrayList<String>();
	//		}
	//	};

	//	private void log(String message) {
	//		threadLog.get().add(info + " " + message + " at " + new Date());
	//	}
	//
	//	private List<String> getLog() {
	//		return completeLog;
	//	}

	FunctionThreadPoolTask(String info, BlockingQueue<String> queue) {
		this.info = info;
		this.queue = queue;
	}

	public void run() {
		try {
			//单个线程的处理逻辑(需要的参数为element)
			//            ParentTree.unityPg(info,parentId);
			//            ParentTree.updateElement(info, parentId);
			//            Gis.dealpNode(info, parentId);
			//            PerfectImage.downloadAndName(info);
			SightExport.selectFromSight(info);

		} catch (Exception e) {
			log.error(info + "\t产生异常！", e);
		} finally {
			try {
				queue.take();
			} catch (InterruptedException ex) {
				log.error("序列出错。", ex);
			}
		}
	}

	public void setPool(FunctionThreadPool pool) {}
}
