package com.nineforce.ecom.util;

// this tracks transaction in the cvs file. 
// Only do summary info now... many have detail line info

public class AmznTxnTypeSum {
	AmznTxnTypeEnum txnType; 
	int totalTxnCnt;
	double totalTxnAmt;  //Currency only has symbols. Use double for amount 
	
	public AmznTxnTypeSum(AmznTxnTypeEnum aTxnType) {
		this.txnType = aTxnType; 
		totalTxnCnt = 0;
		totalTxnAmt = 0;
	}
	
	public void increaseTxnCnt() {
		totalTxnCnt++;
	}
	
	public void addTxnAmt(double mnt) {
		totalTxnAmt = totalTxnAmt + mnt; 
	}
	
	public int getTotalTxnCnt() { return totalTxnCnt;}
	public double getTotalTxnAmt() { return totalTxnAmt;}	
	
	public String toString() {
		return " Total Txn " + totalTxnCnt + ", and Total Amount " + totalTxnAmt;
	}
}
