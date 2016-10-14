package org.ngbw.sdk.common.util;
import java.io.File;

public class ConvertEncoding
{
	/*
		converts the file to ascii with unix style eol.
		uses convertEncoding.sh script which should be installed under $SDK_VERSIONS.
			convertEncoding uses "file -i" to determine current encoding, iconv to convert to ascii
			and zip to conver the line endings.

		probably safe to call on binary files, but I haven't tested extensively.
	*/
	public static void convertInPlace(File file) throws Exception
	{
		ProcessRunner pr = new ProcessRunner(true);
		int exitCode = pr.run("sh convertEncoding.sh " + file.getAbsolutePath());
		if (pr.getStdOut().length() > 0 || pr.getStdErr().length() > 0)
		{
			throw new Exception("convertEncoding.sh says: " + pr.getStdOut() + "\n" + pr.getStdErr());
		}
	}
	
}
