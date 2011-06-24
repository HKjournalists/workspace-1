package bupt.longlong.utils.thread;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TreadPoolTest extends TestCase {

    public void testThreadPool() {
        List<String> elementList = new ArrayList<String>();
        // 任务参数列表赋值
        ThreadPool pool = new ThreadPool(elementList);
        pool.execute();
    }
}
