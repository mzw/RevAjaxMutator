package jp.mzw.revajaxmutator.genprog;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationFileWriter;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.ajaxmutator.detector.genprog.StatementDetector;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;
import jp.mzw.ajaxmutator.mutator.genprog.StatementDeleteMutator;
import jp.mzw.ajaxmutator.mutator.genprog.StatementInsertMutator;
import jp.mzw.ajaxmutator.mutator.genprog.StatementSwapMutator;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.ajaxmutator.test.executor.JUnitExecutor;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.test.result.Coverage;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class GenProgConductor {
	protected static Logger LOGGER = LoggerFactory.getLogger(GenProgConductor.class);

	static String COMBINEDIFF = "/usr/local/bin/combinediff";

	double w_path = 0.01;
	double w_mut = 0.06;
	int sample_size = 80;
	int num_gen = 10;
	
	AppConfig config;
	MutationFileWriter mutationFileWriter;
	MutationListManager mutationListManager;
	MutationTestConductor conductor;
	boolean skipTest;
	
	public GenProgConductor(Class<?> config) throws InstantiationException, IllegalAccessException, IOException {
		this.config = (AppConfig) config.newInstance();
		
		this.mutationFileWriter = new MutationFileWriter(this.config.getRecordedJsFile());
		this.mutationListManager = new MutationListManager(mutationFileWriter.getDestinationDirectory());
		
		setup();
	}
	
	private void setup() throws IOException {
		Properties localenv = new Properties();
		localenv.load(GenProgConductor.class.getClassLoader().getResourceAsStream("localenv.properties"));
		
		COMBINEDIFF = localenv.getProperty("combinediff") != null ? localenv.getProperty("combinediff") : "/usr/local/bin/combinediff";
		w_path = localenv.getProperty("w_path") != null ? Double.parseDouble(localenv.getProperty("w_path")) : 0.01;
		w_mut = localenv.getProperty("w_mut") != null ? Double.parseDouble(localenv.getProperty("w_mut")) : 0.06;
		sample_size = localenv.getProperty("sample_size") != null ? Integer.parseInt(localenv.getProperty("sample_size")) : 80;
		num_gen = localenv.getProperty("num_gen") != null ? Integer.parseInt(localenv.getProperty("num_gen")) : 10;
		
		skipTest = localenv.getProperty("skip_test") != null ? Boolean.parseBoolean(localenv.getProperty("skip_test")) : false;
	}
	
	public void search(Class<?> testClass) throws IOException, InterruptedException, JSONException {
		search(num_gen, testClass);
	}
	
	public void search(int generations, Class<?> testClass) throws IOException, InterruptedException, JSONException {
		LOGGER.info("Start GenProg");
		
		ArrayList<Mutation> cur_mutations = new ArrayList<Mutation>();
		ArrayList<Mutation> nxt_mutations = new ArrayList<Mutation>();
		
		cur_mutations.addAll(getInitMutations());
		
		for(int g = 1; g < generations; g++) {
			LOGGER.info("The " + g + " th(st/nd) generation");
			
			ArrayList<Mutation> samples = sample(cur_mutations, sample_size);
			// Fitness
			for(Mutation mutation : samples) {
				measureFitness(mutation, testClass, g);
			}
			Collections.sort(samples, new MutationComparator());

			// Crossover
			LOGGER.info("Crossover: " + (samples.size()/2));
			nxt_mutations.addAll(crossover(samples));
			
			// Mutation
			LOGGER.info("Mutation: " + (samples.size()/2));
			nxt_mutations.addAll(mutation(samples));
			
			// For next generation
			cur_mutations = nxt_mutations;
			nxt_mutations = new ArrayList<Mutation>();
		}
		// Examine whether mutation succeed to repair program at final generation
		for(Mutation mutation : cur_mutations) {
			measureFitness(mutation, testClass, generations);
		}
		mutationListManager.generateMutationListFile();
	}

	//----------------------------------------------------------------------------------------------------
	private ArrayList<Mutation> mutation(ArrayList<Mutation> mutations) throws IOException, InterruptedException, JSONException {
		ArrayList<Mutation> ret = new ArrayList<Mutation>();
		
		ArrayList<Statement> statements = new ArrayList<Statement>(setWeight(getStatements()));
		// Create the half number of mutants
		for(int i = 0; i < (mutations.size()/2); i++) {
			Mutation mutation = generateMutation(statements);
			ret.add(mutation);
		}
		
		return ret;
	}
	
	private Mutation generateMutation(ArrayList<Statement> statements) throws IOException, InterruptedException {
		
		ArrayList<MutationFileInformation> originList = new ArrayList<MutationFileInformation>();
		
		// 1: for-all(stmt_i, prob_i) in Path P do
		//how can you specify PathP??
		for(int i = 0; i < statements.size(); i++) {
			Statement stmti = statements.get(i);
			
			// 2. if rand(0, 1) <= prob_i && rand(0, 1) <= W_mut then
			double rand1 = Math.random();
			double probi = stmti.getWeight();
			double rand2 = Math.random();
			
			if(rand1 <= probi && rand2 <= w_mut) {

				// 3. let op = choose({insert, swap, delete})
				double rand3 = Math.random();
				String op;
				if(rand3 < (1.0/3)) {
					op = "insert";
				} else if(rand3 < (2.0/3)) {
					op = "swap";
				} else {
					op = "delete";
				}
				
				// 4. if op = swap then
				if("swap".equals(op)) {
					// 5. let stmt_j = choose(P)
					int j = (int) (statements.size() * Math.random());
					Statement stmtj = statements.get(j);
					// 6. Path P [i] <- <stmt_j, prob_i>
					List<jp.mzw.ajaxmutator.generator.Mutation> mutations = (new StatementSwapMutator(stmti, stmtj)).generateMutationList(stmti);
					if(mutations != null && mutations.size() == 1) {
						jp.mzw.ajaxmutator.generator.Mutation mutation = mutations.get(0);
						File file = mutationFileWriter.writeToFile(mutation);
						if(file != null) {
							MutationFileInformation origin = new MutationFileInformation(file.getName(), file.getAbsolutePath());
							originList.add(origin);
						}
					}
				}
				
				// 7.else if op = insert then
				else if("insert".equals(op)) {
					// 8. let stmt_j = choose(P)
					int j = (int) (statements.size() * Math.random());
					Statement stmtj = statements.get(j);
					// 9. Path P [i] <- <{stmt_i ; stmt_j }, prob_i> else if op = delete then
					List<jp.mzw.ajaxmutator.generator.Mutation> mutations = (new StatementInsertMutator(stmti, stmtj)).generateMutationList(stmti);
					if(mutations != null && mutations.size() == 1) {
						jp.mzw.ajaxmutator.generator.Mutation mutation = mutations.get(0);
						File file = mutationFileWriter.writeToFile(mutation);
						if(file != null) {
							MutationFileInformation origin = new MutationFileInformation(file.getName(), file.getAbsolutePath());
							originList.add(origin);
						}
					}
				}
				
				// 10. else if op = delete then
				else if("delete".equals(op)) {
					// 11. PathP [i] <- <{}, prob_i>
					List<jp.mzw.ajaxmutator.generator.Mutation> mutations = (new StatementDeleteMutator(stmti)).generateMutationList(stmti);
					if(mutations != null && mutations.size() == 1) {
						jp.mzw.ajaxmutator.generator.Mutation mutation = mutations.get(0);
						File file = mutationFileWriter.writeToFile(mutation);
						if(file != null) {
							MutationFileInformation origin = new MutationFileInformation(file.getName(), file.getAbsolutePath());
							originList.add(origin);
						}
					}
					
				}
			}	
		}
		
		MutationFileInformation diff = combinediff(mutationFileWriter, originList);
		if(diff == null) {
			return generateMutation(statements);
		}
		
		return new Mutation(diff, originList);
	}
	
	private ArrayList<Mutation> crossover(ArrayList<Mutation> mutations) throws IOException, InterruptedException {
		ArrayList<Mutation> ret = new ArrayList<Mutation>();
		
		int N = mutations.size() / 2;

		for(int p = 0; p < N-1; p++) {
			
			Mutation P = mutations.get(p);
			ArrayList<MutationFileInformation> pOriginList = P.getOriginList();
			
			for(int q = p+1; q < N; q++) {
				
				Mutation Q = mutations.get(q);
				ArrayList<MutationFileInformation> qOriginList = Q.getOriginList();

				int cutoff = (int) Math.round(pOriginList.size() * Math.random());
				ArrayList<MutationFileInformation> Cpre = new ArrayList<MutationFileInformation>();
				for(int c = 0; c < cutoff; c++) {
					Cpre.add(pOriginList.get(c));
				}
				ArrayList<MutationFileInformation> Dpost = new ArrayList<MutationFileInformation>();
				for(int c = cutoff; c < pOriginList.size(); c++) {
					Dpost.add(pOriginList.get(c));
				}
				int _cutoff = cutoff < qOriginList.size() ? cutoff : qOriginList.size();
				ArrayList<MutationFileInformation> Dpre = new ArrayList<MutationFileInformation>();
				for(int c = 0; c < _cutoff; c++) {
					Dpre.add(qOriginList.get(c));
				}
				ArrayList<MutationFileInformation> Cpost = new ArrayList<MutationFileInformation>();
				for(int c = _cutoff; c < qOriginList.size(); c++) {
					Cpost.add(qOriginList.get(c));
				}
				
				ArrayList<MutationFileInformation> cOriginList = new ArrayList<MutationFileInformation>();
				cOriginList.addAll(Cpre);
				cOriginList.addAll(Cpost);
				ArrayList<MutationFileInformation> dOriginList = new ArrayList<MutationFileInformation>();
				dOriginList.addAll(Dpre);
				dOriginList.addAll(Dpost);
				
				MutationFileInformation c = combinediff(mutationFileWriter, cOriginList);
				if(c != null) {
					Mutation C = new Mutation(c, cOriginList);
					ret.add(C);
				} else {
					LOGGER.info("Fail to combine: " + P.getMutationFileInformation().getAbsolutePath());
					ret.add(P);
				}
				
				MutationFileInformation d = combinediff(mutationFileWriter, dOriginList);
				if(d != null) {
					Mutation D = new Mutation(d, dOriginList);
					ret.add(D);
				} else {
					LOGGER.info("Fail to combine: " + Q.getMutationFileInformation().getAbsolutePath());
					ret.add(Q);
				}

				
			}
		}
		
		return ret;
	}

	public static MutationFileInformation combinediff(MutationFileWriter mutationFileWriter, List<MutationFileInformation> mutations) throws IOException, InterruptedException {
		
		if(mutations.size() == 1) {
			return mutations.get(0);
		} else if(1 < mutations.size()) {
			MutationFileInformation mut_base = mutations.get(0);
			ArrayList<MutationFileInformation> originList = new ArrayList<MutationFileInformation>();
			originList.add(mut_base);
			List<String> mut_base_content = FileUtils.readLines(new File(mut_base.getAbsolutePath()));
			for(int i = 1; i < mutations.size(); i++) {
				MutationFileInformation mut_combine = mutations.get(i);
				
				List<String> mut_combine_content = FileUtils.readLines(new File(mut_combine.getAbsolutePath()));
				
				List<String> mut_base_locations = new ArrayList<>();
				for(String line : mut_base_content) {
					if(line.startsWith("@@")) {
						mut_base_locations.add(line);
					}
				}
				List<String> mut_combine_locations = new ArrayList<>();
				for(String line : mut_combine_content) {
					if(line.startsWith("@@")) {
						mut_combine_locations.add(line);
					}
				}
				boolean same = false;
				for(String line : mut_base_locations) {
					boolean _same = false;
					for(String _line : mut_combine_locations) {
						System.out.println(line + ", " + _line);
						if(line.equals(_line)) {
							_same = true;
							break;
						}
					}
					if(_same) {
						same = true;
						break;
					}
				}
				if(same) {
					continue;
				}

				String content = CombineDiff.combinediff(COMBINEDIFF, ".", mut_base.getAbsolutePath(), mut_combine.getAbsolutePath());
				
				if(content == null) {
					LOGGER.warn("Fail to combine: " + mut_base.getFileName() + " + " + mut_combine.getFileName());
					continue;
				}
				
				File file = mutationFileWriter.writeToFile(content);
				if(file == null) {
					continue;
				}
				MutationFileInformation combined = new MutationFileInformation(file.getName(), file.getAbsolutePath());
				originList.add(mut_combine);
				
				LOGGER.info("Combined: " + combined.getFileName() + ", " + mut_base.getFileName() + " + " + mut_combine.getFileName());
				
				mut_base = combined;
			}
			return mut_base;
		}
		
		return null;
	}
	
	private boolean measureFitness(Mutation mutation, Class<?> testClass, int generation) {
		int fitness = 0;
		int failure = 0;
		
		if(this.skipTest) {
			this.mutationListManager.addMutationFileInformation(generation+"_generation", mutation.getMutationFileInformation());
			return false;
		}

		List<Result> results = conductor.testSpecificMutation(mutation.getMutationFileInformation(), new JUnitExecutor(true, testClass));
		if(results == null) { // fail to mutation
			LOGGER.warn("Fail to mutation: " + mutation.getMutationFileInformation().getAbsolutePath());
			mutation.setFitness(-1);
			return false;
		}
		for(Result result : results) {
			fitness += result.getRunCount() - result.getFailureCount();
			failure += result.getFailureCount();
		}
		mutation.setFitness(fitness);

		if(failure == 0) {
			LOGGER.info("Success to repair: " + mutation.getMutationFileInformation());
			return true;
		}

		LOGGER.info("Measure fitness: " + fitness + ", " + mutation.getMutationFileInformation());
		return false;
	}
	
	private static class MutationComparator implements Comparator<Mutation> {
		@Override
		public int compare(Mutation m1, Mutation m2) {
			if(m1.getFitness() < m2.getFitness()) {
				return 1;
			} else if(m1.getFitness() == m2.getFitness()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	
	private ArrayList<Mutation> sample(ArrayList<Mutation> mutations, int sample_size) {
		ArrayList<Mutation> ret = new ArrayList<Mutation>();
		
		if(mutations.size() < sample_size) {
			return mutations;
		}
		
		Collections.shuffle(mutations);
		for(int i = 0; i < sample_size; i++) {
			ret.add(mutations.get(i));
		}

		return ret;
	}

	//----------------------------------------------------------------------------------------------------
	private ArrayList<Mutation> getInitMutations() throws IOException, JSONException, InterruptedException {
		Set<Statement> statements = getStatements();
		Set<Statement> weighted_path = setWeight(statements);
		ArrayList<Mutation> mutations = generateMutations(weighted_path);
		return mutations;
	}
	
	private Set<Statement> getStatements() throws MalformedURLException, UnsupportedEncodingException {
		String path_to_js_file = config.getRecordedJsFile().toString();
		
		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
        builder.setStatementDetectors(ImmutableSet.of(
        		new StatementDetector()
        		));
        MutateVisitor visitor = builder.build();
        conductor = new MutationTestConductor();
        conductor.setup(path_to_js_file, "", visitor);
        return visitor.getStatements();
	}
	
	private Set<Statement> setWeight(Set<Statement> statements) throws JSONException {
		try {
			List<File> files = Coverage.getFailureCoverageResults(config.getJscoverReportDir());
			for (File file : files) {
				JSONObject failure_coverage_json = Coverage.parse(file);
				String encoded_url = config.getRecordedJsFile().getName();
				String decoded_url = URLDecoder.decode(encoded_url, "utf-8");
				URL url = new URL(decoded_url);
				String url_path_to_js_file = URLDecoder.decode(url.getPath(), "utf-8");
				JSONArray failure = Coverage.getCoverageResults(failure_coverage_json, url_path_to_js_file);
				
				List<String> jsfile = FileUtils.readLines(config.getRecordedJsFile());

		        int line_num = failure.length();
		        double[] weighted_path = new double[jsfile.size()+1];
		        for(int i = 1; i < line_num; i++) {
		        	Object failure_line = failure.get(i);
		        	int failure_cover_freq = Coverage.getCoverFreq(failure_line);
		        	if(0 < failure_cover_freq) {
		            	weighted_path[i] = w_path;
		        	} else {
		            	weighted_path[i] = 0;
		        	}
		        }
				for(Statement statement : statements) {
		        	int lineno = statement.getAstNode().getLineno();
		        	statement.setWeight(weighted_path[lineno]);
		        }
			}
		} catch (IOException e) {
			for(Statement statement : statements) {
	        	statement.setWeight(1.0);
	        }
			return statements;
		}
		return statements;
	}
	
	private ArrayList<Mutation> generateMutations(Set<Statement> wighted_path) throws IOException, InterruptedException {
		ArrayList<Mutation> ret = new ArrayList<Mutation>();
		for(int i = 0; i < sample_size; i++){
			ret.add(generateMutation(new ArrayList<Statement>(wighted_path)));
		}
		return ret;
	}
	
}
