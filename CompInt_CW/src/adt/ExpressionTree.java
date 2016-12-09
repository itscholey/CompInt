package adt;

import java.util.Iterator;

public class ExpressionTree<T extends Comparable<T>> implements Iterable<T> {
	private TreeNode<T> root;
	private int size;

	public ExpressionTree()
	{
		root = null;
		size = 0;
	}
	
	public ExpressionTree(T element)
	{
		root = new TreeNode<T>(element);
		size = 1;
	}
	
	public void add(T elem)
	{
		root = add(elem, root);
		size++;
	}
	
	protected TreeNode<T> add(T elem, TreeNode<T> node)
	{
		if (node == null)
		{
			return new TreeNode<T>(elem);
		}
		
		int num = elem.compareTo(node.element);
		if (num < 0) 
		{
			node.left = add(elem, node.left);
		}
		else
		{
			node.right = add(elem, node.right);
		}
		return node;
	}
	
	
	public int getSize()
	{
		return size;
	}
	
	public int height()
	{
		return height(root);
	}
	
	protected int height(TreeNode<T> node)
	{
		if (node == null)
		{
			return 0;
		}
		else
		{
			return 1 + Math.max(height(node.left), height(node.right));
		}
	}
	
	public String toString()
	{
		String tree = "[ ";
		tree += toString(root);
		tree += " ]";
		return tree;
	}
	
	protected String toString(TreeNode<T> node)
	{
		if (node == null)
		{
			return "";
		}
		else
		{
			return toString(node.left) + " " + node.element.toString() + " " + toString (node.right);
		}
	}
	
	public T find(T elem)
	{
		TreeNode<T> tmp = find(elem, root);
		if (tmp == null)
		{
			return null;
		}
		else
		{
			return tmp.element;
		}
	}
	
	protected TreeNode<T> find(T elem, TreeNode<T> node)
	{
		if (node == null)
		{
			return null;
		}
		
		
		int num = elem.compareTo(node.element);
		if (num < 0)
		{
			return find(elem, node.left);
		}
		else if (num > 0)
		{
			return find(elem, node.right);
		}
		else
		{
			return node;			
		}
	}
	
	public void clear()
	{
		root = null;
		size = 0;
	}
	
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	protected static class TreeNode<E> 
	{
		private E element;
		private TreeNode<E> left;
		private TreeNode<E> right;

		private TreeNode(E element) 
		{
			this.element = element;
			left = null;
			right = null;
		}

		public TreeNode(E element, TreeNode<E> left, TreeNode<E> right) 
		{
			this.element = element;
			this.left = left;
			this.right = right;
		}

		public boolean isNumber() {
			return left == null && right == null;
		}
		
		

	}

}
