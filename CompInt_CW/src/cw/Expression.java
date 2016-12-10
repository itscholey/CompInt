package cw;

import java.util.Random;

public class Expression implements Comparable<Expression> {
	private static final String[] OPERATORS = { "+", "-", "*" };
	private String[] expression;
	private Double fitness;
	
	public Expression(String[] expr) {
		expression = expr;
	}
	
	public Expression(int length) {
		randomExpression(length);
	}
	
	public void setExpression(String[] expr) {
		this.expression = expr;
	}
	public String[] getExpression() {
		return expression;
	}
	
	public int size() {
		return expression.length;
	}
	
	public void setFitness(Double f) {
		this.fitness = f;
	}
	
	public Double getFitness() {
		return fitness;
	}
	
	public String getExpressionPart(int part) {
		return expression[part];
	}
	
	public int compareTo(Expression otherExpr) {
        if (fitness < otherExpr.getFitness()) {
            return -1;
        }
        else if (fitness > otherExpr.getFitness()) {
            return 1;
        }
        else {
            return 0;
        }
    }
	
	public Expression mutateByChange() {
		Random r = new Random();
		int toSwap = r.nextInt(expression.length);
		String[] swapped = getExpression();
		swapped[toSwap] = OPERATORS[r.nextInt(OPERATORS.length)];
		
		setExpression(swapped);
		
		return this;
	}
	
	public static String getRandomOperator() {
		Random r = new Random();
		String result = OPERATORS[r.nextInt(OPERATORS.length)];
		return result;
	}
	
	/**
	 * Generate a random string of operators.
	 * 
	 * @return A String[] containing the random operators.
	 */
	public void randomExpression(int length) {
		expression = new String[length];
		
		for (int i = 0; i < length; i++) {
			expression[i] = Expression.getRandomOperator();
		}
	}
	
	public String toString() {
		String s = "Values: ";
		
		for (int i = 0; i < expression.length; i++) {
			s += expression[i] + " ";
		}
		s += fitness;
		
		return s;
	}
}
