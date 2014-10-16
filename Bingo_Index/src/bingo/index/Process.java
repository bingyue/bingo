package bingo.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumberTools;

/**
 * 建立文件索引
 * @author BingYue
 */
public class Process {
	
	/**
	 * 对datafile文件进行字段切分
	 * @param dataFile 指向带切分的文件的file实例
	 * @return 切分后的文件
	 */
	public static Document fileToDocument(File dataFile){
		Document doc = new Document();
		//Lucene中的Field
		doc.add(new Field("title", dataFile.getName(), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("link", reader(dataFile, "link"), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("content", contentReader(dataFile), Store.YES,
				Index.ANALYZED));
		doc.add(new Field("size", NumberTools.longToString(dataFile.length()),
				Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("path", dataFile.getAbsolutePath(), Store.YES,
				Index.NOT_ANALYZED));
		return doc;	
	}
	
	/**
	 * 读取f中field字段的值
	 */
	public static String reader(File f, String field) {
		String content = "null";
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(field)) {
					content = line.replaceAll(field + ":", "");
					break;
				}
			}
			//关闭文件流
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * 读取f中特定的内容
	 * 
	 * @param f
	 * @return
	 */
	public static String contentReader(File f) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			for (String line = null; (line = br.readLine()) != null;) {
				i++;
				if (i >= 7) {
					sb.append(line);
				}

			}
			//关闭文件流
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().replaceAll("description:", "");
	}

}
