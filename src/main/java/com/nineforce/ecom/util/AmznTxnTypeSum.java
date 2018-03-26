package com.nineforce.ecom.util;

// this tracks transaction in the cvs file. 
// Only do summary info now... many have detail line info

public class AmznTxnTypeSum {
	AmznTxnType txnType; 
	int totalTxnCnt;
	float totalTxnAmt;  //use currency? 
	
	public AmznTxnTypeSum(AmznTxnType aTxnType) {
		this.txnType = aTxnType; 
		totalTxnCnt = 0;
		totalTxnAmt = 0;
	}
	
	public void increaseTxnCnt() {
		totalTxnCnt++;
	}
	
	public void addTxnAmt(float mnt) {
		totalTxnAmt = totalTxnAmt + mnt; 
	}
	
	public String toString() {
		return " Total Txn " + totalTxnCnt + ", and Total Amount " + totalTxnAmt;
	}
}
