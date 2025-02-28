package Wordle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gillj
 * @version 1.0
 * @created 14-Feb-2025 1:31:08 PM
 */
public class Vocabulary {

	private String filePath;
	private List<String> words;

	public Vocabulary(){

	}

	/**
	 * 
	 * @param file
	 */
	public void changeFile(String file){

	}

	public String getRandomWord(){
		return "";
	}

	/*
		Loads the words
	 */
	public void loadWords()  {
		filePath = "src/Wordle/wordle-full-1.txt";
		try {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		words = new ArrayList<>();
		String line;
		while((line = br.readLine()) != null) {
    words.add(line);
		}
		} catch (IOException e) {
    e.printStackTrace();
  }
	}
	public List<String> getWords() {
			return words;
	}

}