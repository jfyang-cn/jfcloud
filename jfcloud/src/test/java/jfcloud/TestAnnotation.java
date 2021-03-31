package jfcloud;

import java.lang.reflect.Field;
import java.util.List;

import org.assertj.core.util.Arrays;

import com.jfyang.jfcloud.jobpool.Job;
import com.jfyang.jfcloud.rocksdb.Index;

public class TestAnnotation {
	
    public static void main(String[] args) {
    	List<Object> fields = Arrays.asList(Job.class.getDeclaredFields());
    	fields.forEach(e -> {
    		Field f = (Field)e;
    		if (f.isAnnotationPresent(Index.class)) {
    			System.out.println(f.getName());
    		}
    	});
    }
}
