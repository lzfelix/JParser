package lexer;

/**
 * Holds variables's name and value.
 * TODO: add value mapping
 * @author Luiz Felix
 */
public class VarToken extends Token {
	String varName;
	double value;
	
	/**
	 * Maps a variable into a value
	 * @param name The variable's name
	 * @param value  The variable's value
	 */
	public VarToken(String name, double value) {
		super(Token.Type.VAR);
		
		this.varName = name;
		this.value = 0;
	}
	
	public String getName() {
		return this.varName;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public String toString() {
		return this.varName + "(" + String.valueOf(value) + ")";
	}

}
