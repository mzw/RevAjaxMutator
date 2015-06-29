package jp.mzw.revajaxmutator.config;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitorBuilder;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.detector.EventAttacherDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTargetRAMutator;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.EventTypeRAMutator;
import jp.mzw.revajaxmutator.MutateConfigurationBase;
import jp.mzw.revajaxmutator.detector.yui.YUIDelegateDetector;
import jp.mzw.revajaxmutator.fixer.EventTargetGivenFixer;

import com.google.common.collect.ImmutableSet;

public class MDL41513 {
    public static final String PATH_TO_JS_FILE =
            "rewrite/http%3A%2F%2Fmaezawa.honiden.nii.ac.jp%3A80%2Fmoodle26%2Flib%2Fjavascript.php%2F-1%2Fmod%2Fquiz%2Fmodule.js";

    public static class FixConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public FixConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(
                    new YUIDelegateDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator>of(
                    new EventTargetGivenFixer("'body'"));
        }
    }

    public static class MutateConfiguration extends MutateConfigurationBase {
        @SuppressWarnings("rawtypes")
        public MutateConfiguration() {
            MutateVisitorBuilder builder = MutateVisitor.defaultJqueryBuilder();
            builder.setEventAttacherDetectors(ImmutableSet.<EventAttacherDetector>of(
                    new YUIDelegateDetector()));
            visitor = builder.build();

            conductor = new MutationTestConductor();
            conductor.setup(PATH_TO_JS_FILE, "", visitor);

            mutators = ImmutableSet.<Mutator>of(
                    new EventTargetRAMutator(visitor.getEventAttachments()),
                    new EventTypeRAMutator(visitor.getEventAttachments()));
        }
    }
}
