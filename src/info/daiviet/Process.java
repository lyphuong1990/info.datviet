package info.daiviet;

import utils.Helper;

public class Process implements Runnable {

	// Chạy nhanh lên nào
	public static void main(String[] args) {
		try {
			BatDongSan bds = new BatDongSan();
			ChoTot ct = new ChoTot();
			DiaOcOnline dool = new DiaOcOnline();
			MuaBanNet mbnet = new MuaBanNet();
			MuaBanNhaDat mbnd = new MuaBanNhaDat();
			RongBay rb = new RongBay();
			BatDongSanJSon bdsjs = new BatDongSanJSon();
			//
			Thread t_bds = new Thread(bds);
			Thread t_ct = new Thread(ct);
			Thread t_dool = new Thread(dool);
			Thread t_mbnet = new Thread(mbnet);
			Thread t_mbnd = new Thread(mbnd);
			Thread t_rb = new Thread(rb);
			Thread t_bdsjs = new Thread(bdsjs);

			t_bds.start();
			t_ct.start();
			t_dool.start();
			t_mbnd.start();
			t_rb.start();
			t_mbnet.start();
			t_bdsjs.start();
			
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
