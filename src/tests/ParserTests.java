package tests;

import static org.junit.Assert.*;
import lexer.Lexer;

import org.junit.Before;
import org.junit.Test;

import exceptions.LexerException;
import exceptions.ParserException;

public class ParserTests {
	Lexer l;
	
	@Before public void setUp() {
		l = Lexer.getInstance();
	}

	@Test public void testSingleDigit() throws ParserException, LexerException {
		l.parseToTokens("3");
		assertEquals("3.0", l.toString());
	}

	@Test public void testAddition() throws ParserException, LexerException {
		l.parseToTokens("3 + 5");
		assertEquals("3.0 ADD 5.0", l.toString());
	}
	
	@Test public void testSubtraction() throws ParserException, LexerException {
		l.parseToTokens("3 - 5");
		assertEquals("3.0 SUB 5.0", l.toString());
	}
	
	@Test public void testMultiplication() throws ParserException, LexerException {
		l.parseToTokens("3 * 5");
		assertEquals("3.0 MUL 5.0", l.toString());
	}
	
	@Test public void testDivision() throws ParserException, LexerException {
		l.parseToTokens("3 / 5");
		assertEquals("3.0 DIV 5.0", l.toString());
	}
	
	@Test public void testHardExpression() throws ParserException, LexerException {
		l.parseToTokens("ln(sin(x)^2 + cos(x)^2) + 9");
		assertEquals("ln LPAR sin LPAR x(0.0) RPAR POW 2.0 ADD cos LPAR x(0.0) RPAR POW 2.0 RPAR ADD 9.0", l.toString());
	}
	
	@Test public void testDecimalValues() throws ParserException, LexerException {
		l.parseToTokens("3.2 / 5.7");
		assertEquals("3.2 DIV 5.7", l.toString());
	}
	
	@Test public void negativeNumber() throws ParserException, LexerException {
		l.parseToTokens("-3^2");
		assertEquals("NEG 3.0 POW 2.0", l.toString());
	}
	
	@Test public void negativeNumber2() throws ParserException, LexerException {
		l.parseToTokens("-3^-2");
		assertEquals("NEG 3.0 POW NEG 2.0", l.toString());
	}
	
	@Test public void subtractingParenthised() throws ParserException, LexerException {
		l.parseToTokens("(2+3)-(4*2)");
		assertEquals("LPAR 2.0 ADD 3.0 RPAR SUB LPAR 4.0 MUL 2.0 RPAR", l.toString());
	}
	
	@Test public void manyVariables() throws ParserException, LexerException {
		l.parseToTokens("x*y+z");
		assertEquals("x(0.0) MUL y(0.0) ADD z(0.0)", l.toString());
	}
	
	@Test (expected=LexerException.class) 
	public void invalidDecimalValue() throws ParserException, LexerException {
		l.parseToTokens("3.328.2");
	}

	@Test public void EConstant() throws ParserException, LexerException {
		l.parseToTokens("e");
		assertEquals(Double.toString(Math.E), l.toString());
	}
	
	@Test public void PiConstant() throws ParserException, LexerException {
		l.parseToTokens("pi");
		assertEquals(Double.toString(Math.PI), l.toString());
	}
}
