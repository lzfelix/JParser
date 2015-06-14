package lexer;

/**
 * Holds numbers information as a <code>integer</code>.
 * 
 * @author Luiz Felix
 */
public class IntegerToken extends Token {
	private int number;
	
	/**
	 * Creates a new token that holds a number in double form.
	 * @param number The value to be stored
	 */
	public IntegerToken(int number) {
		super(Token.Type.NUM);
		this.number = number;
	}
	
	public int getValue() {
		return this.number;
	}
	
	public String toString() {
		return String.valueOf(number);
	}

}
