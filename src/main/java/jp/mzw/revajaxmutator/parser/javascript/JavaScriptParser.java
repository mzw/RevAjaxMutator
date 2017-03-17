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
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.tools.shell.Global;

import com.google.common.collect.ImmutableSet;

public class JavaScriptParser {

	private final AstRoot ast;

	public JavaScriptParser(File file) throws IOException {
		final String src = FileUtils.readFileToString(file);
		final InputStream is = JavaScriptParser.class.getClassLoader().getResourceAsStream("env.rhino.1.2.js");

		final Global global = new Global();
		final Context cx = ContextFactory.getGlobal().enterContext();
		global.init(cx);

		final Scriptable scope = cx.initStandardObjects(global);
		cx.setOptimizationLevel(-1); // bypass 64kb size limit
		cx.evaluateReader(scope, new InputStreamReader(is), "env.rhino.js", 1, null);

		final CompilerEnvirons ce = new CompilerEnvirons();
		ce.initFromContext(cx);
		final org.mozilla.javascript.Parser parser = new org.mozilla.javascript.Parser(ce);

		this.ast = parser.parse(src, "", 1);
	}

	public AstRoot getAstRoot() {
		return this.ast;
	}

	public List<String> getFunctionNames() {
		final ArrayList<String> ret = new ArrayList<>();
		this.ast.visitAll(new NodeVisitor() {
			@Override
			public boolean visit(AstNode node) {
				if (node instanceof FunctionNode) {
					final FunctionNode _node = (FunctionNode) node;
					final String name = _node.getName();
					if (!"".equals(name)) {
						ret.add(name);
					}
				}
				return true;
			}
		});
		return ret;
	}

	// Same as those implemented in
	// jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector
	private final Set<String> globalAttributes = ImmutableSet.of("accessKey", "class", "dir", "id", "lang", "style",
			"tabindex", "title", "contenteditable", "contextmenu", "draggable", "dropzone", "hidden", "spellcheck");
	private final Set<String> attributes = ImmutableSet.of("abbr", "accept-charset", "accept", "action", "align",
			"alink", "alt", "archive", "axis", "background", "bgcolor", "border", "cellpadding", "cellspacing", "char",
			"charoff", "charset", "checked", "cite", "classid", "clear", "code", "codebase", "codetype", "color",
			"cols", "colspan", "compact", "content", "coords", "data", "datetime", "declare", "defer", "disabled",
			"enctype", "face", "for", "frame", "frameborder", "headers", "height", "href", "hreflang", "hspace",
			"http-equiv", "id", "ismap", "label", "language", "link", "longdesc", "marginheight", "marginwidth",
			"maxlength", "media", "method", "multiple", "name", "nohref", "noresize", "noshade", "nowrap", "object",
			"profile", "prompt", "readonly", "rel", "rev", "rows", "rowspan", "rules", "scheme", "scope", "scrolling",
			"selected", "shape", "size", "span", "src", "standby", "start", "summary", "target", "text", "type",
			"usemap", "valign", "value", "valuetype", "version", "vlink", "vspace", "width");

	/**
	 * Detect specific attribute values from infix expressions because
	 * {@link jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector} works
	 * for only Assignment
	 *
	 * @return
	 */
	public List<String> getAttributeValuesFromInfixExpression() {
		final ArrayList<String> ret = new ArrayList<>();
		this.ast.visitAll(new NodeVisitor() {
			@Override
			public boolean visit(AstNode node) {
				if (node instanceof InfixExpression) {
					final InfixExpression _node = (InfixExpression) node;
					try { // only for node with operator
						InfixExpression.operatorToString(_node.getOperator());
						if (_node.getLeft() instanceof PropertyGet) {
							final PropertyGet _left_node = (PropertyGet) _node.getLeft();
							final String value = _node.getRight().toSource();
							for (final String attr : JavaScriptParser.this.globalAttributes) {
								if (attr.equals(_left_node.getProperty().toSource())) {
									if (!ret.contains(value)) {
										ret.add(value);
										break;
									}
								}
							}
							for (final String attr : JavaScriptParser.this.attributes) {
								if (attr.equals(_left_node.getProperty().toSource())) {
									if (!ret.contains(value)) {
										System.out.println(attr + ", " + value + ", " + node.toSource());
										ret.add(value);
										break;
									}
								}
							}
						}
					} catch (final IllegalArgumentException e) {
						// NOP
					}
				}
				return true;
			}
		});
		return ret;
	}

	private static final Set<String> eventHandlerKeywords = ImmutableSet.of("addEventListener", "on", "off", "bind",
			"unbind", "delegate", "undelegate", "error", "live", "load", "unload", "one", "trigger");
	// list taken from http://www.quirksmode.org/dom/events/
	private static final Set<String> eventTypeKeywords = ImmutableSet.of("blue", "change", "click", "dblclick",
			"contextmenu", "focus", "focusin", "focusout", "hover", "keydown", "keypress", "keyup", "mousedown",
			"mouseenter", "mouseleave", "mouseremove", "mouseout", "mouseover", "mouseup", "mousewheel", "copy", "cut",
			"paste", "resize", "scroll", "select", "submit", "unload");

	public List<String> getEventTypes() {
		final ArrayList<String> ret = new ArrayList<>();
		this.ast.visitAll(new NodeVisitor() {
			@Override
			public boolean visit(AstNode node) {
				if (node instanceof FunctionCall) {
					final FunctionCall functionCall = (FunctionCall) node;
					// Get the name of the function
					final Name name = ((PropertyGet) functionCall.getTarget()).getProperty();
					if (name == null) {
						// it's an anonymous function
						return true;
					}

					// Check if it is an event handler
					if (eventHandlerKeywords.contains(name.getIdentifier())) {
						for (final AstNode a : functionCall.getArguments()) {
							if (a instanceof StringLiteral) {
								final String argText = ((StringLiteral) a).getValue().toLowerCase();
								if (eventTypeKeywords.contains(argText)) {
									ret.add(argText);
								}
							}
						}
					}
				}
				return true;
			}
		});
		return ret;
	}
}