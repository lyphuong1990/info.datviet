package info.daiviet;

public class Process implements Runnable{
	
	
	public static void main(String[] args) {
		try {
			BatDongSan bds = new BatDongSan();
			ChoTot ct = new ChoTot();
			DiaOcOnline dool = new DiaOcOnline();
			MuaBanNhaDat mbnd = new MuaBanNhaDat();
			RongBay rb = new RongBay();
			Thread t = new Thread(bds);
			Thread t1 = new Thread(ct);
			Thread t2 = new Thread(dool);
			Thread t3 = new Thread(mbnd);
			Thread t4 = new Thread(rb);
			while (true) {
				bds.run();
				ct.run();
				dool.run();
				mbnd.run();
				rb.run();
				t.sleep(30000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
