package by.ts.hmxy.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class ConsoleBanner {
	public static void banner() {
		InputStream inStream = ClassLoader.getSystemResourceAsStream("data/hmxy/console_banner.txt");
		if (inStream != null) {
			InputStreamReader in = new InputStreamReader(inStream);
			char[] cs = new char[64];
			try {
				while ((in.read(cs)) != -1) {
					System.out.print(cs);
				}
				System.out.println();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
