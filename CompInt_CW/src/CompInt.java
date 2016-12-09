import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import adt.*;

public class CompInt 
{
	private Scanner scanner;
	private DataSet dataSet;
	/* <Fitness, Tree> */
	private HashMap<Double, ExpressionTree<String>> population;
	
	private final String[] operators = {"+", "-", "/", "*"};
	
	
	public static void main(String[] args) 
	{
		new CompInt();
	}
	
	public CompInt()
	{
		dataSet = new DataSet();
		population = new HashMap<>();
		
		fg dfg = new fg("1+2+3/4*5", fg.EXPRESSIONTYPE.Infix);
		System.out.println(dfg.GetPrefixExpression());
		
		fg dfgh = new fg("+ + 1.0 2.0 * / 3.0 4.0 5.0", fg.EXPRESSIONTYPE.Prefix);
		System.out.println(dfgh.GetInfixExpression());
		
		/*try {
			scanner = new Scanner(new File("C:/Users/Chloe/Downloads/cwk_test.csv"));
			setup();
			for (int i = 0; i < dataSet.size(); i++)
			{
				randomSolution(i);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		
		
	}
	
	public void setup()
	{
		int countNodes = 0;

		String line = scanner.nextLine();
		
		while (scanner.hasNextLine())
		{
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length];
			for (int pos = 0; pos < tokens.length; pos++)
			{
				components[pos] = Double.parseDouble(tokens[pos]);	
			}
			countNodes++;
			dataSet.add(components);
			
		}
		System.out.println("Total nodes = " + countNodes);
		System.out.println(dataSet.toString());
		
		
		
		
		
	}
	
	public void method()
	{
		
		
		/*
		 * BEGIN
		 * 	INITIALISE population with random candidate solutions
		 * 	EVALUATE candidates
		 * 	REPEAT UNTIL ( termination condition IS satisfied ) DO
		 * 		1. SELECT parents
		 *  	2. RECOMBINE pairs of parents
		 *  	3. MUTATE offspring
		 *  	4. EVALUATE new candidates
		 *  	5. SELECT individuals for next gen
		 * 	OD
		 * END
		*/		
	}
	
	public ExpressionTree<String> randomSolution(int index)
	{
		Random r = new Random();
		String[] data = new String[(dataSet.getDataLength()*2) - 1];
		data[0] = String.valueOf(dataSet.getData(index)[1]);
		
		for (int i = 1; i < (dataSet.getDataLength()-1); i++)
		{
			data[(2*i)-1] = operators[r.nextInt(operators.length)];
			data[2*i] = String.valueOf(dataSet.getData(index)[i+1]);
		}
		
		String s = "";
		for (int j = 0; j < ((dataSet.getDataLength()-1)*2)-1; j++)
		{
			s += (data[j] + " ");
		}
		
		
		System.out.println(s);
		return null;
	}
	

}
