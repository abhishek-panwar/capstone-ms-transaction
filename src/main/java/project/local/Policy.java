package project.local;

import java.util.ArrayList;
import java.util.Arrays;

public class Policy {

	private ArrayList<String[]> policies = new ArrayList<String[]>(); //requester, request for, device id, action
	
	/**
	 * 
	 * @param Requester, Request for, Device id, Action
	 */
	
	public Policy() {
		// TODO Auto-generated constructor stub
	}
	
	public Policy(Policy policy) {
		// TODO Auto-generated constructor stub
		this.policies = new ArrayList<String[]>(policy.policies);
	}
	
		
	public void addPolicy(String[] policy)
	{
		this.policies.add(policy);
	}
	
	public ArrayList<String[]> getPolicies() {
		return policies;
	}
	
	public void deletePolicy(String[] policy)
	{
		
	}
	
	public boolean checkPolicy(String[] policy)
	{
		for(String[] str : this.policies)
		{
			if(str[0].equals(policy[0]) && str[1].equals(policy[1]) && str[2].equals(policy[2]) && str[3].equals("allow"))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "";
		for(String[] policy : policies)
		{
			str+=Arrays.toString(policy)+"\n";
		}
		return str;
	}
	
	
	
	
}
