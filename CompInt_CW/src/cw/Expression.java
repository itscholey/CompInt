package cw;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.lang.Math;

public class Expression implements Comparable<Expression>, Cloneable {
	private static final String[] OPERATORS = { "+", "-", "*", "/", "%", "^"};
	private String[] expression;
	private Double fitness;
	
	public Expression(String[] expr) {
		this.expression = expr;
	}
	
	public Expression(int length) {
		randomExpression(length);
	}
	
	public Expression() {
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
	  
	public Expression mutateByChange() {
		Random r = new Random();
		String[] swapped = getExpression();
		int toMutate = r.nextInt(expression.length);
		
		for (int i = 0; i < toMutate; i++) {
			int toSwap = r.nextInt(expression.length);
			swapped[toSwap] = OPERATORS[r.nextInt(OPERATORS.length)];
		}
		
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
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(expression);
		result = prime * result + ((fitness == null) ? 0 : fitness.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expression other = (Expression) obj;
		if (!Arrays.equals(expression, other.expression))
			return false;
		if (fitness == null) {
			if (other.fitness != null)
				return false;
		} else if (!fitness.equals(other.fitness))
			return false;
		return true;
	}

	public String toString() {
		String s = "Values: ";
		
		for (int i = 0; i < expression.length; i++) {
			s += expression[i] + " ";
		}
		s += fitness;
		
		return s;
	}
	
	public int compareTo(Expression otherExpr) {
		Double ftns = Math.abs(fitness);
		Double otrftns = Math.abs(otherExpr.getFitness());
		return ftns.compareTo(otrftns);
    }

	public Expression clone() {
		final Expression clone;
		try {
			clone = (Expression) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cannot clone.", e);
		}
		clone.setExpression(this.expression.clone());
		clone.setFitness(this.fitness);
		return clone;
	}
}
