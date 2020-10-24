package xyz.potomac_foods.OrderDisplaySystem2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	public static String getHTML(String webURL) throws IOException {
		StringBuilder linesOfCode = new StringBuilder();
		URL webPage;
		
		webPage = new URL(webURL);
		BufferedReader in = new BufferedReader(new InputStreamReader(webPage.openStream()));
		
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			linesOfCode.append(inputLine).append("\n");
		
		in.close();
		
		return linesOfCode.toString();
	}
	
	public static boolean hasInternetConnection() {
		try {
			URL url = new URL("https://www.google.com/"); 
	        URLConnection connection;
			connection = url.openConnection();
			connection.connect(); 
		} catch (Exception e) {
			return false;
		} 
		
		return true;
	}
	
	public static byte[] getAsBytes(String webURL) throws IOException {
		URL url = new URL(webURL);
		InputStream in = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1!=(n=in.read(buf))){
		   out.write(buf, 0, n);
		}
		
		out.close();
		in.close();
		byte[] response = out.toByteArray();
		
		return response;
	}
	
	public static String getFileExtension(String fileName) {
		return fileName.split("\\.")[fileName.split("\\.").length - 1];
	}
	
	public static boolean versionMatcher(String versionOnSystem, String versionOnline) {
		String vOnSys = "";
		String vOnline = "1";
		
		//vX where v is just a letter and X is the version
		Pattern versionPattern = Pattern.compile("[0-1]+.+.");
		Matcher versionOnSystemFound = versionPattern.matcher(versionOnSystem);
		Matcher versionOnlineFound = versionPattern.matcher(versionOnline);
		
		while(versionOnSystemFound.find())
			vOnSys = versionOnSystemFound.group(0);
			
		while(versionOnlineFound.find())
			vOnline = versionOnlineFound.group(0);
			
		if (vOnSys != vOnline)
			return false;
		
		return true;
	}
}
