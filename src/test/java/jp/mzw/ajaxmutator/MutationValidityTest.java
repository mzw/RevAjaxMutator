package jp.mzw.ajaxmutator;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.generator.*;
import jp.mzw.ajaxmutator.mutatable.Mutatable;
import jp.mzw.ajaxmutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.generic.ChangeConditionSwitchStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.ChangeValueVariableDecMutator;
import jp.mzw.ajaxmutator.mutator.generic.IfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveBreakContinueForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveBreakContinueWhileLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveBreakFromSwitchStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveElseIfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveParamsFromFuncNodeMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveReturnStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveVariableDeclarationMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceArithmeticOperatorsAssignmentMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceArithmeticOperatorsForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceArithmeticOperatorsVariableDecMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceLogicalOperatorsForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceLogicalOperatorsIfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceLogicalOperatorsWhileLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceNumToStrVariableDecMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplacePlusMinusOperatorForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceRelationalOperatorsForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceRelationalOperatorsIfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceRelationalOperatorsWhileLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceReturnStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.SwapFuncParamsMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.*;
import jp.mzw.ajaxmutator.util.Util;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import java.io.*;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * High level test that checks if generated mutants are all syntactically valid.
 *
 * @author Kazuki Nishiura
 */
public class MutationValidityTest {
	UnifiedDiffGeneratorForTest diffGenerator = new UnifiedDiffGeneratorForTest(
			this.getClass().getResourceAsStream("/quizzy.js"));
	List<String> contentOfOriginalFile = readResourceFile("/quizzy.js");

