package com.jfyang.jfcloud.rocksdb;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksdbImpl {
	
	static Logger logger = LoggerFactory.getLogger(RocksdbImpl.class.getName());

	private RocksDB db = null;
	private Map<String, ColumnFamilyHandle> tableList = null;
	private Charset charset = Charset.forName("utf-8");
	
	public RocksdbImpl() {
		tableList = new LinkedHashMap<>();
		RocksDB.loadLibrary();
	}
	
	public int initDb(String dbPath) {
		
		dbPath = StringUtils.appendIfMissing(dbPath, "/");

		logger.info("db init from {}", dbPath);
		
		try {
			Path path = Paths.get(dbPath);
	        if (!Files.exists(path)){
	        	logger.info("[RocksdbImpl] rocksdb is create");
				Files.createDirectories(path);
	        }
		} catch (IOException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		
		try {
			Options options = new Options();
			options.setCreateIfMissing(true);
			List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();
			List<byte[]> cfs = RocksDB.listColumnFamilies(options, dbPath);
			if (cfs.size() > 0) {
				for (byte[] cf : cfs) {
					columnFamilyDescriptors.add(new ColumnFamilyDescriptor(cf, new ColumnFamilyOptions()));
				}
			} else {
				columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
			}

			List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();
			DBOptions dbOptions = new DBOptions();
			dbOptions.setCreateIfMissing(true);

			db = RocksDB.open(dbOptions, dbPath, columnFamilyDescriptors, columnFamilyHandles);
			
			columnFamilyHandles.stream().forEach(k -> {				
				try {					
					String table = new String(k.getName());					
					tableList.put(table, k);
				} catch (Exception e) {
					StringWriter error = new StringWriter();
					e.printStackTrace(new PrintWriter(error));
					logger.error(error.toString());
				}
			});			
			
			logger.info("tables="+tableList.keySet());

		} catch (Exception e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return 0;
	}
	
	public void closeDb() {
		db.close();
	}
	
	public int createTable(String tableName) {
		
		if (tableList.containsKey(tableName)) {
			return 0;
		}
		
		try {
			ColumnFamilyHandle columnFamilyHandle = db.createColumnFamily(
					new ColumnFamilyDescriptor(tableName.getBytes(), new ColumnFamilyOptions()));
			tableList.put(tableName, columnFamilyHandle);
			logger.info("create table " + tableName);
		} catch (RocksDBException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return 0;
	}
	
	public int dropTable(String tableName) {
		
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return -1;
			}

			ColumnFamilyHandle columnFamilyHandle = tableList.get(tableName);
			db.dropColumnFamily(columnFamilyHandle);
			tableList.remove(tableName);			
			
		} catch (RocksDBException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return 0;
	}
	
	public List<String> getTables() {
		List<String> tables = new ArrayList<String>();
		tableList.forEach((k,v) -> {				
			tables.add(k);
		});
		return tables;
	}
	
	public void buildTableIndex(String tableName, RocksDataInterface dao) {
		RocksIterator iter = null;
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return;
			}

			ReadOptions readOptions = new ReadOptions();
			readOptions.setTotalOrderSeek(true);
			readOptions.setPrefixSameAsStart(true);
			iter = db.newIterator(tableList.get(tableName), readOptions);

			for (iter.seekToFirst(); iter.isValid(); iter.next()) {
				String data = new String(iter.value());
				String key = new String(iter.key());
				if (data!=null && !data.isEmpty())
					dao.buildIndex(key, data);
			}
			
			iter.close();
		} catch (Exception e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
	}

	public int putValue(String tableName, String key, String value) {
		
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return -1;
			}
			
			ColumnFamilyHandle columnFamilyHandle = tableList.get(tableName);
			db.put(columnFamilyHandle, key.getBytes(charset), value.getBytes(charset));			
			
		} catch (RocksDBException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return 0;
	}

	public String getValue(String tableName, String key) {
		byte[] value = null;
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return null;
			}
			
			ColumnFamilyHandle columnFamilyHandle = tableList.get(tableName);
			value = db.get(columnFamilyHandle, key.getBytes(charset));
			if (value == null) {
				return null;
			}			
		} catch (RocksDBException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		ByteBuffer buf = ByteBuffer.wrap(value);
        CharBuffer cBuf = charset.decode(buf);
		return cBuf.toString();
	}
	
	public List<String> getValues(String tableName, Set<String> keys) {
		List<String> ret = new ArrayList<String>();
		
		if (!tableList.containsKey(tableName)) {
			logger.error("table {} isn't exist!", tableName);
			return null;
		}
		
		ColumnFamilyHandle columnFamilyHandle = tableList.get(tableName);
		keys.forEach(key -> {
			byte[] value = null;
			try {
				value = db.get(columnFamilyHandle, key.getBytes(charset));
				if (value != null) {
					ByteBuffer buf = ByteBuffer.wrap(value);
			        CharBuffer cBuf = charset.decode(buf);
					ret.add(cBuf.toString());
				}
			} catch (RocksDBException e) {
				StringWriter error = new StringWriter();
				e.printStackTrace(new PrintWriter(error));
				logger.error(error.toString());
			}
		});
		
		return ret;
	}
	
	public int delValue(String tableName, String key) {
		try {
			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return -1;
			}
			ColumnFamilyHandle columnFamilyHandle = tableList.get(tableName);
			db.delete(columnFamilyHandle, key.getBytes(charset));
		} catch (RocksDBException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return 0;
	}
	
	public Map<String,String> getTableData(String tableName) {
		RocksIterator iter = null;
		Map<String,String> map = new LinkedHashMap<>();
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return map;
			}

			ReadOptions readOptions = new ReadOptions();
			readOptions.setTotalOrderSeek(true);
			readOptions.setPrefixSameAsStart(true);
			iter = db.newIterator(tableList.get(tableName), readOptions);

			for (iter.seekToFirst(); iter.isValid(); iter.next()) {
				String data = new String(iter.value());
				map.put(new String(iter.key()), data);
			}
			
			iter.close();
		} catch (Exception e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}

		return map;
	}
	
	public Map<String, String> seek(String tableName, String key){
		RocksIterator iter = null;
		Map<String,String> map = new LinkedHashMap<>();
		
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return map;
			}

			ReadOptions readOptions = new ReadOptions();
			readOptions.setTotalOrderSeek(true);
			readOptions.setPrefixSameAsStart(true);
			iter = db.newIterator(tableList.get(tableName), readOptions);

			for (iter.seek(key.getBytes(charset)); iter.isValid(); iter.next()) {
				
	            String k = new String(iter.key());
	            if (!k.startsWith(key))
	            	break;

				String data = new String(iter.value());
				map.put(new String(iter.key()), data);
			}
			
			iter.close();
		} catch (Exception e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return map;
	}
	
	public Pair<String, String> first(String tableName, String key) {
		RocksIterator iter = null;
		Pair<String, String> ret = null;
		try {

			if (!tableList.containsKey(tableName)) {
				logger.error("table {} isn't exist!", tableName);
				return ret;
			}

			ReadOptions readOptions = new ReadOptions();
			readOptions.setTotalOrderSeek(true);
			readOptions.setPrefixSameAsStart(true);
			iter = db.newIterator(tableList.get(tableName), readOptions);

			iter.seek(key.getBytes(charset)); 
			if (iter.isValid()) {
				
	            String k = new String(iter.key());
	            if (!k.startsWith(key))
	            	return ret;

				String data = new String(iter.value());
				ret = new Pair<String, String>(new String(iter.key()), data);
			}
			
			iter.close();
		} catch (Exception e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return ret;
	}
}
