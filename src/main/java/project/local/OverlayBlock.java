package project.local;

import java.util.ArrayList;
import java.util.Arrays;

public class OverlayBlock {

	private int previousHash;
	//private Policy policyHeader; //policy to be stored in block
	private int blockHash; //current block hash
	private String blockID;
	private ArrayList<MultiSigTransaction> transactions; 
	
	public OverlayBlock(int previousHash, ArrayList<MultiSigTransaction> transactions){
		this.previousHash = previousHash;
		this.transactions = transactions;
		//this.policyHeader = new Policy(policyHeader);
		
		Object[] contents = {previousHash, Arrays.hashCode(transactions.toArray())};
		this.blockHash = Arrays.hashCode(contents);
	}
	
	public int getPreviousHash() {
		return previousHash;
	}
	
	public ArrayList<MultiSigTransaction> getTransactions() {
		return transactions;
	}
	
	public String printTransaction()
	{
		String str = "";
		for(MultiSigTransaction tr : transactions)
		{
			str+=tr.toString()+"\n";
		}
		return str;
	}
	
	public void setPreviousHash(int previousHash) {
		this.previousHash = previousHash;
	}
	
	public void setBlockHash(int blockHash) {
		this.blockHash = blockHash;
	}
	
	public void alterBlock()
	{
		this.transactions.remove(this.transactions.size()-1);
	}
	
	public int getBlockHash() {
		Object[] contents = {previousHash, Arrays.hashCode(transactions.toArray())};
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
				+ "\nBlock Hash: " + blockHash + "\nTransactions: \n"
				+ printTransaction() + "--------------------------------------------------";
	}
	
	
	
}
