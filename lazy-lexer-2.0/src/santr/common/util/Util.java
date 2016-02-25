package santr.common.util;

public class Util {

	private static int[] primeNumberArray = new int[128];
	
	static{
		initPrimeNumber();
	}
	
	public static void initPrimeNumber(){
		int count = 0;
		for(int i=3,j;count<128;i++){
			  int k=(int) Math.sqrt(i);
			  for(j=2;j<=k;j++){
			      if(i%j==0){
			    	  
			   break;
			      }
			  }
			  if(j>k){
				  primeNumberArray[count] = i;
				  count++;
			  }
		}
	}
	
	public static int getPrimeNumber(char charT){
		return primeNumberArray[charT];
	}
	
	public static void main(String[] args) {
		long number = 1l;
		for(int i = 0 ; i < primeNumberArray.length;i++){
			number = number * primeNumberArray[i];
		}
		System.out.println(number);
		System.out.println(number%primeNumberArray[20]);
		

	}
}
