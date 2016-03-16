package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;

public class RequestURLVIFixer extends AbstractReplacingAmongFixer<Request> {
	    public RequestURLVIFixer(Collection<Request> mutationTargets, String[] parseResult) {
	        super(Request.class, mutationTargets, parseResult);
	    }

	    @Override
	    protected AstNode getFocusedNode(Request node) {
	        return node.getUrl();
	    }

	    @Override
	    public AstNode getDefaultReplacingNode() {
	        return StringToAst.parseAsStringLiteral("'http://google.com'");
	    }
	}
