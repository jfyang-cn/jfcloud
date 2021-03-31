package jfcloud;

import java.lang.reflect.Field;
import java.util.List;

import org.assertj.core.util.Arrays;

import com.jfyang.jfcloud.jobpool.Job;
import com.jfyang.jfcloud.rocksdb.Index;
import com.jfyang.jfcloud.rocksdb.RocksDao;

public class TestGenerics {

    public static void main(String[] args) {
    	
    	RocksDao dao = new RocksDao<Job>(Job.class, null, null);

    	Job job = new Job();
    	job.setId("111111");
    	job.setRef("22222");
    	job.setStatus(0);
    	dao.put("a", job);
    	
//    	List<Object> fields = Arrays.asList(Job.class.getDeclaredFields());
//    	fields.forEach(e -> {
//    		Field f = (Field)e;
//    		if (f.isAnnotationPresent(Index.class)) {
//    			System.out.println(f.getName());
//    		}
//    	});
    }
}
