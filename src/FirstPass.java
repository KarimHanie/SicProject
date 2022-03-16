import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class FirstPass {
	static String[][] symbolT = new String[19][2];
	String[] lctr = new String[19];
	String[] label = new String[19];
	String[] inst = new String[19];
	String[] ref = new String[19];
	String[] obCode = new String[19];
	String[] temp ;// variable that used to check the length of each line after splitting
	int length = 0;// store length of whole program
	int arryCount = 0;
	int coun = 0;// to calc length of available values inside samyblT
	String lines = null;
	
	// constructor
	public FirstPass() {
	}
	// split file into 3 array that represent label,instructions ,reference
	public void splitFile(File f) {// File f = new file();
		// try-with resources an automatic method to close the file after i open
		try (Scanner sc = new Scanner(f);) {
			// hasNext>> method that check every time if the file has data to read or not
			// return true of false(Boolean)
			while (sc.hasNext()) {
				for (int i = 0; i < 19; i++) {
					// lines >> store each line in that String variable so i can split him later
					lines = sc.nextLine();
					temp = lines.split("\t");// split String when cursor found "tab"
					System.out.println(" length of temp" + temp.length);
					// check if array temp ... his length is 3 (Store 3 values in 3 cells or not )
					if (temp.length == 3) {
						// check that value in the first cell in temp is a "\t" or not
						// if true then label have nothing on it then replace that "tab" into #
						if (temp[0].trim().length() == 0) {
							label[i] = "";
							inst[i] = temp[1];
							ref[i] = temp[2];
						} else {
							label[i] = temp[0];
							inst[i] = temp[1];
							ref[i] = temp[2];
						}
					}
					/*
					 * if temp contain only 2 cells that mean the last value is a space that cursor
					 * stopped because there is no many "\t" can be found
					 * 
					 */
					if (temp.length == 2) {
						label[i] = temp[0];
						inst[i] = temp[1];
						ref[i] = ref[0];
					}
				}
				
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();// method print the name of error happened
		}
		locationCounter(inst);
		calcLengthOfprogram();
		symbolTable(label, lctr);
		objectCode(label, inst, ref);
//	PrintSicTable();	
	}
	public void locationCounter(String[] ins) {
		for (int i = 2; i < 19; i++) {
			// check start of program to get the starting address of the program
			if (ins[i - 2].equals("Start")) {
				lctr[0] = ref[0];
				lctr[1] = Integer.toString(Integer.parseInt(ref[0]));
			}
			// chech if instruction == to word thats means we need to store an int value
			// that need 3 bytes
			if (ins[i - 1].equals("WORD")) {
				// so we take the prev address of prev instruction and convert it to decimal
				// number
				int num1 = Integer.parseInt(lctr[i - 1], 16);
				// then add +3 on that number
				num1 += 3;
				// convert that decimal number again into hexa
				String num2 = Integer.toHexString(num1).toUpperCase();
				// store that number in lctr of i
				lctr[i] = num2;
			} else if (ins[i - 1].equals("RESW")) {
				// convert number at ref[] to decimal
				int temp = (Integer.parseInt((ref[i - 1])));
				temp = temp * 3;
				// get prev location and convert it into decimal
				int num1 = Integer.parseInt(lctr[i - 1], 16);
				// add location number and ref value that i need in memory to store whatever
				num1 = num1 + temp;
				// convert that new decima number to hexa and assign him to new lctr[i]
				String num2 = Integer.toHexString(num1).toUpperCase();
				lctr[i] = num2;
			} else if (ins[i - 1].equals("RESB")) {
				// same as above
				int temp = (Integer.parseInt((ref[i - 1])));
				int num1 = Integer.parseInt(lctr[i - 1], 16);
				num1 = num1 + temp;
				String num2 = Integer.toHexString(num1).toUpperCase();
				lctr[i] = num2;
			} else if (ins[i - 1].equals("BYTE")) {
				// check if ref value of byte contains C which mean characters
				// so just count these characters and convert them into hexa and add it to get
				// the new address
				if (ref[i - 1].startsWith("C'") || ref[i - 1].startsWith("c'")) {
					// -3 in case ref[i-1] is c --> C' characters'>> so if we want to count them we
					// just need to
					// subtract C' ' from length like C'ahmedz' --> length should be 9

					int temp = (ref[i - 1].length() - 3);
					int num1 = Integer.parseInt(lctr[i - 1], 16);
					num1 = num1 + temp;
					String num2 = Integer.toHexString(num1).toUpperCase();
					lctr[i] = num2;
				}
				// second option of byte which is storing hexa number
				if (ref[i - 1].startsWith("X'") || ref[i - 1].startsWith("x'")) {
					/*
					 * to calc needed bytes to new address --------------------- |first : hexa =16
					 * bit | | byte =8bits | --------------------- so 16 hexa= 8 byte (/8) then 2
					 * hexa = 1 byte
					 * 
					 * second : then Rule is for each 2 hexa >> equal 1 byte
					 * 
					 * so count length of program divide it by 2 after subtracting {X'' from it(-3
					 * )same as C''} that will give us needed bytes to add
					 */
					int temp = ((ref[i - 1].length() - 3)) / 2;
					int num1 = Integer.parseInt(lctr[i - 1], 16);
					num1 = num1 + temp;
					String num2 = Integer.toHexString(num1).toUpperCase();
					lctr[i] = num2;
				}
				
			} 
			else if(ins[i-1].startsWith("+")) {
				System.out.println(ins[i-1]);
				int num1 = Integer.parseInt(lctr[i - 1], 16);
				num1 +=4;
				String num2 = Integer.toHexString(num1).toUpperCase();
				lctr[i] = num2;
			}
			else {
				
				// covering the rest instruction add three
				String num=Converter.calcformat(inst, i-1);
				int num1 = Integer.parseInt(lctr[i - 1], 16);
				int num3 = Integer.parseInt(num) ;
				num1+=num3;
				
				String num2 = Integer.toHexString(num1).toUpperCase();
				lctr[i] = num2;
			}
		}
	}
	public void objectCode(String[] label, String[] inst, String[] ref) {
		int i = 0;
		while (!label[i].equals("END")) {
			if (inst[i].equals("RESW") || inst[i].equals("RESB") || inst[i].equals("Start")) {
				obCode[i] = "NO objectCode";
			} else if (inst[i].equals("WORD")) {
				// use fomating method %X to print hexadecimal number ,,06 cause we want to
				// represent
				// number in 6 bits cause each instruction need 3 bytes
				// convert string value in REF array to decimal INt number so we can convert him
				// to hexadecimal
				obCode[i] = String.format("%06X", Integer.parseInt(ref[i]));
			} else if (inst[i].equals("BYTE") && (ref[i].contains("X'") || ref[i].contains("x'"))) {
				obCode[i] = ref[i].substring(2, ref[i].length() - 1);
			} else if (inst[i].equals("BYTE") && (ref[i].contains("C'") || ref[i].contains("c'"))) {
				String st = ref[i].substring(2, ref[i].length() - 1);
				String asc = "";
				int j = 0;
				while (j < st.length()) {
					int ascii = (int) st.charAt(j);
					asc += ascii;
					j++;
				}
				obCode[i] = asc;
			} else if (ref[i].contains(",X")) {
				int temp = Integer.parseInt(calcAddressObjectCode(symbolT, ref, inst, i, false), 16)
						+ Integer.parseInt("8000", 16);
				String objectC = Converter.calcOpcode(inst, i) + "" + Integer.toHexString(temp).toUpperCase();

				obCode[i] = objectC;
			} else {
				int temp = Integer.parseInt(calcAddressObjectCode(symbolT, ref, inst, i, true), 16);
				String objectC = Converter.calcOpcode(inst, i) + "" + Integer.toHexString(temp).toUpperCase();
				obCode[i] = objectC;
			}
			i++;
		}
	}
// public String calcOpCode(String[] inst,int k) {
//	 this.inst=inst;
//	 Converter cnv=new Converter();
//	 cnv.initialize();
//	 String obC="";
//	 for(int i=0;i<cnv.OPTAB.length;i++) {
//		 for(int j=0;j<cnv.OPTAB[i].length;j++) {
//			 if(cnv.OPTAB[i][j].contains(inst[k])) {
//				 obC=cnv.OPTAB[i][2];
//				 return obC;
//			 }
//		 }
//	 }
//	 
//	 return "";
// }
	public String calcAddressObjectCode(String[][] symblt, String[] ref, String[] ins, int k, boolean direct) {
		if (direct == false) {
			String labl = ref[k].substring(0, ref[k].length() - 2);
//		 System.out.println("labl:"+labl);
			for (int i = 0; i < symblt.length; i++) {
				for (int j = 0; j < symblt[i].length; j++) {
//				System.out.print(symblt[i][j]+" ");
					if (symblt[i][j].contains(labl)) {
						return symblt[i][1];
					}
				}
				System.out.println();
			}
			return " empty";
		} else {
			String labl = ref[k];
//		 System.out.println("labl:"+labl);
			for (int i = 0; i < symblt.length; i++) {
				for (int j = 0; j < symblt[i].length; j++) {
//				System.out.print(symblt[i][j]+" ");
					if (symblt[i][j].contains(labl)) {
						return symblt[i][1];
					}
				}
				System.out.println();
			}
			return " empty";
		}
	}

	public void calcLengthOfprogram() {
		this.length = Integer.parseInt(lctr[18], 16) - Integer.parseInt(lctr[0], 16);
	}

	public void PrintSicTable() {
		System.out.printf("%s\t%s\t%s\t%s\t%s", "Lctr", "Label", "Inst", "Ref", "ObCode" + "\n");
		for (int i = 0; i < 19; i++)
			System.out.printf("%s\t%s\t%s\t%s\t%s", lctr[i], label[i], inst[i], ref[i], obCode[i] + "\n");
//	 System.out.println("");
		System.out.println(" \n length of whole programe is: " + Integer.toHexString(this.length).toUpperCase());
		System.out.println("-----------------Symbol table-----------------");
		System.out.printf("\t%s\t%s", "Label", "location" + "\n");
		for (int I = 0; I < coun; I++) {
			for (int j = 0; j < 2; j++) {
				System.out.printf("\t" + "%s" + "", symbolT[I][j]);
			}
			System.out.println();
		}
		System.out.println("-----------------HTE-----------------");

		hteRecord(lctr, label, inst, ref, obCode);
	}

	public void symbolTable(String[] lab, String[] location) {

//	System.out.println("-----------------Symbol table-----------------");
//	 System.out.printf("\t%s\t%s","Label","location"+"\n");
//	for(int i=1;i<19;i++) {
//		if(!lab[i].startsWith("#") && !lab[i].equals("END") ) {
////			 System.out.printf("\t\t%s\t%s",lab[i],location[i]+"\n");
//			 smbolTCount++; 
//		}
//	}
		// coun " counter"
		// first for-loop scan label to find variables
		// if condition check if this cell is emty or not
		// second for-loop store name of variables and its addresses in 2d array
		for (int i = 1; i < 19; i++) {
			if (!(lab[i].trim().length()==0) && !lab[i].equals("END")) {
				for (int k = 0; k < 2; k++) {
					if (k == 0)
						symbolT[coun][k] = lab[i];
					if (k == 1)
						symbolT[coun][k] = lctr[i];
				}
				coun++;
			}
		}
	}

	public void hteRecord(String[] lctr, String[] label, String[] inst, String[] ref, String[] obCode) {

//		int loct=Integer.parseInt(lctr[1],16);
//		String lc=String.format("%06x", loct);
//		System.out.println("loct:"+lc);
//		int lastlctr=Integer.parseInt(lctr[lctr.length-1],16);
//		String lastlc=String.format("%06x", lastlctr);
//		
		int count = 0;
		for (int i = 0; i < label.length; i++) {
			if (inst[i].equalsIgnoreCase("start")) {
				System.out.println("H^" + label[i].toUpperCase() + "^" + converTo6bits(ref, i).toUpperCase() + "^"
						+ String.format("%06x", this.length));
			} else if (label[i].equalsIgnoreCase("end")) {
				System.out.println("\nE^" + converTo6bits(lctr, 0));
				break;
			} else {
				if (inst[i].equalsIgnoreCase("resw") || inst[i].equalsIgnoreCase("resb")) {
					continue;
				} else {
					if (count < 10) {
						System.out.print( obCode[i] + "^");
						count++;
					} else {
						System.out.println();
						count = 0;
					}
				}
			}
		}
	}
	public String converTo6bits(String[] lctr, int index) {
		int loct = Integer.parseInt(lctr[index], 16);
		String lc = String.format("%06x", loct);
		return lc;
	}
}