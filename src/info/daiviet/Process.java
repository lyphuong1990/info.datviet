package info.daiviet;

import java.nio.charset.Charset;

import utils.Helper;

public class Process implements Runnable{
	
	//Chạy nhanh lên nào
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
			
			
			
//			while (true) {
//				bds.run();
//				ct.run();
//				dool.run();
//				mbnet.run();
//				mbnd.run();
//				rb.run();
//				bdsjs.run();
//				t.sleep(30000);
//			}
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
