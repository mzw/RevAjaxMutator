package jp.mzw.revajaxmutator.parser.javascript;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.tools.shell.Global;

public class JavaScriptParser {
	
	private AstRoot ast;
	
	public JavaScriptParser(File file) throws IOException {
		String src = FileUtils.readFileToString(file);
		InputStream is = JavaScriptParser.class.getClassLoader().getResourceAsStream("env.rhino.1.2.js");

		Global global = new Global(); 
		Context cx = ContextFactory.getGlobal().enterContext();
		global.init(cx);

		Scriptable scope = cx.initStandardObjects(global);
		cx.setOptimizationLevel(-1); // bypass 64kb size limit
		cx.evaluateReader(scope, new InputStreamReader(is), "env.rhino.js", 1, null);
		
		CompilerEnvirons ce = new CompilerEnvirons();
		ce.initFromContext(cx);
		org.mozilla.javascript.Parser parser = new org.mozilla.javascript.Parser(ce);
		
		ast = parser.parse(src, "", 1);
	}
	
	public AstRoot getAstRoot() {
		return this.ast;
	}
	
	public List<String> getFunctionNames() {
		final ArrayList<String> ret = new ArrayList<>();
		ast.visitAll(new NodeVisitor() {
			@Override
			public boolean visit(AstNode node) {
				if(node instanceof FunctionNode) {
					FunctionNode _node = (FunctionNode) node;
					String name = _node.getName();
					if(!"".equals(name)) {
						ret.add(name);
					}
				}
				return true;
			}
		});
		return ret;
	}
}
