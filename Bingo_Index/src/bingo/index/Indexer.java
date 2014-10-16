package bingo.index;

import java.io.File;
import java.io.IOException;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * 对指定的文件建立索引
 * @author BingYue
 */
public class Indexer {
	
	/**
	 * 调用内部静态方法
	 * 开始索引创建工作
	 * 使用Lucene索引器的步骤大都相同，类似JDBC操作数据库的实现
	 */
	public static void start(String dataDir, String indexDir){
		
		//Java IO操作
		//在内存里创建名字为pathname的File对象
		File dataFile=new File(dataDir);
		File indexFile=new File(indexDir);
		//
		index(dataFile, indexFile);
		//完成索引操作
		quit();
	}
	
	/**
	 * 执行索引创建工作
	 * @param dataFile
	 * @param indexFile
	 */
	private static void index(File dataFile, File indexFile) {
		//验证待索引文件目录是否正确
		if (!dataFile.exists() || !dataFile.isDirectory()) {
			System.out.println("Error message:" + dataFile + " does not exist or is not a directory.");
		}
		//创建一个分词器
		//这里使用MMAnalyzer，作为中文分词组件
		Analyzer zh_cnAnalyzer = new MMAnalyzer();
		
		//创建索引器
		//IndexWriter是Lucene重要的类，执行创建索引等工作
		
		try {
			//创建IndexWriter：文件路径，分词器，是否创建，最大长度（默认不做限制）
			//此处iscreate为true,不管目录下是否有文件，都会重新删除后新建
			IndexWriter writer = new IndexWriter(indexFile, zh_cnAnalyzer, true, MaxFieldLength.UNLIMITED);
			//调用索引方法
			indexDirectory(writer, dataFile);
			/**
			 * 索引优化操作
			 * Lucene 3.5以后的版本已经不再提供这个方法
			 * 取而代之的是forcemerge
			 */
			writer.optimize();
			//创建完毕 关闭索引器
			writer.close();
	
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 对目录下的txt文件进行索引操作
	 * @param writer 索引器实例
	 * @param dir 待索引文件（目录）
	 */
	private static void indexDirectory(IndexWriter writer, File dir) {
		
		//获取当前文件目录下的所有文件和文件夹
		File[] files = dir.listFiles();
		/**
		 * 遍历File[]集合
		 * 发现文件夹，执行递归操作，深度搜索
		 * txt文本文档则执行索引indexfile()
		 */
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			//抽象路径名表示的文件是否是一个目录
			//发现下级目录存在 递归操作
			if (f.isDirectory()) {
				indexDirectory(writer, f);
			} 
			//String endsWith()方法
			//实现文件过滤器的操作
			else if (f.getName().endsWith(".txt")) {
				indexFile(writer, f);
			}
		}
	}
    /**
     * 完成索引文件操作
     * @param writer
     * @param f
     */
	private static void indexFile(IndexWriter writer, File f) {
		//鲁棒性，考虑到特殊情况
		//文件夹可能是隐藏或者不可读写，退出操作
		if (f.isHidden() || !f.exists() || !f.canRead()) {
			return;
		}
		//调用fileToDocument()
		Document doc = Process.fileToDocument(f);
		try {
			writer.addDocument(doc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 退出，结束索引操作
	 */
	private static void quit() {
		System.out.println("索引建立完毕,程序退出.");
		System.exit(0);
	}
}

