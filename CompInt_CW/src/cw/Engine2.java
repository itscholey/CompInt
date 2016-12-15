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

public class Engine2 {

	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	private ArrayList<Expression> population;
	private DataSet<Double> testData;
	private ArrayList<Double> testActual;

	private static final Double  	RECOMBINATION_PROBABILITY = 1.00;
	private static final Double  	MUTATION_PROBABILITY = 0.7;
	private static final int 		NUM_PARENTS = 4;						// MIN parents = 2
	private static final String[]	EVOLUTIONARY_METHOD = {"twoOptGeneration", "simpleStep", "crowdingStep", "localSearch", "randomSearch"};
	private static final Boolean 	ELITISM = true;
	private static final String  	SURVIVOR_SELECTION = "Generational";
	private static final int 	 	POPULATION_SIZE = 6;
	private static final int 	 	GENERATIONS = 150;
	private static final int 	 	CROSSOVER_POINT = 5;
	private static final String	 	TRAIN_FILE = "C:/Users/Chloe/Downloads/cwk_train.csv";
	private static final String  	TEST_FILE = "C:/Users/Chloe/Downloads/cwk_test.csv";
	private Random r;
	private Scanner scanner;

	public static void main(String[] args) {
		new Engine2(); 
	}

	public Engine2() {
		Object[] train = setupData(new File(TRAIN_FILE), data, actualValues);
		data = (DataSet<Double>) train[0]; 
		actualValues = (ArrayList<Double>) train[1];
		Object[] test = setupData(new File(TEST_FILE), testData, testActual);
		testData = (DataSet<Double>) test[0];
		testActual = (ArrayList<Double>) test[1];
		r = new Random();
		evolve(1); // IN evolutionary_method
/*		initialise();
		String[] sA = {"-", "+", "+", "*", "+", "+", "-", "-", "*", "*", "-", "*"};
		Expression e = new Expression(sA);
		
		
		String analysis = "\n ********** END OF ALGORITHM **********\n";
		for (int i = 0; i < data.size(); i++) {
			String exp = buildExpression(e, data.getData(i));
			analysis += "Row " + i + " is: " + exp + "\n";
			analysis += "      Estimated: " + getExpressionResult(exp) + "\n";
			analysis += "      Actual:    " + actualValues.get(i) + "\n";
		}
		
		for (int i = 0; i < testData.size(); i++) {
			String exp = buildExpression(e, testData.getData(i));
			analysis += "Test Row: " + i + " is: " + exp + "\n";
			analysis += "      Estimated: " + getExpressionResult(exp) + "\n";
			analysis += "      Actual:    " + testActual.get(i) + "\n";
		}
		
		analysis += " TRAIN AVERAGE = " + getExpressionAverage(e) + "\n";
		analysis += " TEST AVERAGE  = " + getTestExpressionAverage(e) + "\n";
		
		System.out.println(analysis);*/
	}

