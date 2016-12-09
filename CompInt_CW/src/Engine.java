import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import adt.DataSet;

public class Engine {

	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	private Scanner scanner;
	// operations
	private DataSet<String> population;
	private static final String[] OPERATORS = { "+", "-", "*", "/" };
	private static final int POPULATION_SIZE = 50;
	private ScriptEngine scriptEngine;
	
	public static void main(String[] args) {
		new Engine();
	}
		
	public Engine() {
		ScriptEngineManager sem = new ScriptEngineManager();
	    scriptEngine = sem.getEngineByName("JavaScript");
		setupData(new File("C:/Users/Chloe/Downloads/cwk_test.csv"));
		method();
	}
	
	private void setupData(File file) {
		data = new DataSet<>();
		actualValues = new ArrayList<>();
		
		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int countNodes = 0;

		String line = scanner.nextLine();
		
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length-1];
			
			for (int pos = 0; pos < tokens.length; pos++) {
				if (pos > 0) { 
					components[pos-1] = Double.parseDouble(tokens[pos]);	
				} else {
					actualValues.add(Double.parseDouble(tokens[0]));
				}
			}
			countNodes++;
			data.add(components);
		}
		System.out.println("Total nodes = " + countNodes);
		//System.out.println(data.toString());
	}
	
	public void method()
	{
		
		
		/*
		 * BEGIN */
		initialise();
		
		buildExpression(population.getData(0), 0);
		
		
		/* 	EVALUATE candidates
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
	
	private void initialise() {
		population = new DataSet<>();
		
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(randomExpression());
		}
		
		System.out.println("Size = " + population.size() + "\n" + population.toString());		
		
	}
	
	private String[] randomExpression() {
		
		Random r = new Random();
		String[] result = new String[data.getDataLength()-1];
		
		for (int i = 0; i < data.getDataLength()-1; i++) {
			result[i] = OPERATORS[r.nextInt(OPERATORS.length)];
		}
		
		return result;
	}
	
	private String[] buildExpression(String[] ops, int index) {
		
		String[] expr = new String[ops.length + data.getDataLength()];
		Double[] values = data.getData(index);
		System.out.println("expr " + expr.length + " values " + values.length + " ops " + ops.length);
		
		int op = 0;
		int num = 0;
		
		// create expression with operands and operators
		for (int i = 0; i < (data.getDataLength()*2)-1; i++) {
			if (i%2 == 0) {
				expr[i] = String.valueOf(values[num]);
				num++;
			} else {
				if (i < (data.getDataLength()*2)-1) {
					expr[i] = ops[op];
					op++;
				}
			}
		}
		
		// ensure no x/0 or x*0
		for (int i = 0; i < expr.length; i++)
		{
			if ((expr[i] == "*" || expr[i] == "/")
					&& ((Double.valueOf(expr[i-1]) == 0.0 && Double.valueOf(expr[i+1]) == 0.0) 
							|| (Double.valueOf(expr[i-1]) == 0.0) 
							|| (Double.valueOf(expr[i+1]) == 0.0))) {
				expr[i] = "+";
			}
		}
		
		
		
		Double result = evaluateExpression(expr);
		System.out.println("Result: " + result);
		return expr;
	}

	private Double evaluateExpression(String[] expr) {
		ArrayList<String> expression = new ArrayList<String>();
		
		for (int i = 0; i < expr.length; i++) {
			expression.add(expr[i]);
		}
		
		String strExpr = "";
		for (int i = 0; i < expression.size(); i++) {
			strExpr += expression.get(i);
			if (i < expression.size()-1) {
				strExpr += " ";
			}
		}
		System.out.println(strExpr);
		
		Object result = null;
		try {
			result = scriptEngine.eval(strExpr);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		
		return Double.valueOf(result.toString());
	}


}
