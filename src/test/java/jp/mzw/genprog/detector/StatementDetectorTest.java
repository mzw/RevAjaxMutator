package jp.mzw.genprog.detector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jp.mzw.ajaxmutator.util.StringToAst;
import jp.mzw.ajaxmutator.detector.genprog.StatementDetector;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.WhileLoop;

public class StatementDetectorTest {
	

    @Test
    public void testAssignment() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			((ExpressionStatement) StringToAst.parseAstRoot(
    					"foo = 1;"
    					).getFirstChild()).getExpression());
    	
        assertTrue(result != null);
        assertEquals("foo", ((Assignment) result.getAstNode()).getLeft().toSource());
    }

    @Test
    public void testFunctionCall() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			((ExpressionStatement) StringToAst.parseAstRoot(
    					"foo();"
    					).getFirstChild()).getExpression());
    	
        assertTrue(result != null);
        assertEquals("foo", ((FunctionCall) result.getAstNode()).getTarget().toSource());
    }

    @Test
    public void testIfStatement() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			(AstNode) StringToAst.parseAstRoot(
    					"if(foo) { bar++; }"
    	    			).getFirstChild());
    	
        assertTrue(result != null);
        assertEquals("foo", ((IfStatement) result.getAstNode()).getCondition().toSource());
    }
    

    @Test
    public void testReturnStatement() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			(AstNode) ((FunctionNode) StringToAst.parseAstRoot(
    					"function foo() { return 0; }"
    	    			).getFirstChild()).getBody().getFirstChild());
        assertTrue(result != null);
        assertEquals("0", ((ReturnStatement) result.getAstNode()).getReturnValue().toSource());
    }
    
//    @Test
//    public void testArrayComprehensionLoop() {
//    	StatementDetector detector = new StatementDetector();
//    	Statement result = detector.detect(
//    			(AstNode) StringToAst.parseAstRoot(
//    					"[for (i of [ 1, 2, 3 ]) i*i ]"
//    			).getFirstChild());
//    	
//        assertTrue(result != null);
//        assertEquals("foo", ((IfStatement) result.getAstNode()).getCondition().toSource());
//    }

    @Test
    public void testDoLoop() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			(AstNode) StringToAst.parseAstRoot(
    					"do{ var foo = 1; } while (bar);"
    			).getFirstChild());
    	
        assertTrue(result != null);
        assertEquals("bar", ((DoLoop) result.getAstNode()).getCondition().toSource());
    }

    @Test
    public void testForInLoop() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			(AstNode) StringToAst.parseAstRoot(
    					"for(var foo in [1, 2, 3]) { bar++; };"
    			).getFirstChild());
    	
        assertTrue(result != null);
        assertEquals("var foo", ((ForInLoop) result.getAstNode()).getIterator().toSource());
    }

    @Test
    public void testForLoop() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			(AstNode) StringToAst.parseAstRoot(
    					"for(var foo = 0; foo < 10; foo++) { bar++; };"
    			).getFirstChild());
    	
        assertTrue(result != null);
        assertEquals("foo < 10", ((ForLoop) result.getAstNode()).getCondition().toSource());
    }
    
//    @Test
//    public void testGeneratorExpressionLoop() {
//    	StatementDetector detector = new StatementDetector();
//    	Statement result = detector.detect(
//    			(AstNode) StringToAst.parseAstRoot(
//    					"(obj[x] for (x in obj))"
//    			).getFirstChild());
//    	
//        assertTrue(result != null);
//        assertEquals("foo < 10", ((GeneratorExpressionLoop) result.getAstNode()).getIterator().toSource());
//    }

    @Test
    public void testWhileLoop() {
    	StatementDetector detector = new StatementDetector();
    	Statement result = detector.detect(
    			(AstNode) StringToAst.parseAstRoot(
    					"while(foo) { bar++; }"
    			).getFirstChild());
    	
        assertTrue(result != null);
        assertEquals("foo", ((WhileLoop) result.getAstNode()).getCondition().toSource());
    }
    
    
}
