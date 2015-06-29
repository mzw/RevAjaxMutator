package jp.mzw.revajaxmutator.config;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.mzw.revajaxmutator.MutateConfigurationBase;
import jp.mzw.revajaxmutator.detector.mootools.MootoolsRequestDetector;
import jp.mzw.revajaxmutator.mutator.MootoolsRequestMethodMutator;

import com.google.common.collect.ImmutableSet;

public class WPBS1 {
    public static final String PATH_TO_JS_FILE =
            "sample/blip.js"; // set to the proxy path

    public static class FixConfiguration extends MutateConfigurationBase {
    	/*
    	Test suite: jp.mzw.revajaxmutator.test.wp.BlipSlideshowTestForBeforeRegression1
		Test class: jp.mzw.revajaxmutator.test.wp.BlipSlideshowTestForBeforeRegression1
		Test case: test
		Error detected line: 41
		Test statement: 		waitUntilImageLoaded();
		==Found:
		Mutation opeartor id: ACM
		==========
    	 */
        @SuppressWarnings("rawtypes")
        public FixConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
            builder.setRequestDetectors(ImmutableSet.<AbstractDetector<Request>>of(
                    new MootoolsRequestDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator> of(
                    new MootoolsRequestMethodMutator());
        }
    }

    public static class MutateConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public MutateConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
            builder.setRequestDetectors(ImmutableSet.<AbstractDetector<Request>>of(
                    new MootoolsRequestDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator> of(
                    new MootoolsRequestMethodMutator());
        }
    }
}
