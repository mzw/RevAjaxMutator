package jp.mzw.revajaxmutator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import jp.mzw.ajaxmutator.JUnitExecutor;
import jp.mzw.ajaxmutator.JUnitTestRunner;
import jp.mzw.ajaxmutator.JUnitTheoryRunner;
import jp.mzw.ajaxmutator.MutationTestConductor;
import jp.mzw.revajaxmutator.genprog.GenProgConductor;
import jp.mzw.revajaxmutator.search.Searcher;

import org.json.JSONException;
import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }
        String cmd = args[0];
        String[] rargs = Arrays.copyOfRange(args, 1, args.length);
        try {
            if ("test".equals(cmd)) {
                test(rargs);
                System.exit(0);
            }
            if ("mutate".equals(cmd)) {
                mutate(rargs);
                System.exit(0);
            }
            if ("analysis".equals(cmd)) {
                analysis(rargs);
                System.exit(0);
            }
            if ("proxy".equals(cmd)) {
                proxy(rargs);
                System.exit(0);
            }
            if ("search".equals(cmd)) {
                search(rargs);
                System.exit(0);
            }
            if ("genprog".equals(cmd)) {
                genprog(rargs);
                System.exit(0);
            }
            usage();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test(String[] args) throws ClassNotFoundException, InitializationError {
        String className = args[0];
        Class<?> testClass = Class.forName(className);

        Runner runner = null;
    	RunWith runWith = testClass.getAnnotation(RunWith.class);
    	if(runWith == null) {
            runner = new JUnitTestRunner(testClass, true);
    	} else if(Theories.class.equals(runWith.value())) {
    		runner = new JUnitTheoryRunner(testClass, true);
    	} else {
            runner = new BlockJUnit4ClassRunner(Class.forName(className));
    	}
        
        Result result = (new JUnitCore()).run(runner);
        System.out.println(String.format("%d tests,  %d fail",
                result.getRunCount(), result.getFailureCount()));
        for (Failure f : result.getFailures()) {
            System.out.println(f.getDescription());
            System.out.println("  exception: " + f.getException());
        }
    }

    public static void mutate(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (args.length == 0) {
            System.err.println("please specify configuration classname");
            System.exit(1);
        }
        String className = args[0];
        Class<?> clazz = getClass(className);

        MutateConfiguration config = (MutateConfiguration) clazz.newInstance();
        MutationTestConductor conductor = config.mutationTestConductor();
        conductor.generateMutations(config.mutators());
    }

    public static void analysis(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String className = args[0];
        String testClassName = args[1];

        MutateConfiguration config = (MutateConfiguration) Class.forName(className).newInstance();
        MutationTestConductor conductor = config.mutationTestConductor();
        conductor.mutationAnalysisUsingExistingMutations(
                new JUnitExecutor(false, Class.forName(testClassName)));
    }

    public static void proxy(String[] args) throws StoreException {
        Framework framework = new Framework();
        Preferences.setPreference("Proxy.listeners", "127.0.0.1:8080");
        framework.setSession("FileSystem", new File(".conversation"), "");
        Proxy proxy = new Proxy(framework);
        for (int i = 0; i < args.length; i++) {
            if ("-record".equals(args[i])) {
                System.err.println("adding RecorderPlugin: path = " + args[i+1]);
                proxy.addPlugin(new RecorderPlugin(args[i+1]));
            }
            if ("-rewrite".equals(args[i])) {
                System.err.println("adding RewriterPlugin: path = " + args[i+1]);
                proxy.addPlugin(new RewriterPlugin(args[i+1]));
            }
            if ("-filter".equals(args[i])) {
                System.err.println("adding FilterPlugin: url = " + args[i+1] + ", method = " + args[i+2]);
                proxy.addPlugin(new FilterPlugin(args[i+1], args[i+2]));
            }
        }
        proxy.run();
        while (proxy.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }
    
    public static void search(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JSONException, IOException {
        if (args.length == 0) {
            System.err.println("please specify configuration filename");
            System.exit(1);
        }
        Class<?> clazz = Class.forName(args[0]);
        Searcher searcher = new Searcher(clazz);
        searcher.search();
    }
    
    public static void genprog(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, JSONException, InterruptedException {
        String className = args[0];
        String testClassName = args[1];

		GenProgConductor conductor = new GenProgConductor(Class.forName(className));
		conductor.search(Class.forName(testClassName));
    }
    
    public static void usage() {
        System.err.println("please specify command (test, mutate, analysis, proxy, search, genprog)");
    }

    public static Class<?> getClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            if (!className.contains(".")) throw e;
        }
        int i = className.lastIndexOf('.');
        return Class.forName(className.substring(0, i) + '$' + className.substring(i+1));
    }
}
