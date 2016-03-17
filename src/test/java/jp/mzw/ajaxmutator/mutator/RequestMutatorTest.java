package jp.mzw.ajaxmutator.mutator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestMethodRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestOnSuccessHandlerRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestUrlRAMutator;
import jp.mzw.ajaxmutator.util.StringToAst;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAstRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestMutatorTest extends MutatorTestBase {
    private String[] urls;
    private String[] callbacks;
    private Collection<Request> requests;

    @Override
    public void prepare() {
        urls = new String[] { "'hoge.php'", "url" };
        callbacks = new String[] { "func1", "func2" };
        MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setRequestDetectors(
                ImmutableSet.of(new JQueryRequestDetector()));
        visitor = builder.build();
        inputs = new String[] {
                jQueryGet(urls[0], callbacks[0], null),
                jQueryPost(urls[1], callbacks[1], "{hoge: 'fuga'}")
        };
        requests = visitor.getRequests();
    }

    @Test
    public void testRequestUrlRAMutator() {
        Mutator<Request> mutator = new RequestUrlRAMutator(requests);
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(Iterables.get(requests, 0));
        assertEquals(urls[1], mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(requests, 1));
        assertEquals(urls[0], mutationList.get(0).getMutatingContent());

        mutator = new RequestUrlRAMutator(Sets.newHashSet(Iterables.limit(requests, 1)));
        mutationList = mutator.generateMutationList(Iterables.get(requests, 0));
        assertEquals("'http://google.com'", mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testRequestOnSuccessCallbackMutator() {
        Mutator<Request> mutator = new RequestOnSuccessHandlerRAMutator(requests);
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(Iterables.get(requests, 0));
        assertEquals(callbacks[1], mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(requests, 1));
        assertEquals(callbacks[0], mutationList.get(0).getMutatingContent());
    }

    @Test
    public void testRequestMethodRAMutator() {
        Mutator<Request> mutator = new RequestMethodRAMutator(requests);
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(Iterables.get(requests, 0));
        assertEquals("post", mutationList.get(0).getMutatingContent());
        mutationList = mutator.generateMutationList(Iterables.get(requests, 1));
        assertEquals("get", mutationList.get(0).getMutatingContent());

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testRequestMethodMutatorForAjaxMethod() {
        prepare();
        ast = parseAstRoot(
                "$.ajax('fuga.php', {type: 'PUT'});$.get('hoge.php', callback);");
        ast.visit(visitor);
        Mutator mutator = new RequestMethodRAMutator(visitor.getRequests());
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(
                Iterables.get(visitor.getRequests(), 0));
        assertEquals("\"GET\"", mutationList.get(0).getMutatingContent());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testCallbackReplacingMutatorForAjax() {
        prepare();
        ast = parseAstRoot(
                "$.ajax('fuga.php', {success: handleSuccess, error: function(e){console.log(e);}});"
                        + "$.post('just-put.php', {data: someValue});");
        ast.visit(visitor);
        Mutator mutator = new ReplacingAjaxCallbackMutator();
        List<Mutation> mutationList = mutator.generateMutationList(Iterables.get(visitor.getRequests(), 0));
        assertEquals(
                StringToAst.parseAsFunctionCall("$.ajax('fuga.php', {success: handleSuccess,"
                        + " error: function(e){console.log(e);}});").toSource(),
                mutationList.get(0).getOriginalNode().toSource());
        assertEquals(
                StringToAst.parseAsFunctionCall("$.ajax('fuga.php', "
                        + "{success: function(e){console.log(e);}, error: handleSuccess});").toSource(),
                mutationList.get(0).getMutatingContent());
        assertNull(mutator.generateMutationList(Iterables.get(visitor.getRequests(), 1)));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testPassBlankResponseMutator() {
        prepare();
        ast = parseAstRoot("$.getJSON('fuga.php', handleSuccess);");
        ast.visit(visitor);
        Mutator mutator = new FakeBlankResponseBodyMutator();
        List<Mutation> mutationList;
        mutationList = mutator.generateMutationList(Iterables.get(visitor.getRequests(), 0));
        assertEquals("handleSuccess", mutationList.get(0).getOriginalNode().toSource());
        assertEquals(
                "function(data, textStatus, jqXHR) {(handleSuccess).apply(this, [/* blank response mutation */'', textStatus, jqXHR]);}",
                mutationList.get(0).getMutatingContent());
    }

    private String jQueryRequest(
            String methodName, String url, String callback, String data) {
        return "$." + methodName + "(" + url + ", " + (data != null ? data +
                ", " : "") + callback + ");";
    }

    private String jQueryGet(String url, String callback, String data) {
        return jQueryRequest("get", url, callback, data);
    }

    private String jQueryPost(String url, String callback, String data) {
        return jQueryRequest("post", url, callback, data);
    }
}
