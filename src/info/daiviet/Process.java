package info.daiviet;

import utils.Helper;

public class Process implements Runnable{
	
	
	public static void main(String[] args) {
		try {
			BatDongSan bds = new BatDongSan();
			ChoTot ct = new ChoTot();
			DiaOcOnline dool = new DiaOcOnline();
			MuaBanNhaDat mbnd = new MuaBanNhaDat();
			RongBay rb = new RongBay();
			Thread t = new Thread();
			while (true) {
				bds.run();
				ct.run();
				dool.run();
				mbnd.run();
				rb.run();
				t.sleep(30000);
			}
		} catch (Exception e) {
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