	public void evolve(int method) {
		System.out.println("------------- Initialising ------------");
		initialise();
		Collections.sort(population);
		System.out.println("Best in generation: " + getBestExpression(population));


		// evaluate each candidate

		// repeat until ( termination condition is satisfied ) do
		
		for (int i = 0; i < GENERATIONS; i++) {
			System.out.println("------------- Generation " + i + " -----------------");
			Collections.sort(population);

			switch(method) 
			{
			case 0: twoOptGeneration(tournament()); break;
			case 1: simpleStep(); break;
			//case 2: crowdingStep();
			case 3: localSearch(); break;
			case 4: randomSearch(); break;
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
		
		analysis += " TRAIN AVERAGE = " + getExpressionAverage(population.get(0)) + "\n";
		analysis += " TEST AVERAGE  = " + getTestExpressionAverage(population.get(0)) + "\n";
		
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

	public void twoOptGeneration(ArrayList<Expression> parents) {
		HashSet<Expression> pool = new HashSet<>();
		ArrayList<Expression> newGenPool = new ArrayList<>();

		for (int i = 0; i < parents.size(); i++) {
			pool.addAll(twoOptNeighbourhood(parents.get(i)));
		}
		newGenPool.addAll(pool);
		Collections.sort(newGenPool);
		population.clear();
		int index = 0;
		if (ELITISM) {
			population.add(newGenPool.get(index).clone());
			index++;
		}
		for (int i = index; i < POPULATION_SIZE; i++) {
			population.add(newGenPool.get(r.nextInt(newGenPool.size())).clone());
		}
	}

	public void simpleStep() {
		ArrayList<Expression> newPop = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			// child = oldPop[i]
			Expression child = population.get(i).clone();
			// mutate with prob of mutation
			if (r.nextDouble() <= MUTATION_PROBABILITY) {
				child.mutateByChange();
				child.setFitness(getExpressionAverage(child));
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
		result.get(0).setFitness(getExpressionAverage(result.get(0)));
		result.get(1).setFitness(getExpressionAverage(result.get(1)));

		System.out.println("\n\n" + parentA.toString() + "\n" + parentB.toString() + "\n" + result.get(0).toString() + "\n" + result.get(1).toString());
		return result;
	}

	private ArrayList<Expression> tournament() {
		System.out.println("---------- Tournament ------------");
		ArrayList<Expression> parents = new ArrayList<>();

		int index = 0;
		if (ELITISM) {
			parents.add(bestNeighbour(population).clone());
			index++;
		}
		for (int i = index; i < NUM_PARENTS; i++) {
			parents.add(population.get(r.nextInt(population.size())).clone());
		}
		Collections.sort(parents);
		for (int i = 0; i < parents.size(); i++) {
			System.out.println(parents.get(i).toString());
		}

		System.out.println("-------------- End of Tournament --------------");
		return parents;
	}

	private Expression localSearch() {
		Collections.sort(population);
		Expression expr = new Expression(population.get(0).getExpression().clone());
		Expression best = new Expression(expr.getExpression().clone());
		best.setFitness(getExpressionAverage(expr));
		
		ArrayList<Expression> nbhd = twoOptNeighbourhood(expr);
		expr = bestNeighbour(nbhd);
		System.out.println("Best neighbour: " + expr.toString());

		if (Math.abs(expr.getFitness()) < Math.abs(best.getFitness())) {
			best = expr.clone();
		}
		return best;		
	}

	public void initialise() {
		population = new ArrayList<>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new Expression(data.getDataLength()-1));	
			population.get(i).setFitness(getExpressionAverage(population.get(i)));
		}

	}

	private Expression bestNeighbour(ArrayList<Expression> nbhd) {
		Collections.sort(nbhd);
		return nbhd.get(0);
	}


	private ArrayList<Expression> twoOptNeighbourhood(final Expression expr) {
		HashSet<Expression> nbhd = new HashSet<>();
		String[] tmpExpr = new String[expr.getExpression().length];
		String tmpPart = "";

		// 1, 2, 3, ...
		for (int i = 0; i < expr.getExpression().length; i++) {
			//System.out.println(i);
			// 1-2, 1-3, 1-4, ...
			for (int gap = 1; gap+i < expr.getExpression().length; gap++) {
				tmpExpr = expr.getExpression().clone();
				tmpPart = expr.getExpression()[i];
				tmpExpr[i] = expr.getExpression()[i+gap];
				tmpExpr[i+gap] = tmpPart;
				Expression e = new Expression(tmpExpr);
				e.setFitness(getExpressionAverage(e));
				nbhd.add(e);
				//System.out.println(e.toString());
			}
		}

		//System.out.println("Neighbourhood");
		ArrayList<Expression> result = new ArrayList<>();
		result.addAll(nbhd);
		Collections.sort(result);

		/*for (Expression e : result) {
			System.out.println(e.toString());
		}	*/	
		return result;
	}

	private String getBestExpression(ArrayList<Expression> area) {
		Collections.sort(area);
		return area.get(0).toString();
	}

	private Double getExpressionAverage(Expression expr)
	{
		Double fitness = 0.0;
		String fitnesses = "";
		String[] expressions = new String[data.size()]; 
		for (int j = 0; j < data.size(); j++) {
			expressions[j] = buildExpression(expr, data.getData(j));
			fitness += Math.abs(getExpressionResult(expressions[j]) - actualValues.get(j));
			fitnesses += "Estimate: " + getExpressionResult(expressions[j]) + " Actual: " + actualValues.get(j) + " Difference: " +
					(getExpressionResult(expressions[j]) - actualValues.get(j) + "\n");
		}
		fitness = (fitness / data.size());

		//System.out.println("Fitness is " + fitness + "\n" + fitnesses);
		return fitness;
	}
	
	private Double getTestExpressionAverage(Expression expr)
	{
		Double fitness = 0.0;
		String fitnesses = "";
		String[] expressions = new String[testData.size()]; 
		for (int j = 0; j < testData.size(); j++) {
			expressions[j] = buildExpression(expr, testData.getData(j));
			fitness += Math.abs(getExpressionResult(expressions[j]) - testActual.get(j));
			fitnesses += "Estimate: " + getExpressionResult(expressions[j]) + " Actual: " + testActual.get(j) + " Difference: " +
					(getExpressionResult(expressions[j]) - testActual.get(j) + "\n");
		}
		fitness = (fitness / testData.size());

		//System.out.println("Fitness is " + fitness + "\n" + fitnesses);
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

	private Expression randomSearch() {

		Expression random = new Expression(12);
		random.setFitness(getExpressionAverage(random));
		Expression best = new Expression(random.getExpression().clone());
		best.setFitness(getExpressionAverage(random));

		random.randomExpression(12);
		random.setFitness(getExpressionAverage(random));
		System.out.println(random.toString());

		if (Math.abs(random.getFitness()) < Math.abs(best.getFitness())) {
			best.setExpression(random.getExpression().clone());
			best.setFitness(getExpressionAverage(best));
			System.out.println("*** New Best *** " + best.toString());
		}
		return best;
	}
}
