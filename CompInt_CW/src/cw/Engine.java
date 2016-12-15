package cw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Engine {

	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	private ArrayList<Expression> population;
	private DataSet<Double> testData;
	private ArrayList<Double> testActual;

	private static final Double  	RECOMBINATION_PROBABILITY = 1.00;
	private static final Double  	MUTATION_PROBABILITY = 0.7;
	private static final int 		NUM_PARENTS = 4;						// MIN parents = 2
	private static final String[]	EVOLUTIONARY_METHOD = {"twoOptGeneration", "simpleStep", "localSearch", "randomSearch"};
	private static final Boolean 	ELITISM = true;
	private static final String  	SURVIVOR_SELECTION = "Generational";
	private static final int 	 	POPULATION_SIZE = 10;
	private static final int 	 	GENERATIONS = 100;
	private static final int 	 	CROSSOVER_POINT = 3;
	private static final String	 	TRAIN_FILE = "C:/Users/Chloe/Downloads/cwk_train.csv";
	private static final String  	TEST_FILE = "C:/Users/Chloe/Downloads/cwk_test.csv";
	private Random r;
	private Scanner scanner;

	public static void main(String[] args) {
		new Engine(); 
	}

	public Engine() {
		Object[] train = setupData(new File(TRAIN_FILE), data, actualValues);
		data = (DataSet<Double>) train[0]; 
		actualValues = (ArrayList<Double>) train[1];
		Object[] test = setupData(new File(TEST_FILE), testData, testActual);
		testData = (DataSet<Double>) test[0];
		testActual = (ArrayList<Double>) test[1];
		r = new Random();
		evolve(2); // IN evolutionary_method
	}

	public void evolve(int method) {
		System.out.println("------------- Initialising ------------");
		initialise();
		Collections.sort(population);
		System.out.println("Best in generation: " + getBestExpression(population));

		for (int i = 0; i < GENERATIONS; i++) {
			System.out.println("------------- Generation " + i + " -----------------");
			Collections.sort(population);

			switch(method) 
			{
			case 0: twoOptGeneration(tournament()); break;
			case 1: simpleStep(); break;
			case 2: localSearch(); break;
			case 3: randomSearch(); break;
			default: randomSearch(); break;
			}

			Collections.sort(population);
			for (int j = 0; j < 5; j++) {
				System.out.println("Best " + j + ": " + population.get(j).toString());
			}
		}

		String analysis = "\n ********** END OF ALGORITHM **********\n";
		analysis += "Algorithm used was: " + EVOLUTIONARY_METHOD[method] + "\n";
		analysis += "Population size: " + POPULATION_SIZE + "   Generations: " + GENERATIONS + "\n";
		analysis += "Best solution was: " + population.get(0) + "\n";

		for (int i = 0; i < data.size(); i++) {
			String exp = buildExpression(population.get(0), data.getData(i));
			analysis += "Row " + i + " is: " + exp + "\n";
			analysis += "      Estimated: " + getExpressionResult(exp) + "\n";
			analysis += "      Actual:    " + actualValues.get(i) + "\n";
		}

		for (int i = 0; i < testData.size(); i++) {
			String exp = buildExpression(population.get(0), testData.getData(i));
			analysis += "Test Row: " + i + " is: " + exp + "\n";
			analysis += "      Estimated: " + getExpressionResult(exp) + "\n";
			analysis += "      Actual:    " + testActual.get(i) + "\n";
		}

		analysis += " TRAIN AVERAGE = " + getExpressionAverage(population.get(0), data, actualValues) + "\n";
		analysis += " TEST AVERAGE  = " + getExpressionAverage(population.get(0), testData, testActual) + "\n";

		System.out.println(analysis);


	}

	public void crowdingStep() {
		ArrayList<Expression> parentPool = new ArrayList<>();
		ArrayList<Expression> children = new ArrayList<>();
		Collections.shuffle(population);
		int parents = NUM_PARENTS;
		if (parents%2 != 0) { 
			parents++;
		}

		/* ****** PHASE 1 ****** */
		for (int i = 0; i < parents; i++) {
			parentPool.add(population.get(i).clone());			
		}

		/* ****** PHASE 2 ****** */

		for (int i = 0; i < parentPool.size(); i+=2) {
			// Crossover
			if (r.nextDouble() < RECOMBINATION_PROBABILITY) {
				// one-point crossover
				children.addAll(crossover(CROSSOVER_POINT, parentPool.get(i), parentPool.get(i+1)));
			}
			// Mutation
			else {
				children.add(parentPool.get(i).clone());
				children.add(parentPool.get(i+1).clone());
				if (r.nextDouble() < MUTATION_PROBABILITY)	children.get(i).mutateByChange();
				if (r.nextDouble() < MUTATION_PROBABILITY)	children.get(i+1).mutateByChange();
			}
		}

		/* ****** PHASE 3 ****** */
		for (int parent = 0; parent < parentPool.size(); parent++) {
			for (int child = 0; child < children.size(); child++) {

			}
		}

		/* ****** PHASE 4 ****** */

		/* ****** PHASE 5 ****** */

	}


	public void simpleStep() {
		ArrayList<Expression> newPop = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			// child = oldPop[i]
			Expression child = population.get(i).clone();
			// mutate with prob of mutation
			if (r.nextDouble() <= MUTATION_PROBABILITY) {
				child.mutateByChange();
				child.setFitness(getExpressionAverage(child, data, actualValues));
			}
			// tournament of child and parent
			ArrayList<Expression> pool = new ArrayList<>();
			pool.add(child);
			pool.add(population.get(i));
			newPop.add(bestNeighbour(pool));
		}

		population.clear();
		population.addAll(newPop);
	}

	public ArrayList<Expression> crossover(int crossPoint, 
			Expression parentA, Expression parentB) {

		String[] first = parentA.getExpression().clone();
		String[] second = parentB.getExpression().clone();
		String[] childOne = first.clone();
		String[] childTwo = second.clone();
		ArrayList<Expression> result = new ArrayList<>();

		for (int i = crossPoint; i < parentA.getExpression().length; i++) {
			childOne[i] = second[i];
			childTwo[i] = first[i];
		}

		result.add(new Expression(childOne));
		result.add(new Expression(childTwo));
		result.get(0).setFitness(getExpressionAverage(result.get(0), data, actualValues));
		result.get(1).setFitness(getExpressionAverage(result.get(1), data, actualValues));

		return result;
	}

	private ArrayList<Expression> tournament() {
		ArrayList<Expression> parents = new ArrayList<>();

		int index = 0;
		if (ELITISM) {
			parents.add(bestNeighbour(population).clone());
			index++;
		}
		for (int i = index; i < NUM_PARENTS; i++) {
			parents.add(population.get(r.nextInt(population.size())).clone());
		}
		return parents;
	}

	private void localSearch() {
		Collections.sort(population);

		ArrayList<Expression> parents = (ArrayList<Expression>) tournament().clone();
		ArrayList<Expression> children = new ArrayList<>();

		int pars = NUM_PARENTS;
		if (pars%2 != 0) { 
			pars--;
		}
			
		for (int i = 0; i < pars-1; i += 2) {
			if (r.nextDouble() < RECOMBINATION_PROBABILITY) {
				children.addAll(crossover(CROSSOVER_POINT, parents.get(i), parents.get(i+1)));
			}
		}

		for (int i = 0; i < children.size(); i++) {
			if (r.nextDouble() < MUTATION_PROBABILITY) {
				children.set(i, children.get(i).mutateByChange().clone());
			}
			children.get(i).setFitness(getExpressionAverage(children.get(i), data, actualValues));
		}
		
		if (ELITISM) {
			children.add(population.get(0).clone());
		}
		
		for (int i = children.size(); i < POPULATION_SIZE; i++) {
			children.add(children.get(i%pars).clone().mutateByChange());
			children.get(i).setFitness(getExpressionAverage(children.get(i), data, actualValues));
		}

		population = (ArrayList<Expression>) children.clone();
	}

	private void randomSearch() {

		Collections.sort(population);
		
		ArrayList<Expression> newPop = new ArrayList<>();
		newPop.add(population.get(0).clone());
		
		for (int i = 1; i < POPULATION_SIZE; i++) {
			newPop.add(new Expression(population.get(0).size()));
			newPop.get(i).setFitness(getExpressionAverage(newPop.get(i), data, actualValues));
		}
		
		population.clear();
		population.addAll(newPop);
	}
	
	public void twoOptGeneration(ArrayList<Expression> parents) {
		ArrayList<Expression> pool = new ArrayList<>();

		for (int i = 0; i < parents.size(); i++) {
			pool.addAll(twoOptNeighbourhood(parents.get(i)));
		}
		Collections.sort(pool);
		population.clear();
		int index = 0;
		if (ELITISM) {
			population.add(pool.get(index).clone());
			index++;
		}
		for (int i = index; i < POPULATION_SIZE; i++) {
			population.add(pool.get(r.nextInt(pool.size())).clone());
		}
	}

	public void initialise() {
		population = new ArrayList<>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new Expression(data.getDataLength()-1));	
			population.get(i).setFitness(getExpressionAverage(population.get(i), data, actualValues));
		}

	}

	private Expression bestNeighbour(ArrayList<Expression> nbhd) {
		Collections.sort(nbhd);
		return nbhd.get(0);
	}


	private ArrayList<Expression> twoOptNeighbourhood(final Expression expr) {
		ArrayList<Expression> result = new ArrayList<>();
		String[] tmpExpr = new String[expr.getExpression().length];
		String tmpPart = "";

		// 1, 2, 3, ...
		for (int i = 0; i < expr.getExpression().length; i++) {
			// 1-2, 1-3, 1-4, ...
			for (int gap = 1; gap+i < expr.getExpression().length; gap++) {
				tmpExpr = expr.getExpression().clone();
				tmpPart = expr.getExpression()[i];
				tmpExpr[i] = expr.getExpression()[i+gap];
				tmpExpr[i+gap] = tmpPart;
				Expression e = new Expression(tmpExpr);
				e.setFitness(getExpressionAverage(e, data, actualValues));
				result.add(e);
			}
		}
		return result;
	}

	private String getBestExpression(ArrayList<Expression> area) {
		Collections.sort(area);
		return area.get(0).toString();
	}

	private Double getExpressionAverage(Expression expr, DataSet<Double> d, ArrayList<Double> actual)
	{
		Double fitness = 0.0;
		String[] expressions = new String[d.size()]; 
		for (int j = 0; j < d.size(); j++) {
			expressions[j] = buildExpression(expr, d.getData(j));
			fitness += Math.abs(getExpressionResult(expressions[j]) - actual.get(j));
		}
		fitness = (fitness / data.size());

		return fitness;
	}

	private String buildExpression(Expression ops, Double[] data) {

		String expr = "";
		Double[] values = data.clone();		
		int op = 0;
		int num = 0;

		// create expression with operands and operators
		for (int i = 0; i < (data.length*2)-1; i++) {
			if (i%2 == 0) {
				expr += String.valueOf(values[num]) + " ";
				num++;
			} else if (i < (data.length*2)-1) {
				expr += ops.getExpressionPart(op) + " ";
				op++;
			}
		}
		return expr;
	}

	private Double getExpressionResult(String expr) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine scriptEngine = sem.getEngineByName("JavaScript");

		Object result = null;
		try {
			result = scriptEngine.eval(expr);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.valueOf(result.toString());
	}

	private Object[] setupData(File file, DataSet<Double> d, ArrayList<Double> actual) {
		d = new DataSet<>();
		actual = new ArrayList<>();

		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line = "";

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length-1];
			actual.add(Double.parseDouble(tokens[0]));

			for (int pos = 1; pos < tokens.length; pos++) {
				components[pos-1] = Double.parseDouble(tokens[pos]);	
			}
			d.add(components);
		}

		Object[] rtn = new Object[2];
		rtn[0] = d;
		rtn[1] = actual;
		return rtn;
	}
}
