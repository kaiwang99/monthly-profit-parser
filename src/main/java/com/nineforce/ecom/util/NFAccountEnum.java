package com.nineforce.ecom.util;

public class NFAccountEnum {
	public enum AMZN {
		TQS("tqs"), WSD("wsd"), SQB("sqb"), AD("ad"), HG("hg"); 
		final String amznAccountName;
		
		AMZN(String name) {
			this.amznAccountName = name; 
		}
		
		public String getAccountName() {
			return this.amznAccountName;
		}
		
		static public NFAccountEnum.AMZN getEnumType(String strType) {
			for (AMZN p : AMZN.values()) {
				if(strType.equals(p.getAccountName()))
					return p; 
			}
			System.out.println("ERROR type[" + strType + "]");
			System.exit(1);
			return null; 
		}
	}

	public enum WMT {
		TQS;
	}
	
	public enum EBAY {
		TQS, WSD, VE, SS;
	}
}
