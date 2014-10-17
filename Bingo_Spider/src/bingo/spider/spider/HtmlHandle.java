package bingo.spider.spider;

import javax.swing.text.html.HTMLEditorKit;

public class HtmlHandle extends HTMLEditorKit {

	/**
	 * 默认序列化ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 使用Swing HTML解析器
	 * HTMLEditorKit中getParser()方法是Protected,
	 * 只能通过重写父类方法来使用
	 */
	@Override
	public Parser getParser(){
		return super.getParser();
	}

}
