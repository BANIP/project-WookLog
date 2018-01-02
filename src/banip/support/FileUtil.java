package banip.support;

import java.io.IOException;
import java.io.InputStream;


public class FileUtil {
				
	public static String read(InputStream input) throws IOException {
		byte[] buffer = new byte[1024];
		StringBuilder result = new StringBuilder();
		while(input.read(buffer) != -1 ) {
			result.append( new String(buffer) );
		}
		return result.toString();
	}

}
