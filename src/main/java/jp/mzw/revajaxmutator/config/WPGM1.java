package jp.mzw.revajaxmutator.config;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.mzw.revajaxmutator.MutateConfigurationBase;
import jp.mzw.revajaxmutator.fixer.DOMSelectionSelectGivenFixer;

import com.google.common.collect.ImmutableSet;

public class WPGM1 {
    public static final String PATH_TO_JS_FILE =
            "sample/meta-box.js"; // set to the proxy path

    public static class FixConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public FixConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
                    new JQueryDOMSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator> of(
                    new DOMSelectionSelectGivenFixer("document"));
        }
    }

    public static class MutateConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public MutateConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
                    new JQueryDOMSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator>of(
                    new DOMSelectionSelectNearbyMutator());
        }
    }
}
