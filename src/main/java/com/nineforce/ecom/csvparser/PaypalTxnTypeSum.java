package com.nineforce.ecom.csvparser;

public class PaypalTxnTypeSum {

	PaypalTxnTypeEnum txnType; 
	int totalTxnCnt;
	double totalTxnGross, totalTxnFee, totalTxnGrossNet;  //Currency only has symbols. Use double for amount 
	
	public PaypalTxnTypeSum(PaypalTxnTypeEnum aTxnType) {
		this.txnType = aTxnType; 
		totalTxnCnt = 0;
		totalTxnGross = 0;
		totalTxnFee = 0;
		totalTxnGrossNet = 0;
	}
	
	public void increaseTxnCnt() {
		totalTxnCnt++;
	}
	
	public void addTxnGross(double mnt) {
		totalTxnGross = totalTxnGross + mnt; 
	}
	public void addTxnFee(double mnt) {
		totalTxnFee = totalTxnFee + mnt; 
	}
	public void addTxnGrossNet(double mnt) {
		totalTxnGrossNet = totalTxnGrossNet + mnt; 
	}
	
	public int getTotalTxnCnt() { return totalTxnCnt;}
	public double getTotalTxnGross() { return totalTxnGross;}	
	public double getTotalTxnFee() { return totalTxnFee;}	
	public double getTotalTxnGrossNet() { return totalTxnGrossNet;}	
	
	public String toString() {
		return txnType + " Paypal Total Txn number " + totalTxnCnt + ", and Total Gross " + totalTxnGross + 
				", and Total Fee " + totalTxnFee + ", total Net " + totalTxnGrossNet;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
