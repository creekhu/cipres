/**
 *
 */
package org.ngbw.sdk.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Roland H. Niedner <br />
 *
 */
public class PServerClient {

	private static Socket connect(String host, Integer port) {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Don't know about host: " + host + ".");
		} catch (IOException e) {
			throw new RuntimeException("Couldn't get I/O for "
					+ "the connection to: " + host + ".");
		}
		return socket;
	}

	private static void disconnect(Socket socket) {
		if (socket == null || socket.isClosed())
			return;
		try {
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException("Couldn't close socket ");
		} finally {
			socket = null;
		}
	}

	private static String write2Socket(Socket socket, String input) {
		BufferedReader in = null;
		PrintWriter out = null;
		StringBuffer output = new StringBuffer();
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
	                socket.getInputStream()));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		try {
			out.println(input);
			System.out.println("Client OUT>" + input);
			while ((line = in.readLine()) != null){
				output.append(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading from socket.");
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out.close();
		}
		return output.toString();
	}

	public static void main(String[] args) throws IOException {
		Socket socket = connect("127.0.0.1", 8189);
		String output = write2Socket(socket ,"RUN date\n");
		System.out.println("Client IN>" + output);
		disconnect(socket);
	}

}
