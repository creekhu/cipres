package org.ngbw.examples;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OptionalDataException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ngbw.sdk.Workbench;


/**
 * This servlet resides on the server and accepts requests for ray-traced images of the currently
 * displayed scene. The applet sends a String content of the povray scene description file
 * that needs to be saved with a .pov extension and fed into the povray executable using the 
 * command line below.
 * There are a number of parameters that need to be provided as well, such as image dimensions and
 * on/off antialiasing switch.
 * 
 * @author Sasha Buzko 
 * @author R. Hannes Niedner 
 *
 */
public class RayTraceDispatcher extends HttpServlet {
	
	private static final long serialVersionUID = 5938611837943764080L;
	@SuppressWarnings("unused")
	private HttpServletResponse res;
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doGet(req, res);
	}
	
	public void doGet(InputStream is /*HttpServletRequest req, HttpServletResponse res */)
	throws ServletException, IOException, OptionalDataException {
		//this.res = res;

		//reading the content of the povray input file from the stream
		BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(/*req.getInputStream()*/ is)));
	
		//res.setContentType("application/octet-stream");
		
		//first line contains parameters (width and height), which are not part of the scene description
		//and should not be saved to the input file for povray
		String params = in.readLine();
		StringTokenizer tok = new StringTokenizer(params);
		int width, height;
		try {
			width = Integer.parseInt(tok.nextToken());
			height = Integer.parseInt(tok.nextToken());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}
		//following lines go into the file
		StringBuilder input = new StringBuilder();
		while (in.ready()){
			input.append(in.readLine() + "\n");
		}
		byte[] output = null;
		byte[] inputBytes = null;
		try {
			inputBytes = input.toString().getBytes();
		} catch (OutOfMemoryError er) {
			System.err.println("Max memory: " + Runtime.getRuntime().maxMemory());
			er.printStackTrace();
			throw new RuntimeException("OutOfMemoryError");
		}
		//generate the image
		output = process(inputBytes, width, height);
		
		//Debug//
		FileOutputStream fos = new FileOutputStream("target/output.png");
		BufferedOutputStream bfos = new BufferedOutputStream(fos);
		bfos.write(output);
		bfos.flush();
		bfos.close();
		
		//send image data back
//		DataOutputStream out =
//			new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(res.getOutputStream())));
//		out.write(output);
//		out.flush();
//		out.close();
	}
	
	private byte[] process(byte[] input, int width, int height) {
		try {
			// collect the input files
			String inputFile = "input.pov";
			Map<String, byte[]> inputMap = new HashMap<String, byte[]>(1);
			inputMap.put(inputFile, input);
			
			// collect the output filenames
			String outputFile1 = "out.png";
			String outputFile2 = "stderr.txt";
			Map<String, String> output = new HashMap<String, String>(2);
			output.put("image", outputFile1);
			output.put("stderr", outputFile2);
			
			// build the command
			String toolId = "POVRAY";
			String[] command = new String[8];
			command[0] = "povray";
			command[1] = inputFile;
			command[2] = "+A";
			command[3] = "-W" + width;
			command[4] = "-H" + height;
			command[5] = "-D";
			command[6] = "-o" + outputFile1;
			command[7] = ">& " + outputFile2;
			
			// execute the command
			Workbench workbench = Workbench.getInstance();
			/*
			Map<String, Map<String, byte[]>> outputMap = workbench.runCommand(toolId, command, inputMap, output, false);
			
			System.err.println(outputMap.get("stderr").get(outputFile2));
			
			return outputMap.get("image").get(outputFile1);
			*/

			return null;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) throws IOException, ServletException {
		RayTraceDispatcher disp = new RayTraceDispatcher();

		InputStream in =
			RayTraceDispatcher.class.getResourceAsStream("102m.pov");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(bos); 
		
		int width = 800;
		int height = 600;
		String prefix = width + " " + height + "\n";
		gzip.write(prefix.getBytes());
		byte[] buffer = new byte[1024];
		while (in.available() > 0){
			in.read(buffer);
			gzip.write((buffer));
			buffer = new byte[1024];
		}
		gzip.finish();
		disp.doGet(new ByteArrayInputStream(bos.toByteArray()));
		gzip.close();
	}
}
