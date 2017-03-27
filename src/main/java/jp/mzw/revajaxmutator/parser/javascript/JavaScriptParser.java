package jp.mzw.revajaxmutator.parser.javascript;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.tools.shell.Global;

import com.google.common.collect.ImmutableSet;

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

	// Same as those implemented in jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector
    private final Set<String> globalAttributes = ImmutableSet.of("accessKey",
            "class", "dir", "id", "lang", "style", "tabindex", "title",
            "contenteditable", "contextmenu", "draggable", "dropzone",
            "hidden", "spellcheck");
    private final Set<String> attributes = ImmutableSet.of("abbr",
            "accept-charset", "accept", "action", "align", "alink", "alt",
            "archive", "axis", "background", "bgcolor", "border",
            "cellpadding", "cellspacing", "char", "charoff", "charset",
            "checked", "cite", "classid", "clear", "code", "codebase",
            "codetype", "color", "cols", "colspan", "compact", "content",
            "coords", "data", "datetime", "declare", "defer", "disabled",
            "enctype", "face", "for", "frame", "frameborder", "headers",
            "height", "href", "hreflang", "hspace", "http-equiv", "id",
            "ismap", "label", "language", "link", "longdesc", "marginheight",
            "marginwidth", "maxlength", "media", "method", "multiple", "name",
            "nohref", "noresize", "noshade", "nowrap", "object", "profile",
            "prompt", "readonly", "rel", "rev", "rows", "rowspan", "rules",
            "scheme", "scope", "scrolling", "selected", "shape", "size",
            "span", "src", "standby", "start", "summary", "target", "text",
            "type", "usemap", "valign", "value", "valuetype", "version",
            "vlink", "vspace", "width");
    /**
     * Detect specific attribute values from infix expressions
     * because {@link jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector} works for only Assignment
     * @return
     */
	public List<String> getAttributeValuesFromInfixExpression() {
		final ArrayList<String> ret = new ArrayList<>();
		ast.visitAll(new NodeVisitor() {
			@Override
			public boolean visit(AstNode node) {
				if(node instanceof InfixExpression) {
					InfixExpression _node = (InfixExpression) node;
					try { // only for node with operator
						InfixExpression.operatorToString(_node.getOperator());
						if(_node.getLeft() instanceof PropertyGet) {
							PropertyGet _left_node = (PropertyGet) _node.getLeft();
							String value = _node.getRight().toSource();
							for(String attr : globalAttributes) {
								if(attr.equals(_left_node.getProperty().toSource())) {
									if(!ret.contains(value)) {
										ret.add(value);
										break;
									}
								}
							}
							for(String attr : attributes) {
								if(attr.equals(_left_node.getProperty().toSource())) {
									if(!ret.contains(value)) {
										ret.add(value);
										break;
									}
								}
							}
						}
					} catch (IllegalArgumentException e) {
						// NOP
					}
				}
				return true;
			}
		});
		return ret;
	}
}
