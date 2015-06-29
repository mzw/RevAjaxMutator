package jp.mzw.revajaxmutator.config;

import com.google.common.collect.ImmutableSet;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryRequestDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.DOMSelection;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AppendedDOMRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AttributeModificationTargetRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AttributeModificationValueRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventCallbackRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTargetRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTypeRAMutator;
import jp.mzw.revajaxmutator.MutateConfigurationBase;
import jp.mzw.revajaxmutator.detector.DOMIdTagSelectionDetector;
import jp.mzw.revajaxmutator.detector.yui.YUIEventAttachementDetector;
import jp.mzw.revajaxmutator.fixer.DOMSelectionSelectGivenFixer;
import jp.mzw.revajaxmutator.mutator.DOMIdTagValueMutator;

public class PwdUnmask {
	public static final String PATH_TO_JS_FILE = "record/pwdunmask/http%3A%2F%2Flocalhost%3A80%2F%7Eyuta%2Fpwdunmask%2Fjs%2Fmoodle-passwordunmask.js";

    public static class FixConfiguration extends MutateConfigurationBase {
    	
    	/*
		Test suite: jp.mzw.revajaxmutator.test.example.PwdUnmaskTest
		Test class: jp.mzw.revajaxmutator.test.example.PwdUnmaskTest
		Test case: testCheckbox
		Error detected line: 31
		Test statement: 		 assertEquals("text",
		==Found:
		Mutation opeartor id: DMV
		==========
    	 */

        @SuppressWarnings("rawtypes")
        public FixConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(
                    new YUIEventAttachementDetector()));
            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
                    new DOMIdTagSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            /// rDMV debugging operator with value from given test case
            mutators = ImmutableSet.<Mutator> of(
                    new DOMSelectionSelectGivenFixer("'update_pwd'"));
        }
    }
	
    public static class MutateConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
		public MutateConfiguration() {
//            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
//            builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
//            builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(
//                    new YUIEventAttachementDetector()));
//            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
//                    new DOMIdTagSelectionDetector()));
//            visitor = builder.build();
            

            MutateVisitorBuilder builder = MutateVisitor.defaultBuilder();
            builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(
            		new YUIEventAttachementDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator>of(
                    new EventTargetRAMutator(visitor.getEventAttachments()),
                    new EventTypeRAMutator(visitor.getEventAttachments()),
                    new EventCallbackRAMutator(visitor.getEventAttachments()),
                    new AppendedDOMRAMutator(visitor.getDomAppendings()),
                    new AttributeModificationTargetRAMutator(visitor.getAttributeModifications()),
                    new AttributeModificationValueRAMutator(visitor.getAttributeModifications()),
                    new DOMSelectionSelectNearbyMutator(),
                    new DOMIdTagValueMutator(visitor.getDomSelections())
                    );
        }
    	
    }

}
