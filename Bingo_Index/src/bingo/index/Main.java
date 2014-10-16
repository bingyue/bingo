package bingo.index;

/**
 * 启动indexer，执行索引操作
 * @author BingYue
 *
 */
public class Main {
	
	/**
	 * 入口函数
	 * @param args
	 */
	public static void main(String[] args) {
		
		/**
		 * 待索引目录
		 */
//     	String dataFile="D:\\Bingo_Document\\TempFile\\";
    	String dataFile="D:\\Bingo_Document\\TxtFile\\";
		//索引存放目录
    	String indexFile="D:\\Bingo_Index\\";
//		String indexFile="D:\\Bingo_Index_News\\";
//		String indexFile="D:\\Bingo_Index_Image\\";
//		String indexFile="D:\\Bingo_Index_Music\\";
//		String indexFile="D:\\Bingo_Index_video\\";
		//启动索引服务
		Indexer.start(dataFile, indexFile);
	}

}
