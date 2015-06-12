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
	public static Type operands[] = {Type.ADD, Type.SUB, Type.MUL, Type.DIV, Type.POW};
	
	private Type type;
	private int priority;
	
	public Token(Type type) {
		this.type = type;
		this.priority = -1;	//std priority for all tokens but operators
		
		//The / 2 grants that (ADD, SUB), (MUL, DIV) and POW have priorities 
		//[1,2,3] respectively
		for (int i = 0; i < operands.length; i++)
			if (operands[i] == type) {
				this.priority = i / 2;
				break;
			}
	}
	
	public Type getType() {
		return this.type;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	//for debug purposes
	public String toString() {
		return this.type.toString();
	}
}
