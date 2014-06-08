package org.ideademo.nexus.services.util;

import java.io.*;

public class IOUtils {
	private static final int EOF = -1;

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 20;

	public static int copy(InputStream input, OutputStream output) throws IOException {
	    long count = copyLarge(input, output);
	    if (count > Integer.MAX_VALUE) {
	        return -1;
	    }
	    return (int) count;
	}

	public static long copyLarge(InputStream input, OutputStream output)
	        throws IOException {
	    return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	public static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
	        throws IOException {
	    long count = 0;
	    int n = 0;
	    while (EOF != (n = input.read(buffer))) {
	        output.write(buffer, 0, n);
	        count += n;
	    }
	    return count;
	}
}
