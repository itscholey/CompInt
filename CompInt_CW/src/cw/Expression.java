package cw;

public class Expression implements Comparable<Expression> {
	private String[] expression;
	private Double fitness;
	
	public Expression(String[] expr) {
		expression = expr;
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
}
