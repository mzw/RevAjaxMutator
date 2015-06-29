package jp.mzw.revajaxmutator.config;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.dom_manipulation_detector.DOMSelectionDetector;
import jp.gr.java_conf.daisy.ajax_mutator.detector.jquery.JQueryEventAttachmentDetector;
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
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.RequestMethodRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.RequestOnSuccessHandlerRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.RequestUrlRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.TimerEventCallbackRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.TimerEventDurationRAMutator;
import jp.mzw.revajaxmutator.MutateConfigurationBase;
import jp.mzw.revajaxmutator.fixer.DOMSelectionSelectGivenFixer;

import com.google.common.collect.ImmutableSet;

public class Example {
    public static final String PATH_TO_JS_FILE = 
            "rewrite/http%3A%2F%2Fmaezawa.honiden.nii.ac.jp%3A80%2Fyuta%2Fresearch%2Ftest%2Fpwdunmask%2F1.withdraw%2Fjs%2Fmoodle-passwordunmask.js";

    public static class FixConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public FixConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setDomSelectionDetectors(ImmutableSet
                    .<AbstractDetector<DOMSelection>> of(new DOMSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator> of(new DOMSelectionSelectGivenFixer("'pwd'"));
        }
    }

    public static class MutateConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public MutateConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setRequestDetectors(ImmutableSet.of(new JQueryRequestDetector()));
            builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(
                    new JQueryEventAttachmentDetector()));
            builder.setDomSelectionDetectors(ImmutableSet.<AbstractDetector<DOMSelection>>of(
                    new DOMSelectionDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator>of(
                    new EventTargetRAMutator(visitor.getEventAttachments()),
                    new EventTypeRAMutator(visitor.getEventAttachments()),
                    new EventCallbackRAMutator(visitor.getEventAttachments()),
                    new TimerEventDurationRAMutator(visitor.getTimerEventAttachmentExpressions()),
                    new TimerEventCallbackRAMutator(visitor.getTimerEventAttachmentExpressions()),
                    new AppendedDOMRAMutator(visitor.getDomAppendings()),
                    new AttributeModificationTargetRAMutator(visitor.getAttributeModifications()),
                    new AttributeModificationValueRAMutator(visitor.getAttributeModifications()),
                    new DOMSelectionSelectNearbyMutator(),
                    new RequestOnSuccessHandlerRAMutator(visitor.getRequests()),
                    new RequestMethodRAMutator(visitor.getRequests()),
                    new RequestUrlRAMutator(visitor.getRequests()));
        }
    }

}
