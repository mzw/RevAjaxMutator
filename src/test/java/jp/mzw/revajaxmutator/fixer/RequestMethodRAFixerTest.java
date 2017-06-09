package jp.mzw.revajaxmutator.fixer;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAstRoot;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.mutatable.Request;

public class RequestMethodRAFixerTest {

	@Test
	public void testGetDefaultReplacingNodeFromGetToPost() {
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
		MutateVisitor visitor = builder.build();

		StringBuilder source = new StringBuilder();
		source.append("$.get('url', callback);");
		AstRoot ast = parseAstRoot(source.toString());
		ast.visit(visitor);

		Set<Request> requests = visitor.getRequests();
		RequestMethodRAFixer fixer = new RequestMethodRAFixer(requests);
		AstNode focusedNode = fixer.getFocusedNode(Iterables.get(requests, 0));

		Assert.assertArrayEquals("get".toCharArray(), focusedNode.toSource().toCharArray());
		Assert.assertArrayEquals("'post'".toCharArray(), fixer.getDefaultReplacingNode(focusedNode).toSource().toCharArray());
	}

	@Test
	public void testGetDefaultReplacingNodeFromPostToGet() {
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
		MutateVisitor visitor = builder.build();

		StringBuilder source = new StringBuilder();
		source.append("$.post('url', callback);");
		AstRoot ast = parseAstRoot(source.toString());
		ast.visit(visitor);

		Set<Request> requests = visitor.getRequests();
		RequestMethodRAFixer fixer = new RequestMethodRAFixer(requests);
		AstNode focusedNode = fixer.getFocusedNode(Iterables.get(requests, 0));

		Assert.assertArrayEquals("post".toCharArray(), focusedNode.toSource().toCharArray());
		Assert.assertArrayEquals("'get'".toCharArray(), fixer.getDefaultReplacingNode(focusedNode).toSource().toCharArray());
	}
}
