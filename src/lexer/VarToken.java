package lexer;

/**
 * Holds variables's name and value.
 * TODO: add value mapping
 * @author Luiz Felix
 * @version 0.01
 */
public class VarToken extends Token {

	String varName;
	double value;
	
	public VarToken(Type type, String name) {
		super(type);
		this.varName = name;
	}
	
	public VarToken(Type type, String name, double value) {
		super(type);
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
