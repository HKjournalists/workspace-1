package bupt.longlong.qunar.itineray.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import bupt.longlong.qunar.itineray.ie.DayTrip;
import bupt.longlong.qunar.itineray.ie.Itineray;
import bupt.longlong.qunar.itineray.ie.ItinerayExtractor;
import bupt.longlong.qunar.itineray.ie.Sight;

public class TestDict {

	void s() {
		String desc = "";
		try {
			FileInputStream fis = new FileInputStream("C:\\mango\\" + 1 + ".txt");
			byte[] bs = new byte[fis.available()];
			fis.read(bs);
			desc = new String(bs, "gbk");
			fis.close();
		} catch(Exception e) {
			
		}
		ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
		appendString("C:\\mango\\test_" + 1 + ".txt", 1 + "");
		Itineray it = itinerayExtractor.extract2(desc);
		for(DayTrip trip : it.getDayTrips()) {
			System.out.println(trip.getToSights());
		}
		System.out.println(desc);
		for(DayTrip dt : it.getDayTrips()) {
			appendString("C:\\mango\\test_" + 1 + ".txt", dt.getTitle());
			appendString("C:\\mango\\test_" + 1 + ".txt", dt.getDescription());
			for(Sight s : dt.getToRegions()) {
				appendString("C:\\mango\\test_" + 1 + ".txt", s.getName());
			}					
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestDict dc = new TestDict();
		dc.s();
//		for(int i = 1; i <=1; i++ ) {
//			tt t = new tt(i);
//			t.start();
//		}

	}
	protected static void appendString(String path, String content) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(path, "rw");
			raf.seek(raf.length());
			raf.write((content + "\r\n").getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	static class tt extends Thread {
		int i;
		String desc = "";
		public tt(int i) {
			this.i = i;
			try {
				FileInputStream fis = new FileInputStream("C:\\mango\\" + i + ".txt");
				byte[] bs = new byte[fis.available()];
				fis.read(bs);
				desc = new String(bs, "gbk");
				fis.close();
			} catch(Exception e) {
				
			}
			
		}
		protected void appendString(String path, String content) {
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(path, "rw");
				raf.seek(raf.length());
				raf.write((content + "\r\n").getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (raf != null) {
					try {
						raf.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		public void run() {
			for(int i = 0; i < 1; i++) {
				ItinerayExtractor itinerayExtractor = ItinerayExtractor.getInstance();
				appendString("C:\\mango\\test_" + this.i + ".txt", i + "");
				Itineray it = itinerayExtractor.extract2(desc);
				for(DayTrip trip : it.getDayTrips()) {
					System.out.println(trip.getToSights());
				}
				System.out.println(desc);
				int x = 3;
				for(DayTrip dt : it.getDayTrips()) {
					appendString("C:\\mango\\test_" + this.i + ".txt", dt.getTitle());
					appendString("C:\\mango\\test_" + this.i + ".txt", dt.getDescription());
					for(Sight s : dt.getToRegions()) {
						appendString("C:\\mango\\test_" + this.i + ".txt", s.getName());
					}					
				}
			}
		}
	}

}
