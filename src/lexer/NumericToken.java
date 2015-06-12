package lexer;

/**
 * Holds numbers information (integer or double)
 * 
 * @author Luiz Felix
 */
public class NumericToken extends Token {
	double number;
	
	public NumericToken(Type type, double number) {
		super(type);
		this.number = number;
	}
	
	public double getValue() {
		return this.number;
	}
	
	public String toString() {
		return String.valueOf(number);
	}

}
