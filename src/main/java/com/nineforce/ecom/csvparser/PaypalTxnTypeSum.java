package com.nineforce.ecom.csvparser;

public class PaypalTxnTypeSum {

	PaypalTxnTypeEnum txnType; 
	int totalTxnCnt;
	double totalTxnAmt;  //Currency only has symbols. Use double for amount 
	
	public PaypalTxnTypeSum(PaypalTxnTypeEnum aTxnType) {
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
		return "Paypal Total Txn " + totalTxnCnt + ", and Total Amount " + totalTxnAmt;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
