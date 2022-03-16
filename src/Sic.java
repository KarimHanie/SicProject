import java.io.File;

public class Sic {
	public static void main(String[] args) {
		FirstPass f = new FirstPass();
		f.splitFile(new File("D:\\1.aast\\1.college\\term 5\\system programming\\inSIC.txt"));
		f.PrintSicTable();
		// f.locationCounter(f.inst);
//		f.calcLengthOfprogram();
//		f.objectCode(f.label,f.inst,f.ref);

//		f.symbolTable(f.label, f.lctr);

	}
}
