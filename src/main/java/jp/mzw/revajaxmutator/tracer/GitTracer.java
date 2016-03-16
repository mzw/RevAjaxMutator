package jp.mzw.revajaxmutator.tracer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitTracer {
	
	private static final String GIT_DIR = ".git/";
	protected Repository repo;
	public GitTracer(String dir) throws IOException {
		File git_dir = new File(dir, GIT_DIR);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repo = builder.setGitDir(git_dir).readEnvironment().findGitDir().build();
	}
	
	/**
	 * Specifies a mutation operator that helped improve a test case
	 * @param commit contains the test case improvement
	 * @return specified mutation operator
	 */
	public String specifyMutationOpeator(RevCommit commit) {
		String mut_op_id = null;
//		String mutation = null;
//		String operator = null;
		Scanner scanner = new Scanner(commit.getFullMessage());
		while(scanner.hasNext()) {
//			String s = scanner.next();
//			
//			mut_op_id = mutation;
//			mutation = operator;
//			operator = s;
//			
//			if(isMutOpId(mut_op_id) &&
//					"mutation".equalsIgnoreCase(mutation) &&
//					"operator".equalsIgnoreCase(operator)) {
			mut_op_id = scanner.next();
			if(isMutOpId(mut_op_id)) {
				scanner.close();
				return mut_op_id;
			}
		}
		scanner.close();
		return null;
	}
	
	private boolean isMutOpId(String mut_op_id) {
		if(mut_op_id == null) {
			return false;
		}
		switch(mut_op_id) {
		case "UET":
		case "UEE":
		case "UEC":
		case "TEI":
		case "TEC":
		case "ACT":
		case "ACM":
		case "ACC":
		case "ACS":
		case "DME":
		case "DMO":
		case "DMA":
		case "DMV":
		case "DMP":
		case "MOV":
		case "MOR":
		case "MOI":
		case "MOU":
		case "MOT":
		case "MOF":
		case "MOC":
		case "MOO":
		case "MON":
		case "JQueryAsyncCommMethodMutation":
			return true;
		}
		return false;
	}
	
	/**
	 * Finds a subject commit from given commit logs
	 * @param filepath is a path to subject file
	 * @param statement is a subject statement is the subject file
	 * @return subject commit or null if not any
	 * @throws NoHeadException
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public RevCommit trace(String filepath, String statement) throws NoHeadException, GitAPIException, IOException {
		Git git = new Git(this.repo);
		
		/// Iterates each commit
		for(RevCommit commit: git.log().call()) {
			/// Traverses diff between current commit and its parents			
			RevCommit[] parents = commit.getParents();
			for(int pid = 0; pid < parents.length; pid++) {
				RevCommit parent = commit.getParent(pid);

				/// Gets diff
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DiffFormatter formatter = new DiffFormatter(baos);
				formatter.setRepository(repo);
				formatter.format(parent.getId(), commit.getId());
				
				/// Determines whether this diff is subject by two facts below
				/// (to be modified)
				boolean subject_file = false;
				boolean subject_statement = false;

				/// Parses diff
				String[] diff_lines = baos.toString().split("\n");
				for(int i = 0; i < diff_lines.length; i++) {
					String diff_line = diff_lines[i];
					
					if(diff_line.startsWith("+++ b/") && diff_line.equals("+++ b/".concat(filepath))) {
						subject_file = true;
					}
					if(diff_line.startsWith("+") && diff_line.contains(statement)) {
						subject_statement = true;
					}
				}
				
				/// Specifies commit
				if(subject_file && subject_statement) {
					return commit;
				}
			}
		}
		return null;
	}
}
