package com.jfyang.jfcloud.rocksdb;

public class Pair<K, V> {

	private K left;
	private V right;
	
	public Pair(K left, V right) {
		super();
		this.left = left;
		this.right = right;
	}

	public K getLeft() {
		return left;
	}

	public V getRight() {
		return right;
	}
}
