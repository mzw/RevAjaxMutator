package jp.mzw.revajaxmutator.config;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.mzw.revajaxmutator.MutateConfigurationBase;
import jp.mzw.revajaxmutator.fixer.DOMSelectionSelectGivenFixer;

import com.google.common.collect.ImmutableSet;

public class WPTG1 {
    public static final String PATH_TO_JS_FILE =
            "sample/tg.js"; // set to the proxy path

    public static class FixConfiguration extends MutateConfigurationBase {
    	/*
    	Test suite: jp.mzw.revajaxmutator.test.wp.TaggedGalleryTestForBeforeRegression1
		Test class: jp.mzw.revajaxmutator.test.wp.TaggedGalleryTestForBeforeRegression1
		Test case: test2
		Error detected line: 50
		Test statement: 			assertTrue(false);
		==Found:
		Mutation opeartor id: DMV
		==========
    	 */
        @SuppressWarnings("rawtypes")
        public FixConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
                    new DOMSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator> of(
                    new DOMSelectionSelectGivenFixer("\"tg-resizecrop\""));
        }
    }

    public static class MutateConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public MutateConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
                    new DOMSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator>of(
                    new DOMSelectionSelectNearbyMutator());
        }
    }
}
