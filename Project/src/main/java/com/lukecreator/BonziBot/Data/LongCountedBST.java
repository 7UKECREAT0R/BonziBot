package com.lukecreator.BonziBot.Data;

/**
 * 64-bit BST with Node counts.
 * @author Lukec
 */
public class LongCountedBST {
	
	public class Node {
		long value;
		int count;
		Node a, b;
		
		private Node(long value) {
			this.value = value;
		}
		private Node(long value, Node left, Node right) {
			this.value = value;
			this.a = left;
			this.b = right;
		}
	}
	
	public LongCountedBST() {
		rootNode = null;
	}
	public LongCountedBST(long root) {
		rootNode = new Node(root);
	}
	public void put(long number) {
		if(this.rootNode == null)
			this.rootNode = new Node(number);
		else
			_put(this.rootNode, number);
	}
	void _put(Node node, long number) {
		if(node.value == number) {
			node.count++;
			return;
		}
		if(number > node.value) {
			if(node.b == null) {
				node.b = new Node(number);
				node.b.count = 1;
				return;
			}
			_put(node.b, number);
		} else {
			if(node.a == null) {
				node.a = new Node(number);
				node.a.count = 1;
				return;
			}
			_put(node.a, number);
		}
	}
	
	public int has(long number) {
		return _has(this.rootNode, number);
	}
	int _has(Node node, long number) {
		if(node == null)
			return 0;
		if(node.value == number)
			return node.count;
		if(number > node.value) {
			if(node.b != null)
				return node.b.count;
			return _has(node.b, number);
		} else {
			if(node.a != null)
				return node.a.count;
			return _has(node.a, number);
		}
	}
	
	public void remove(long number) {
		this.rootNode = _remove(this.rootNode, number);
	}
	Node _remove(Node node, long number) {
		if(node == null)
			return null;
		
		if(node.value == number) {
			node.count--;
			if(node.count > 0)
				return node;
			else return null;
		}
		
		if(number > node.value)
			node.b = _remove(node.b, number);
		else
			node.a = _remove(node.a, number);
		
		return node;
	}
	
	public Node rootNode;
}
