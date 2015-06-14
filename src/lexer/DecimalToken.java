package lexer;

/**
 * Holds numbers information as a <code>double</code>.
 * 
 * @author Luiz Felix
 */
public class DecimalToken extends Token {
	private double number;
	
	/**
	 * Creates a new token that holds a number in double form.
	 * @param number The value to be stored
	 */
	public DecimalToken(double number) {
		super(Token.Type.NUM);
		this.number = number;
	}
	
	public double getValue() {
		return this.number;
	}
	
	public String toString() {
		return String.valueOf(number);
	}

}
