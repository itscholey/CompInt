package adt;

import java.util.ArrayList;

public class DataSet<T> {
	
	private ArrayList<T[]> ds;
	
	public DataSet()
	{
		ds = new ArrayList<T[]>();
	}
	
	public void add(T[] element)
	{
		ds.add(element);
	}
	
	public void remove(int element)
	{
		ds.remove(element);
	}
	
	/**
	 * Return the size of each entry in the data set.
	 * @return
	 */
	public int getDataLength()
	{
		return ds.get(0).length;
	}
	
	public T[] getData(int index)
	{
		return ds.get(index);
	}
	
	/**
	 * Return how many entries are in the data set.
	 * 
	 * @return
	 */
	public int size()
	{
		return ds.size();
	}
	
	public String toString()
	{
		String result = "";
		int max;
		if (ds.get(0) instanceof Double[]) {
			max = 5;
		}
		else {
			max = 0;
		}
		
		
		for (int i = 0; i < ds.size(); i++)
		{
			result += "[ ";
			for (int j = 0; j < ds.get(i).length; j++)
			{
				result += ds.get(i)[j] + "  ";
				int length = String.valueOf(ds.get(i)[j]).length();
				if (j < ds.get(i).length-1)
				{
					for (int pad = length; pad <= max; pad++)
					{
						result += " ";
					}
				}
			}
			result += "]\n";
		}
		
		return result;
	}
}
