package lexer;

/**
 * Represents a token without information that can vary, for example:
 * parenthesis, commas, operators
 * 
 * @author Luiz Felix
 */
public class Token {
	//NEG = negative number
	//POS = a (dummy) positive signal
	//FUN = function
	//VAR = variable
	//NUM = number (double, always)
	
	public static enum Type {ADD, SUB, MUL, DIV, POW, NEG, POS, LPAR, RPAR, COM, FUN, VAR, NUM};
	public static Type operators[] = {Type.ADD, Type.SUB, Type.MUL, Type.DIV, Type.POW, Type.NEG, Type.POS};
	
	private Type type;
	private int priority;
	
	/**
	 * Constructs a new token. Based on its <code>code</code> the priority is also set.
	 * @param type The token's type
	 */
	public Token(Type type) {
		this.type = type;
		this.priority = -1;	//standard priority
		
		//The / 2 grants that (ADD, SUB), (MUL, DIV) and (POW, NEG) have priorities 
		//[1,2,3] respectively
		for (int i = 0; i < operators.length; i++)
			if (operators[i] == type) {
				this.priority = i / 2;
				break;
			}
		
		//forces POS having the same precedence as NEG and POW
		if (priority > 2)
			priority = 2;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	/**
	 * Helper function that tells if a '-'/'+' sign is a operator or a signal
	 * This character will be a negative sign only if it is preceded either by another
	 * operator, a left parenthesis, a comma or it is in the beginning of the expression (in 
	 * this case the parameter to this function should be a null, which is the default returned  
	 * value from a list-like data structure when it is empty).
	 * @param token The last read token BEFORE a '-' is found.
	 * @return <code>true</code> iff <code>token</code> is a operator, a left parenthesis or
	 * <code>null</code>.
	 */
	public static boolean isSignal(Token token) {
		if (token == null) return true;
		
		//test for operators
		for (Type t : operators)
			if (token.getType() == t)
				return true;
		
		if (token.getType() == Type.LPAR || token.getType() == Type.COM)
			return true;
		
		return false;
	}
	
	/* for debug purposes */
	public String toString() {
		return this.type.toString();
	}
}
