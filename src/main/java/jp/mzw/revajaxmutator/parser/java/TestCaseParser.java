package jp.mzw.revajaxmutator.parser.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;

public class TestCaseParser {

	protected List<ASTNode> nodes;

	public TestCaseParser(File file) throws IOException {
		String src = FileUtils.readFileToString(file);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(src.toCharArray());
		CompilationUnit cu = (CompilationUnit) parser
				.createAST(new NullProgressMonitor());
		AllElementsFindVisitor visitor = new AllElementsFindVisitor();
		cu.accept(visitor);
		nodes = visitor.getNodes();
	}

	public List<ASTNode> getNodes() {
		return this.nodes;
	}

	public List<String> getIdValues() {
		ArrayList<String> values = new ArrayList<>();
		for (ASTNode node : nodes) {
			if (node instanceof MethodInvocation) {
				MethodInvocation _node = (MethodInvocation) node;
				if ("id".equals(_node.getName().toString())) {
					Object arg = _node.arguments().get(0);
					if (arg instanceof StringLiteral) {
						StringLiteral _arg = (StringLiteral) arg;
						String modified = "\"#" + _arg.getLiteralValue() + "\"";
						values.add(modified);
					}
				}
			}
		}
		return values;
	}

	public List<String> getTagNames() {
		ArrayList<String> values = new ArrayList<>();
		for (ASTNode node : nodes) {
			if (node instanceof MethodInvocation) {
				MethodInvocation _node = (MethodInvocation) node;
				if ("tagName".equals(_node.getName().toString())) {
					Object arg = _node.arguments().get(0);
					if (arg instanceof StringLiteral) {
						StringLiteral _arg = (StringLiteral) arg;
						String modified = "\"" + _arg.getLiteralValue() + "\"";
						values.add(modified);
					}
				}
			}
		}
		return values;
	}

	public List<String> getClassValues() {
		ArrayList<String> values = new ArrayList<>();
		for (ASTNode node : nodes) {
			if (node instanceof MethodInvocation) {
				MethodInvocation _node = (MethodInvocation) node;
				if ("className".equals(_node.getName().toString())) {
					Object arg = _node.arguments().get(0);
					if (arg instanceof StringLiteral) {
						StringLiteral _arg = (StringLiteral) arg;
						String modified = "\"." + _arg.getLiteralValue() + "\"";
						values.add(modified);
					}
				}
			}
		}
		return values;
	}

	public List<String> getAttributeValues() {
		ArrayList<String> ret = new ArrayList<>();
		ret.addAll(getIdValues());
		ret.addAll(getClassValues());
		return ret;
	}

}
