package project.local;

import java.util.ArrayList;
import java.util.Arrays;

public class Block {

	private int previousHash;
	private Policy policyHeader; //policy to be stored in block
	private int blockHash; //current block hash
	private ArrayList<Transaction> transactions; 
	
	public Block(int previousHash, Policy policyHeader, ArrayList<Transaction> transactions){
		this.previousHash = previousHash;
		this.transactions = transactions;
		this.policyHeader = new Policy(policyHeader);
		
		Object[] contents = {previousHash, Arrays.hashCode(policyHeader.getPolicies().toArray()), Arrays.hashCode(transactions.toArray())};
		this.blockHash = Arrays.hashCode(contents);
	}
	
	public int getPreviousHash() {
		return previousHash;
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public Policy getPolicyHeader() {
		return policyHeader;
	}
	
	public String printTransaction()
	{
		String str = "";
		for(Transaction tr : transactions)
		{
			str+=tr.toString()+"\n";
		}
		return str;
	}
	
	public void alterBlock()
	{
		this.transactions.remove(this.transactions.size()-1);
	}
	
	public int getBlockHash() {
		Object[] contents = {previousHash, Arrays.hashCode(policyHeader.getPolicies().toArray()), Arrays.hashCode(transactions.toArray())};
		this.blockHash = Arrays.hashCode(contents);		
		return blockHash;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "--------------------------------------------------\nPrevious Hash: " + previousHash
				+ "\nPolicies: \n" + policyHeader.toString() + "\nBlock Hash: " + blockHash + "\nTransactions: \n"
				+ printTransaction() + "--------------------------------------------------";
	}
	
	
	
}
