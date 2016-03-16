package jp.mzw.revajaxmutator.genprog;

import java.util.ArrayList;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;

public class Mutation {

	MutationFileInformation mutationFileInfo;
	ArrayList<MutationFileInformation> originList;//Path?
	int fitness;//weight
	
	public Mutation(MutationFileInformation mutation) {
		mutationFileInfo = mutation;
		originList = new ArrayList<MutationFileInformation>();
		originList.add(mutation);
		fitness = 0;
	}
	
	public Mutation(MutationFileInformation mutation, ArrayList<MutationFileInformation> originList) {
		mutationFileInfo = mutation;
		this.originList = originList;
		fitness = 0;
	}
	
	public MutationFileInformation getMutationFileInformation() {
		return mutationFileInfo;
	}
	
	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	
	public int getFitness() {
		return fitness;
	}
	
	
	public void addOrigin(Mutation mutation) {
		for(MutationFileInformation origin : mutation.getOriginList()) {
			boolean isContained = false;
			for(MutationFileInformation myorigin : this.originList) {
				if(origin.getAbsolutePath().equals(myorigin.getAbsolutePath())) {
					isContained = true;
					break;
				}
			}
			if(!isContained) {
				this.originList.add(origin);
			}
		}
	}
	public ArrayList<MutationFileInformation> getOriginList() {
		return this.originList;
	}
	
	
}
