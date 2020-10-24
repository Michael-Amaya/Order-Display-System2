package xyz.potomac_foods.OrderDisplaySystem2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/** Allows you to stage items for download, then download them all at once
 * 
 * @author Michael Amaya
 *
 */
public class Downloader{
	
	ArrayList<String> toDownload;
	String downloadLocation;
	
	public Downloader(String downloadLocation) {
		toDownload = new ArrayList<>();
		this.downloadLocation = downloadLocation.substring(0, downloadLocation.length() - 1);
	}
	
	public boolean add(String element) {
		toDownload.add(element);
		return true;
	}
	
	public boolean downloadAll() throws IOException {
		ArrayList<String> foldersToBeMade = new ArrayList<>();
		
		// Check for folders that need to be made
		for (String element : toDownload) {
			String[] newElement = ((String) element).split("/");
			newElement[newElement.length - 1] = "";
			foldersToBeMade.add(String.join("/", newElement));
		}
		
		// Make folders that need to be made
		for (String folder : foldersToBeMade) {
			if (!(folder.length()-1 < 1)) {
				String newFolder = folder.substring(1, folder.length()-1);
				
				File file = new File(newFolder);
				file.mkdirs();
			}
		}
		
		// Download crap to the correct folders
		for (String element : toDownload) {
			String newElement = (String) element;
			String oldElement = (String) element;
			newElement = newElement.substring(1, newElement.length());
			// System.out.println(oldElement + " " + newElement);
			
			byte[] bytes = Utilities.getAsBytes(downloadLocation + oldElement);
			Path path = Paths.get(newElement);
			
			System.out.println("Writing " + newElement + " as bytes");
			Files.write(path, bytes);
		}
		
		return true;
	}
	
	public ArrayList<String> getElements() {
		return toDownload;
	}
	
	public String toString() {
		StringBuilder items = new StringBuilder();
		
		items.append(" {Downloader} ");
		for (String item : toDownload) {
			items.append(item).append(", ");
		}
		
		items.delete(items.length() - 2, items.length() - 1);
		
		return items.toString();
	}
}
