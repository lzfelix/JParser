package tests;

import static org.junit.Assert.*;
import lexer.Lexer;
import lexer.ParserException;
import org.junit.Test;

public class ParserTests {
	Lexer l;

	@Test public void testSingleDigit() throws ParserException {
		l = new Lexer("3");
		assertEquals("3.0", l.toString());
	}

	@Test public void testAddition() throws ParserException {
		l = new Lexer("3 + 5");
		assertEquals("3.0 ADD 5.0", l.toString());
	}
	
	@Test public void testSubtraction() throws ParserException {
		l = new Lexer("3 - 5");
		assertEquals("3.0 SUB 5.0", l.toString());
	}
	
	@Test public void testMultiplication() throws ParserException {
		l = new Lexer("3 * 5");
		assertEquals("3.0 MUL 5.0", l.toString());
	}
	
	@Test public void testDivision() throws ParserException {
		l = new Lexer("3 / 5");
		assertEquals("3.0 DIV 5.0", l.toString());
	}
	
	@Test public void testHardExpression() throws ParserException {
		l = new Lexer("ln(sin(x)^2 + cos(x)^2) + 9");
		assertEquals("ln LPAR sin LPAR x(0.0) RPAR POW 2.0 ADD cos LPAR x(0.0) RPAR POW 2.0 RPAR ADD 9.0", l.toString());
	}
	
	@Test public void testDecimalValues() throws ParserException {
		l = new Lexer("3.2 / 5.7");
		assertEquals("3.2 DIV 5.7", l.toString());
	}
	
	@Test public void negativeNumber() throws ParserException {
		l = new Lexer("-3^2");
		assertEquals("SUB 3.0 POW 2.0", l.toString());
	}
	
	@Test public void manyVariables() throws ParserException {
		l = new Lexer("x*y+z");
		assertEquals("x(0.0) MUL y(0.0) ADD z(0.0)", l.toString());
	}
	
	@Test (expected=ParserException.class) 
	public void invalidDecimalValue() throws ParserException {
		l = new Lexer("3.328.2");
	}
	
	@Test (expected=ParserException.class) 
	public void invalidVarAndNumber() throws ParserException {
		l = new Lexer("3x");
	}
	
	@Test (expected=ParserException.class) 
	public void multipleOperators() throws ParserException {
		l = new Lexer("3+-4");		
	}
}
