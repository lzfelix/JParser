package tests;

import static org.junit.Assert.*;
import lexer.Lexer;

import org.junit.Before;
import org.junit.Test;

public class ParserTests {
	Lexer l;


	@Test 
	public void testSingleDigit() {
		l = new Lexer("cos(x)^2 + sin(x)^2 - 1");
		System.out.println(l.toString());
	}

}
