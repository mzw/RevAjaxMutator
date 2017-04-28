package jp.mzw.revajaxmutator.parser.javascript;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.tools.shell.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class JavaScriptParser {
	protected static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptParser.class);

	/**
	 * Root of abstract syntax tree obtained by parsing given JavaScript file
	 */
	private final AstRoot ast;

	/**
	 * Parse given JavaScript file
	 *
	 * @param file
	 * @throws IOException
	 *             causes when given file does not exist
	 */
	public JavaScriptParser(final File file) throws IOException {
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

	/**
	 * Get AST root
	 *
	 * @return AST root
	 */
	public AstRoot getAstRoot() {
		return this.ast;
	}

	/**
	 * Get name set of functions implemented in given JavaScript file
	 *
	 *
	 * @return set of function names
	 */
	public Set<String> getFunctionNames() {
		final Set<String> ret = Sets.newHashSet();
		this.ast.visitAll(node -> {
			if (node instanceof FunctionNode) {
				final FunctionNode _node = (FunctionNode) node;
				final String name = _node.getName();
				if (!"".equals(name)) {
					ret.add(name);
				}
			}
			return true;
		});
		return ret;
	}

	/**
	 * Same as those implemented in
	 *
	 * @see jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector
	 */
	private final Set<String> globalAttributes = ImmutableSet.of("accessKey", "class", "dir", "id", "lang", "style",
			"tabindex", "title", "contenteditable", "contextmenu", "draggable", "dropzone", "hidden", "spellcheck");

	/**
	 * Same as those implemented in
	 *
	 * {@see jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector}
	 */
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
	 * {@see jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector} works
	 * for only Assignment
	 *
	 * @return
	 */
	public Set<String> getAttributeValuesFromInfixExpression() {
		final Set<String> ret = Sets.newHashSet();
		this.ast.visitAll(node -> {
			if (node instanceof InfixExpression) {
				final InfixExpression _node = (InfixExpression) node;
				try { // only for node with operator
					InfixExpression.operatorToString(_node.getOperator());
					if (_node.getLeft() instanceof PropertyGet) {
						final PropertyGet _left_node = (PropertyGet) _node.getLeft();
						final String value = _node.getRight().toSource();
						for (final String attr1 : JavaScriptParser.this.globalAttributes) {
							if (attr1.equals(_left_node.getProperty().toSource())) {
								if (!ret.contains(value)) {
									ret.add(value);
									break;
								}
							}
						}
						for (final String attr3 : JavaScriptParser.this.attributes) {
							if (attr3.equals(_left_node.getProperty().toSource())) {
								if (!ret.contains(value)) {
									ret.add(value);
									break;
								}
							}
						}
						for (final String attr2 : JavaScriptParser.this.attributes) {
							if (attr2.equals(_left_node.getProperty().toSource())) {
								if (!ret.contains(value)) {
									System.out.println(attr2 + ", " + value + ", " + node.toSource());
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
		});
		return ret;
	}

	/**
	 * Native: {@code addEventListener} and {@code attachEvent} jQuery:
	 * {@link <a href=
	 * "http://api.jquery.com/category/events/event-handler-attachment/">link</a>}
	 */
	private static final Set<String> eventHandlerKeywords = ImmutableSet.of("addEventListener", "attachEvent", "on",
			"off", "bind", "unbind", "delegate", "undelegate", "error", "live", "load", "unload", "one", "trigger");

	/**
	 * List taken from
	 * {@link <a href="http://www.quirksmode.org/dom/events/">link</a>}
	 */
	private static final Set<String> eventTypeKeywords = ImmutableSet.of("blue", "change", "click", "dblclick",
			"contextmenu", "focus", "focusin", "focusout", "hover", "keydown", "keypress", "keyup", "mousedown",
			"mouseenter", "mouseleave", "mouseremove", "mouseout", "mouseover", "mouseup", "mousewheel", "copy", "cut",
			"paste", "resize", "scroll", "select", "submit", "unload");

	/**
	 * Get set of event types implemented in given JavaScript file
	 *
	 * @return
	 */
	public Set<String> getEventTypes() {
		final Set<String> ret = Sets.newHashSet();
		this.ast.visitAll(node -> {
			if (node instanceof FunctionCall) {
				final FunctionCall functionCall = (FunctionCall) node;
				// Get the name of the function
				final Name name = this.parseFunctionCall(functionCall);
				if (name == null) {
					// It's an anonymous function
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
					if (eventTypeKeywords.contains(name.getIdentifier())) {
						ret.add(name.getIdentifier());
					}
				}
			}
			return true;
		});
		return ret;
	}

	/**
	 * Get name of function call
	 *
	 * @param functionCall
	 * @return
	 */
	// private static Name getFunctionCallName(FunctionCall functionCall) {
	// if (functionCall.getTarget() instanceof Name) {
	// return (Name) functionCall.getTarget();
	// } else if (functionCall.getTarget() instanceof PropertyGet) {
	// return ((PropertyGet) functionCall.getTarget()).getProperty();
	// }
	// LOGGER.warn("Unknown type: {}",
	// functionCall.getTarget().getClass().getName());
	// return null;
	// }

	private Name parseFunctionCall(FunctionCall functionCall) {
		// System.out.println(functionCall.getTarget().getClass() + " " +
		// functionCall.getTarget().getLineno());
		if (functionCall.getTarget() instanceof FunctionCall) {
			return functionCall.getTarget().getEnclosingFunction().getFunctionName();
		} else if (functionCall.getTarget() instanceof Name || functionCall.getTarget() instanceof FunctionCall) {
			return (Name) functionCall.getTarget();
		}
		// Ignore edge cases such as, ElementGet calls, e.g.,
		// "this.onloads[n]();"
		else if (functionCall.getTarget() instanceof ElementGet
				|| functionCall.getTarget() instanceof ParenthesizedExpression) {
			return null;
		} else {
			return ((PropertyGet) functionCall.getTarget()).getProperty();
		}
	}
}