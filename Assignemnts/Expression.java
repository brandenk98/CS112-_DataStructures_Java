package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is created 
	 * and stored, even if it appears more than once in the expression.
	 * At this time, values for all variables and all array items are set to
	 * zero - they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr The expression
	 * @param vars The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void 
	makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		
		boolean duplicate=false;
		String expression="";
		for(int i = 0; i < expr.length(); i++){
			if((int)expr.charAt(i) != 32 && (int)expr.charAt(i) != 9){
				expression += expr.charAt(i);
			}
		}

		StringTokenizer st = new StringTokenizer(expr,delims,true);
		String [] tokens = new String [st.countTokens()];
		for(int i = 0; i<tokens.length; i++){
			tokens[i]=st.nextToken();
		}
		for (int i=0; i<tokens.length-1; i++){
			if(Character.isLetter(tokens[i].charAt(0))){
				if (tokens[i+1].charAt(0) == '['){
					Array arr = new Array(tokens[i]); 
					for(int j=0; j<arrays.size(); j++){
						if(arr.name.equals(arrays.get(j).name)){
							duplicate=true;
						}
					}
					if(duplicate == false){
						arrays.add(arr);
					}
				}
				else {
					Variable var=new Variable(tokens[i]);
					for(int j =0;j<vars.size();j++){
						if(var.name.equals(vars.get(j).name)){
							duplicate =true;
						}
					}
					if(duplicate == false){
						vars.add(var);	
					}

				}
			}
			duplicate=false;
		}


		if(Character.isLetter(tokens[tokens.length-1].charAt(0))){
			Variable var = new Variable(tokens[tokens.length-1]);
			for(int j =0;j<vars.size();j++){
				if(var.name.equals(vars.get(j).name)){
					duplicate =true;
				}
			}
			if(duplicate==false){
				vars.add(var);
			}
		}

	}


	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input 
	 * @param vars The variables array list, previously populated by makeVariableLists
	 * @param arrays The arrays array list - previously populated by makeVariableLists
	 */
	public static void 
	loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { 
				vars.get(vari).value = num;
			} else { 
				arr = arrays.get(arri);
				arr.values = new int[num];
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok," (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;              
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars The variables array list, with values for all variables in the expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float 
	evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		String expression="";
		for(int i=0; i <expr.length();i++){
			if((int)expr.charAt(i)!=32&&(int)expr.charAt(i)!=9){
				expression+=expr.charAt(i);
			}
		}
		StringTokenizer st= new StringTokenizer(expression,delims,true);
		String [] tokens=new String [st.countTokens()];
		for(int i = 0;i<tokens.length;i++){
			tokens[i]=st.nextToken();
		}
		for(int i=0;i<vars.size();i++){
			for(int j=0;j<tokens.length;j++){
				if(vars.get(i).name.equals(tokens[j])){
					tokens[j]= Integer.toString(vars.get(i).value);
				}
			}
		}
		Stack<String> numbers=new Stack<String>();
		Stack<String> operators=new Stack<String>();
		Stack<String> tempnumbers=new Stack<String>();
		Stack<String> tempoperators=new Stack<String>();
		for(int i =0;i<tokens.length;i++){
		}	
		int temp=0;
		int order=0;
		int narrays=0;
		int brakets=0;
		for(int i=0;i<tokens.length;i++){
			if(tokens[i].charAt(0)=='('){
				for(int j=i;j<tokens.length;j++){
					if(tokens[j].indexOf('(')!=-1){
						brakets++;
					}
					if(tokens[j].indexOf(')')!=-1){
						brakets--;
						if(brakets==0){
							temp=j;
							numbers.push((String) evaluate(tokens,i+1,temp-1,vars,arrays));
							i=j;

							break;
						}
					}
				}
			}	
			else if(tokens[i].charAt(0)=='['){
				for(int j=i;j<tokens.length;j++){
					if(tokens[j].indexOf('[')!=-1){
						brakets++;
					}
					if(tokens[j].indexOf(']')!=-1){
						brakets--;
						if(brakets==0){
							temp=j;
							numbers.push((String) evaluate(tokens,i+1,temp-1,vars,arrays));
							i=j;
							narrays++;
							operators.push("[");
							break;
						}
					}
				}
			}
			else if(tokens[i].indexOf('+')!=-1||tokens[i].indexOf('-')!=-1||tokens[i].indexOf('*')!=-1||tokens[i].indexOf('/')!=-1){
				if(tokens[i].indexOf('*')!=-1||tokens[i].indexOf('/')!=-1){
					order++;
				}
				operators.push(tokens[i]);
			}
			else{numbers.push(tokens[i]);
			}
		}
		float var1=0;
		float var2=0;
		int[] holder;
		while(narrays>0){
			if(operators.peek().indexOf('[')!=-1){
				narrays--;
				operators.pop();
				var1=Float.parseFloat(numbers.pop());
				for(int i =0;i<arrays.size();i++){
					if(arrays.get(i).name.equals(numbers.peek())){
						numbers.pop();
						holder=(arrays.get(i).values);
						numbers.push(Integer.toString(holder[(int)var1]));
						break;
					}
				}
			}
			else{tempnumbers.push(numbers.pop());
			tempoperators.push(operators.pop());
			}
		}
		tempnumbers.push(numbers.pop());
		while(tempoperators.size()>0){
			operators.push(tempoperators.pop());
			numbers.push(tempnumbers.pop());
		}
		numbers.push(tempnumbers.pop());
		while (operators.size()>0){
			if(operators.peek().indexOf('*')!=-1){
				operators.pop();
				var2=Float.parseFloat(numbers.pop());
				var1=Float.parseFloat(numbers.pop());
				numbers.push(Float.toString((var2*var1)));
				order--;
			}
			else if(operators.peek().indexOf('/')!=-1){
				operators.pop();
				var2=Float.parseFloat(numbers.pop());
				var1=Float.parseFloat(numbers.pop());
				numbers.push(Float.toString((var1/var2)));
				order--;
			}
			else{
				tempoperators.push(operators.pop());
				tempnumbers.push(numbers.pop());
			}
		}
		tempnumbers.push(numbers.pop());
		while(tempoperators.size()>0){
			operators.push(tempoperators.pop());
			numbers.push(tempnumbers.pop());
		}
		numbers.push(tempnumbers.pop());

		while(operators.size()>0){
			tempoperators.push(operators.pop());
			tempnumbers.push(numbers.pop());
		}
		tempnumbers.push(numbers.pop());
		while (tempnumbers.size()>1){
			if(tempoperators.peek().indexOf('+')!=-1){
				tempoperators.pop();
				var2=Float.parseFloat(tempnumbers.pop());
				var1=Float.parseFloat(tempnumbers.pop());
				tempnumbers.push(Float.toString((var2+var1)));
			}
			else if(tempoperators.peek().indexOf('-')!=-1){
				tempoperators.pop();
				var2=Float.parseFloat(tempnumbers.pop());
				var1=Float.parseFloat(tempnumbers.pop());
				tempnumbers.push(Float.toString((var2-var1)));
			}
		}
		numbers.push(tempnumbers.pop());
		return Float.parseFloat(numbers.pop());

	}

	private static String evaluate (String[] tokens, int fIndex, int lIndex,ArrayList<Variable> vars, ArrayList<Array> arrays){
		Stack<String> numbers=new Stack<String>();
		Stack<String> operators=new Stack<String>();
		Stack<String> tempnumbers=new Stack<String>();
		Stack<String> tempoperators=new Stack<String>();
		int temp=0;
		int order=0;
		int narrays=0;
		int brakets=0;
		for(int i=fIndex;i<=lIndex;i++){

			if(tokens[i].charAt(0)=='('){
				for(int j=i;j<tokens.length;j++){
					if(tokens[j].indexOf('(')!=-1){
						brakets++;
					}
					if(tokens[j].indexOf(')')!=-1){
						brakets--;
						if(brakets==0){
							temp=j;

							numbers.push((String) evaluate(tokens,i+1,temp-1,vars,arrays));
							i=j;
							break;
						}
					}
				}
			}
			else if(tokens[i].charAt(0)=='['){
				for(int j=i;j<tokens.length;j++){
					if(tokens[j].indexOf('[')!=-1){
						brakets++;
					}
					if(tokens[j].indexOf(']')!=-1){
						brakets--;
						if(brakets==0){
							temp=j;
							numbers.push((String) evaluate(tokens,i+1,temp-1,vars,arrays));
							i=j;
							narrays++;
							operators.push("[");
							break;
						}
					}
				}
			}
			else if(tokens[i].indexOf('+')!=-1||tokens[i].indexOf('-')!=-1||tokens[i].indexOf('*')!=-1||tokens[i].indexOf('/')!=-1){
				if(tokens[i].indexOf('*')!=-1||tokens[i].indexOf('/')!=-1){
					order++;
				}
				operators.push(tokens[i]);
			}
			else{numbers.push(tokens[i]);
			}
		}
		float var1=0;
		float var2=0;
		int[] holder;
		while(narrays>0){
			if(operators.peek().indexOf('[')!=-1){
				narrays--;
				operators.pop();
				var1=Float.parseFloat(numbers.pop());
				for(int i =0;i<arrays.size();i++){
					if(arrays.get(i).name.equals(numbers.peek())){
						numbers.pop();
						holder=(arrays.get(i).values);
						numbers.push(Integer.toString(holder[(int)var1]));
						break;
					}
				}
			}
			else{tempnumbers.push(numbers.pop());
			tempoperators.push(operators.pop());
			}
		}
		tempnumbers.push(numbers.pop());
		while(tempoperators.size()>0){
			operators.push(tempoperators.pop());
			numbers.push(tempnumbers.pop());
		}
		numbers.push(tempnumbers.pop());
		while (operators.size()>0){
			if(operators.peek().indexOf('*')!=-1){
				operators.pop();
				var2=Float.parseFloat(numbers.pop());
				var1=Float.parseFloat(numbers.pop());
				numbers.push(Float.toString((var2*var1)));
				order--;
			}
			else if(operators.peek().indexOf('/')!=-1){
				operators.pop();
				var2=Float.parseFloat(numbers.pop());
				var1=Float.parseFloat(numbers.pop());
				numbers.push(Float.toString((var1/var2)));
				order--;
			}
			else{
				tempoperators.push(operators.pop());
				tempnumbers.push(numbers.pop());
			}
		}
		tempnumbers.push(numbers.pop());

		while(tempoperators.size()>0){
			operators.push(tempoperators.pop());
			numbers.push(tempnumbers.pop());
		}
		numbers.push(tempnumbers.pop());

		while(operators.size()>0){
			tempoperators.push(operators.pop());
			tempnumbers.push(numbers.pop());
		}
		tempnumbers.push(numbers.pop());
		while (tempnumbers.size()>1){	
			if(tempoperators.peek().indexOf('+')!=-1){
				tempoperators.pop();
				var2=Float.parseFloat(tempnumbers.pop());
				var1=Float.parseFloat(tempnumbers.pop());
				tempnumbers.push(Float.toString((var2+var1)));
			}
			else if(tempoperators.peek().indexOf('-')!=-1){
				tempoperators.pop();
				var2=Float.parseFloat(tempnumbers.pop());
				var1=Float.parseFloat(tempnumbers.pop());
				tempnumbers.push(Float.toString((var2-var1)));
			}
		}
		numbers.push(tempnumbers.pop());
		return numbers.pop();
	}
}