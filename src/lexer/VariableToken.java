package lexer;

/**
 * A symbolic token that holds a variable index, but not its
 * value (that must be replaced before the math parsing is 
 * done).
 * 
 * @author Luiz Felix
 */
public class VariableToken extends Token {
	private int index;
	
	public VariableToken(int index) {
		super(Token.Type.VAR);
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public String toString() {
		return "VAR_" + index;
	}
}
