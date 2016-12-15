package cw;

import java.util.ArrayList;

/**
 * An Abstract Data Type to model a set of data that the program is based on.
 * 
 * @author Chloe Barnes, 139006412
 * @version CS3910 Computational Intelligence Logistics Coursework
 * @version 15th December 2016
 * 
 * @param <T> The type the Data Set contains.
 */
public class DataSet<T> {
	// Store the data
	private ArrayList<T[]> ds;
	
	/**
	 * Construct a DataSet object and initialise the storage.
	 */
	public DataSet() {
		ds = new ArrayList<T[]>();
	}
	
	/**
	 * Add an element to the Data Set.
	 * 
	 * @param element The element to add.
	 */
	public void add(T[] element) {
		ds.add(element);
	}
	
	/**
	 * Remove an element from the Data Set.
	 * 
	 * @param element The index of the element to remove.
	 */
	public void remove(int element) {
		ds.remove(element);
	}
	
	/**
	 * Return the size of each entry in the data set.
	 * 
	 * @return The length of what is contained in the data set.
	 */
	public int getDataLength()	{
		return ds.get(0).length;
	}
	
	/**
	 * Return the data at a given index.
	 * 
	 * @param index The index to get from.
	 * @return The data retrieved.
	 */
	public T[] getData(int index) {
		return ds.get(index);
	}
	
	/**
	 * Return how many entries are in the data set.
	 * 
	 * @return The size as an int.
	 */
	public int size()	{
		return ds.size();
	}
	
	@Override
	public String toString() {
		String result = "";
		int max;
		if (ds.get(0) instanceof Double[]) {
			max = 5;
		}
		else {
			max = 0;
		}
		
		// Create a String representation
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
