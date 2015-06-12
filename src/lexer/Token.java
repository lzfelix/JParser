package lexer;

/**
 * Represents a token without information that can vary, for example:
 * parenthesis, commas, operators
 * 
 * @author Luiz Felix
 */
public class Token {
	//FUN = function
	//VAR = variable
	//NUM = number (double, always)
	public static enum Type {ADD, SUB, MUL, DIV, POW, LPAR, RPAR, COM, FUN, VAR, NUM};
	
	private Type type;
	
	public Token(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return this.type;
	}
	
	//for debug purposes
	public String toString() {
		return this.type.toString();
	}
}
