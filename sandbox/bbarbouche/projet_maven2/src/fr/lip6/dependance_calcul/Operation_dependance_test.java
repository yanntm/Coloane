package fr.lip6.dependance_calcul;
import junit.framework.TestCase;

public class Operation_dependance_test extends TestCase{
	
	public int a=5;
	public Operation_dependance op=new Operation_dependance();
	
	
	
	
	public void testfactorielle(){
		int resultat=5*4*3*2;
		assertEquals(resultat,op.factorielle(a));
	}
	
	
	
	
	public void testcarre(){
		int resultat=a*a;
		assertEquals(resultat,op.carre(a));
	}
	
	

}
