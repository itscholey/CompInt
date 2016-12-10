package cw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Engine2 {

	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	private ArrayList<Expression> population;
	private static final int POPULATION_SIZE = 5;
	private Scanner scanner;
	private ScriptEngine scriptEngine;
	
	public static void main(String[] args) {
		new Engine2();
	}
		
	public Engine2() {
		ScriptEngineManager sem = new ScriptEngineManager();
	    scriptEngine = sem.getEngineByName("JavaScript");
		setupData(new File("C:/Users/Chloe/Downloads/cwk_test.csv"));
		evolve();
	}
	
	public void evolve() {
		System.out.println("------------- Initialise ------------");
		initialise();
		System.out.println("--------- Done Initialising ---------");
	}
	
	public void initialise() {
		population = new ArrayList<>();
		
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new Expression(data.getDataLength()-1));
			population.get(i).setFitness(getExpressionAverage(population.get(i)));
		}
		
		for (int i = 0; i < population.size(); i++) {
			System.out.println(population.get(i).toString());
		}
		
	}
	
	private Double getExpressionAverage(Expression expr)
	{
		Double fitness = 0.0;
		String fitnesses = "";
		String[][] expressions = new String[data.size()][]; 
		for (int j = 0; j < data.size(); j++) {
			expressions[j] = buildExpression(expr, j);
			fitness += (getExpressionResult(expressions[j]) - actualValues.get(j));
			fitnesses += "Estimate: " + getExpressionResult(expressions[j]) + " Actual: " + actualValues.get(j) + " Difference: " +
					(getExpressionResult(expressions[j]) - actualValues.get(j) + "\n");
		}
		
		fitness = (fitness / data.size());
		
		System.out.println("Fitness is " + fitness + "\n" + fitnesses);
		return fitness;
	}
	
	private String[] buildExpression(Expression ops, int index) {
		
		String[] expr = new String[ops.size() + data.getDataLength()];
		Double[] values = data.getData(index);		
		int op = 0;
		int num = 0;
		
		// create expression with operands and operators
		for (int i = 0; i < (data.getDataLength()*2)-1; i++) {
			if (i%2 == 0) {
				expr[i] = String.valueOf(values[num]);
				num++;
			} else if (i < (data.getDataLength()*2)-1) {
					expr[i] = ops.getExpressionPart(op);
					op++;
			}
		}
		return expr;
	}
	
	private Double getExpressionResult(String[] expr) {
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
		
		Object result = null;
		try {
			result = scriptEngine.eval(strExpr);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.valueOf(result.toString());
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
		
		String line = "";
		int count = 0;
		
		//while (scanner.hasNextLine()) {
		while (count < 3) {
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length-1];
			actualValues.add(Double.parseDouble(tokens[0]));
			
			for (int pos = 1; pos < tokens.length; pos++) {
				components[pos-1] = Double.parseDouble(tokens[pos]);	
			}
			data.add(components);
			count++;
		}
		System.out.println(data.toString());
	}
}
