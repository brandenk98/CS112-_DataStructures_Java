<html><head></head><body data-gr-c-s-loaded="true"><pre style="word-wrap: break-word; white-space: pre-wrap;">package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * &lt;pre&gt;
	 *     &lt;coeff&gt; &lt;degree&gt;
	 *     &lt;coeff&gt; &lt;degree&gt;
	 *     ...
	 *     &lt;coeff&gt; &lt;degree&gt;
	 * &lt;/pre&gt;
	 * with the guarantee that degrees will be in descending order. For example:
	 * &lt;pre&gt;
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * &lt;/pre&gt;
	 * which represents the polynomial:
	 * &lt;pre&gt;
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * &lt;/pre&gt;
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node removeZeros(Node poly){
		while(poly.term.coeff == 0) {
			poly = poly.next;
			}
		Node prev = poly,iter = poly.next;
		while(iter != null) {
			if(iter.term.coeff == 0){
				prev.next = prev.next.next;
			}else
					prev = prev.next;
				iter = iter.next;
			}
		return poly;
		}
	
	public static Node add(Node poly1, Node poly2) {
		float coeff = 0;
		int degree = 0;
		Node polyAdd;
		
		if(poly1.term.degree &lt; poly2.term.degree) {
			polyAdd = new Node(poly1.term.coeff, poly1.term.degree, null);
			poly1 = poly1.next;
		} else if(poly1.term.degree &gt; poly2.term.degree) {
			polyAdd = new Node(poly1.term.coeff, poly1.term.degree, null);
			poly1 = poly1.next;
		} else {
			polyAdd = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, null);
			poly1 = poly1.next;
			poly2 = poly2.next;
		}
		
		Node ret = polyAdd;
		
		while(poly1 != null &amp;&amp; poly2 != null){
			if(poly1.term.degree == poly2.term.degree){
				degree = poly1.term.degree;
				coeff = poly1.term.coeff + poly2.term.coeff;
				poly1 = poly1.next;
				poly2 = poly2.next;
			}else if(poly1.term.degree &gt; poly2.term.degree &amp;&amp; poly1.term.degree &lt; poly2.next.term.degree){
				degree = poly1.term.degree;
				coeff = poly1.term.coeff;
				poly1 = poly1.next;
			}
			else if(poly1.term.degree &gt; poly2.term.degree &amp;&amp; poly2.next == null){
				degree = poly2.term.degree;
				coeff = poly2.term.degree;
				poly2 = poly2.next;
			}
			polyAdd.next = new Node(coeff,degree,null);
			polyAdd = polyAdd.next;	
		}
		
		return ret;
	}
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node total = null;
		for(Node iter = poly1; iter != null; iter = iter.next) {
			Node dist = null, distIter = dist;
			
			for(Node iter2 = poly2; iter2 != null; iter2 = iter2.next) {
				distIter = new Node(iter.term.coeff * iter2.term.coeff, iter.term.degree + iter2.term.degree, null);
				distIter = distIter.next;
			}
			
			total = add(dist, total);
		}
		return total;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float answer = 0;
		for(Node poly1 = poly; poly1 != null; poly1 = poly1.next){
			answer += poly1.term.coeff * (Math.pow(x, poly1.term.degree));
		}
		return answer;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
</pre></body></html>