	@BeforeClass
	public static void normalizeLineBreak() {
		Util.normalizeLineBreak(new File(MutationValidityTest.class
				.getResource("/quizzy.js").getFile()));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGeneratedMutantIsSyntacticallyValid() {
		MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
		builder.setRequestDetectors(ImmutableSet
				.of(new JQueryRequestDetector()));
		builder.setEventAttacherDetectors(ImmutableSet
				.<EventAttacherDetector> of(new JQueryEventAttachmentDetector()));
		MutateVisitor visitor = builder.build();

		ParserWithBrowser parser = ParserWithBrowser.getParser();
		AstRoot root = parser.parse(readResource("/quizzy.js"),
				"http://fake.dummy", 1);
		root.visit(visitor);

		Set<Mutator> mutators = ImmutableSet.<Mutator> of(
				new EventTargetRAMutator(visitor.getEventAttachments()),
				new EventTypeRAMutator(visitor.getEventAttachments()),
				new EventCallbackRAMutator(visitor.getEventAttachments()),
				new TimerEventDurationRAMutator(visitor
						.getTimerEventAttachmentExpressions()),
				new TimerEventCallbackRAMutator(visitor
						.getTimerEventAttachmentExpressions()),
				new AppendedDOMRAMutator(visitor.getDomAppendings()),
				new AttributeModificationTargetRAMutator(visitor
						.getAttributeModifications()),
				new AttributeModificationValueRAMutator(visitor
						.getAttributeModifications()),
				new DOMSelectionSelectNearbyMutator(),
				new RequestOnSuccessHandlerRAMutator(visitor.getRequests()),
				new RequestMethodRAMutator(visitor.getRequests()),
				new RequestUrlRAMutator(visitor.getRequests()),
				new ChangeConditionSwitchStatementMutator(),
				new ChangeValueVariableDecMutator(),
				new IfStatementMutator(),
				new RemoveBreakContinueForLoopMutator(),
				new RemoveBreakContinueWhileLoopMutator(),
				new RemoveBreakFromSwitchStatementMutator(),
				new RemoveElseIfStatementMutator(),
				new RemoveParamsFromFuncNodeMutator(),
				new RemoveReturnStatementMutator(),
				new RemoveVariableDeclarationMutator(),
				new ReplaceArithmeticOperatorsAssignmentMutator(),
				new ReplaceArithmeticOperatorsForLoopMutator(),
				new ReplaceArithmeticOperatorsVariableDecMutator(),
				new ReplaceLogicalOperatorsForLoopMutator(),
				new ReplaceLogicalOperatorsIfStatementMutator(),
				new ReplaceLogicalOperatorsWhileLoopMutator(),
				new ReplaceNumToStrVariableDecMutator(),
				new ReplacePlusMinusOperatorForLoopMutator(),
				new ReplaceRelationalOperatorsForLoopMutator(),
				new ReplaceRelationalOperatorsIfStatementMutator(),
				new ReplaceRelationalOperatorsWhileLoopMutator(),
				new ReplaceReturnStatementMutator(),
				new SwapFuncParamsMutator()
				);
				

		// Events
		generateMutationAndParse(visitor.getEventAttachments(), mutators);
		generateMutationAndParse(visitor.getTimerEventAttachmentExpressions(),
				mutators);
		// Asynchronous communications
		generateMutationAndParse(visitor.getRequests(), mutators);
		// DOM manipulations
		generateMutationAndParse(visitor.getDomCreations(), mutators);
		generateMutationAndParse(visitor.getDomAppendings(), mutators);
		generateMutationAndParse(visitor.getDomSelections(), mutators);
		generateMutationAndParse(visitor.getDomRemovals(), mutators);
		// Generic
		generateMutationAndParse(visitor.getAssignmentExpressions(), mutators);
		generateMutationAndParse(visitor.getBreaks(), mutators);
		generateMutationAndParse(visitor.getContinues(), mutators);
		generateMutationAndParse(visitor.getFors(), mutators);
		generateMutationAndParse(visitor.getFuncnodes(), mutators);
		generateMutationAndParse(visitor.getIfs(), mutators);
		generateMutationAndParse(visitor.getReturns(), mutators);
		generateMutationAndParse(visitor.getSwitches(), mutators);
		generateMutationAndParse(visitor.getVariableDecss(), mutators);
		generateMutationAndParse(visitor.getWhiles(), mutators);
	}

	private String readResource(String resource) {
		InputStream inputStream = this.getClass().getResourceAsStream(resource);
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer);
		} catch (IOException e) {
			System.err.println(e);
		}
		return writer.toString();
	}

	@SuppressWarnings("resource")
	private List<String> readResourceFile(String pathToResource) {
		InputStream stream = this.getClass()
				.getResourceAsStream(pathToResource);
		List<String> contents = new ArrayList<String>();
		Scanner scanner = new Scanner(stream);
		while (scanner.hasNext()) {
			contents.add(scanner.nextLine());
		}
		return contents;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void generateMutationAndParse(Set<? extends Mutatable> mutatables,
			Set<Mutator> mutators) {
		if (mutatables.size() == 0) {
			return;
		}

		Set<Mutator> applicableMutator = new HashSet<Mutator>();
		Mutatable aMutatable = Iterables.get(mutatables, 0);
		for (Mutator mutator : mutators) {
			if (mutator.isApplicable(aMutatable.getClass())) {
				applicableMutator.add(mutator);
			}
		}

		for (Mutator mutator : applicableMutator) {
			for (Mutatable mutatable : mutatables) {
				List<Mutation> mutationList = mutator
						.generateMutationList(mutatable);
				if (mutationList == null) {
					continue;
				}
				for (Mutation mutation : mutationList) {
					if (mutation == null) {
						continue;
					}

					String unifiedDiff = diffGenerator.generateUnifiedDiff(
							mutation.getOriginalNode(),
							Arrays.asList(mutation.getMutatingContent().split(
									System.lineSeparator())));

					Patch patch = DiffUtils
							.parseUnifiedDiff(splitByNewline(unifiedDiff));
					try {
						String mutant = Joiner.on("").join(
								patch.applyTo(contentOfOriginalFile));
						ParserWithBrowser parser = ParserWithBrowser
								.getParser();
						parser.parse(mutant, "http://mutant.dummy", 1);

					} catch (PatchFailedException e) {
						fail("Mutant should be applicable");
					}
				}
			}
		}
	}

	private List<String> splitByNewline(String str) {
		return Arrays.asList(str.split(System.lineSeparator()));
	}
}
