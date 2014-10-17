package bingo.spider.spider;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 构造Swing面板
 * 声明实现 Runnable接口的类,实现爬虫
 * @author BingYue
 *
 */
public class Main extends JFrame implements Runnable,ISpiderReportable{

	/**
	 * 默认的序列化ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * SWing面板 的系列元素
	 */
	JLabel promoteLabel = new JLabel();
	//开始按钮
	JButton begin = new JButton();
	//输入网址文本框
	JTextField url = new JTextField();
	//当前进程 标签
	JLabel current = new JLabel();
	//正确链接（访问正常的链接）
	JLabel goodLinksLabel = new JLabel();
    //死链 计数
	//死链接指原来正常，后来失效的链接。
	protected int badLinksCount = 0;
	//死链 标签
	JLabel badLinksLabel = new JLabel();
	//正常链接计数
	protected int goodLinksCount = 0;
	//下方滚动条
	JScrollPane errorScroll = new JScrollPane();

	JTextArea badLinksTextArea = new JTextArea();

	protected Thread bgThread;

	protected Spider spider;

	protected URL base;
	
	public Main(){
		//构造器 调用生成面板方法
		drawSpiderFrame();
	}
	
	/**
	 * Main类的main方法
	 * @param args
	 */
	static public void main(String args[]) {
		(new Main()).setVisible(true);
	}
	/**
	 * 描绘界面
	 */
	public void drawSpiderFrame() {
		/**
		 * 爬虫面板
		 */
		setSize(405, 288);
		setVisible(true);
		setTitle("Bingo_Spider");
		//相对于父窗体位置 null表示置于中央
//		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		/**
		 * 向面板中添加元素
		 */
		promoteLabel.setBounds(12, 12, 84, 12);
		promoteLabel.setText("请输入URL:");
		getContentPane().add(promoteLabel);

		//开始按钮
		begin.setBounds(12, 36, 84, 24);
		begin.setText("开始");
		begin.setActionCommand("Begin");
		getContentPane().add(begin);

		url.setBounds(108, 36, 288, 24);
		getContentPane().add(url);
		//滚动
		errorScroll.setBounds(12, 120, 384, 156);
		errorScroll.setAutoscrolls(true);
		errorScroll
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		errorScroll
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		errorScroll.setOpaque(true);
		getContentPane().add(errorScroll);
		
		//
		badLinksTextArea.setEditable(false);
		badLinksTextArea.setBounds(0, 0, 366, 138);
		errorScroll.getViewport().add(badLinksTextArea);

		current.setText("正在爬行: ");
		current.setBounds(12, 72, 384, 12);
		getContentPane().add(current);

		goodLinksLabel.setText("正常链接数目: 0");
		goodLinksLabel.setBounds(12, 96, 192, 12);
		getContentPane().add(goodLinksLabel);
		badLinksLabel.setText("死链数目: 0");
		badLinksLabel.setBounds(216, 96, 96, 12);
		getContentPane().add(badLinksLabel);

		beginningActionListener begActListener = new beginningActionListener();
		begin.addActionListener(begActListener);
	}
	
	/**
	 * 事件通知
	 */
	public void addNotify() {
		Dimension size = getSize();

		super.addNotify();

		if (frameSizeAdjusted)
			return;
		frameSizeAdjusted = true;

		Insets insets = getInsets();
		javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
		int menuBarHeight = 0;
		if (menuBar != null)
			menuBarHeight = menuBar.getPreferredSize().height;
		setSize(insets.left + insets.right + size.width, insets.top
				+ insets.bottom + size.height + menuBarHeight);
	}

	boolean frameSizeAdjusted = false;

	/**
	 * 内部类 事件动作
	 * 监听 开始/取消 按钮动作
	 */
	class beginningActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object object = event.getSource();
			if (object == begin)
				beginningActionPerformed(event);
		}
	}
	/**
	 * @param event
	 */
	void beginningActionPerformed(ActionEvent event) {
		if (bgThread == null) {
			begin.setText("Cancel");
			bgThread = new Thread(this);
			bgThread.start();
			goodLinksCount = 0;
			badLinksCount = 0;
		} else {
			spider.cancel();
		}

	}
	/**
	 * ISpiderReportable接口方法的实现
	 * 标志可用的链接
	 * @param base
	 * @param url
	 * @return
	 */
	public boolean spiderFoundURL(URL base, URL url) {
		UpdateCurrentStats cs = new UpdateCurrentStats();
		cs.msg = url.toString();
		SwingUtilities.invokeLater(cs);

		if (!checkLink(url)) {
			UpdateErrors err = new UpdateErrors();
			err.msg = url + "(on page " + base + ")\n";
			SwingUtilities.invokeLater(err);
			badLinksCount++;
			return false;
		}

		goodLinksCount++;
		return true;
	}
	/**
	 * 检查url连接是否正常
	 * @param url
	 * @return
	 */
	protected boolean checkLink(URL url) {
		try {
			//连接正常返回true，否则返回false
			URLConnection connection = url.openConnection();
			connection.connect();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	class UpdateErrors implements Runnable {
		public String msg;

		public void run() {
			badLinksTextArea.append(msg);
		}
	}
	
	class UpdateCurrentStats implements Runnable {
		public String msg;

		public void run() {
			current.setText("当前爬行: " + msg);
			goodLinksLabel.setText("正常链接数目: " + goodLinksCount);
			badLinksLabel.setText("死链: " + badLinksCount);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			badLinksTextArea.setText("");
			spider = new Spider(this);
			spider.clear();
			base = new URL(url.getText());
			spider.addURL(base);
			spider.begin();
			Runnable doLater = new Runnable() {
				public void run() {
					begin.setText("Begin");
				}
			};
			SwingUtilities.invokeLater(doLater);
			bgThread = null;

		} catch (MalformedURLException e) {
			UpdateErrors err = new UpdateErrors();
			err.msg = "Bad address.";
			SwingUtilities.invokeLater(err);

		}
	}

	/**
	 * 接口中的方法
	 */
	@Override
	public void spiderURLError(URL url) {
		//死链计数增加
		badLinksCount++;
		
	}
	/**
	 * 接口中的方法
	 */
	@Override
	public void spiderFoundEMail(String email) {
		//对email地址暂时不作操作
		
	}

}
