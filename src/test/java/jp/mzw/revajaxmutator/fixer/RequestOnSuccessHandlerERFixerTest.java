package jp.mzw.revajaxmutator.fixer;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.MutatorTestBase;

public class RequestOnSuccessHandlerERFixerTest extends MutatorTestBase{
	private String[] urls;
	private String[] callbacks;
	private Collection<Request> requests;
	
	@Override
	public void prepare() {
		urls = new String[] {"hoge.php", "url"};
		callbacks = new String[] {"func1", "func2"};
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
		visitor = builder.build();
		inputs = new String[] {
				jQueryGet(urls[0], callbacks[0], null), 
				jQueryPost(urls[1], callbacks[1], "{hoge: 'fuga'}")
		};
		requests = visitor.getRequests();
	}
	
	@Test
	public void testRequestOnSuccessHandlerERFixer() {
		Mutator<Request> fixer = new RequestOnSuccessHandlerERFixer(requests);
		List<Mutation> mutationList;
		mutationList = fixer.generateMutationList(Iterables.get(requests,  0));
		assertEquals(callbacks[1], mutationList.get(0).getMutatingContent());
		mutationList = fixer.generateMutationList(Iterables.get(requests, 1));
		assertEquals(callbacks[0], mutationList.get(0).getMutatingContent());
	}
	
	private String jQueryRequest(String methodName, String url, String callback, String data) {
		return "$." + methodName + "(" + url + ", " + (data != null ?data + ", " : "") + callback + ");";
	}
	
	private String jQueryGet(String url, String callback, String data) {
		return jQueryRequest("get", url, callback, data);
	}
	
	private String jQueryPost(String url, String callback, String data) {
		return jQueryRequest("post", url, callback, data);
	}
}
