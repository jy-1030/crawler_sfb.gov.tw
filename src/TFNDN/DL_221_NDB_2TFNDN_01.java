package TFNDN;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.ini4j.spi.BeanAccess;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;

import com.cake.net._useage.Parameter;
import com.cake.net.http.connection.URLProxy;
import com.cake.net.http.html.HTML;
import com.cake.net.http.html.HTMLX;
import com.cake.net.http.html.htmlparser.Extractor_Property;
import com.cake.net.http.html.htmlparser.Extractor_Result;
import com.cake.net.http.html.htmlparser.HtmlParser;
import com.tej.error.ErrorTitle;
import com.tej.frame.DownloadFrame;
import com.tej.frame.Table;
import com.tej.setting.IP;

public class DL_221_NDB_2TFNDN_01 extends DownloadFrame {
	private Parameter parameter;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public DL_221_NDB_2TFNDN_01(Parameter parameter){
		this.parameter = parameter;	
	}

	/**
	 * 連結抓取資料處理
	 * 網頁連線 or 規則複雜的資料截取
	 */
	@Override
	public String getData(String url) throws Exception {
//	public String getData(String url, String sdate, String edate) throws Exception {
		String host = "";
		String port = "";
		String source = "";
		
		URLProxy newIP = null;
		HTML htmlx = null;
		List<String> prox = IP.getIPFromTable();
		for (int i = prox.size(); i >= 0; i--) {
			if (i < prox.size()) {
				String proxy = prox.get(i);
				host = proxy.substring(0, proxy.indexOf(':'));
				port = proxy.substring(proxy.indexOf(':') + 1, proxy.length());
				logger.info("使用IP => " + host + ":" + port + "抓取資料..." + (prox.size() - i) + "/" + prox.size());
				newIP = new URLProxy(host, port);
			}
			try {
				htmlx = newIP == null ? new HTMLX(url, false) : new HTMLX(url, newIP, false);
				htmlx.setIgnoreSSLCertificate(true);
				htmlx.setConnectTimeout(parameter.getConnectTimeout(), parameter.getRetry());
				// 設定網頁連結多久中斷 ,為避免連線過久卡在網頁
				htmlx.setReadTimeout(parameter.getReadTimeout(), parameter.getRetry());
				htmlx.connect();
				htmlx.extractInputStream(parameter.getEncoding());
				source = htmlx.getCodeStringtype();

//				 Document doc = Jsoup.parse(source);
				// Document table = Jsoup.parseBodyFragment(doc.getElementsByClass("blueHeader").toString());
				// String source = table.toString();

				
//				List<NameValuePair> params = new ArrayList<NameValuePair>();
//				sdate = sdate.substring(0,4)+"-"+sdate.substring(4,6)+"-"+sdate.substring(6,8);
//				edate = edate.substring(0,4)+"-"+edate.substring(4,6)+"-"+edate.substring(6,8);
//				params.add(new BasicNameValuePair("sdate", sdate));
//				params.add(new BasicNameValuePair("edate", edate));
//				//logger.info("設定POST完畢");
			 
				// 回傳值應為html格式 ,能將資料區塊先截出來最好
				// 轉換IP重試連線等行為在此運行
				// 模擬瀏覽器 模擬行為也在此運行
				// 此function例外錯誤由呼叫class接收
				return htmlx.getCodeStringtype();
			} catch (Exception e) {
				logger.error(e);
			}
			Thread.sleep(5000);
		}
		logger.error("用光所有proxy 還是被擋");
		return "";
	}
	/**
	 * 分析資料 存入暫存
	 * 網頁資料剖析  , 外部檔欄位
	 */
	@Override
	public Table[] parseListMap(String source) {
		
		
		//表格陣列
		List<Table> tableList= new ArrayList<>();
		
		//宣告表格資料暫存陣列
		List<Txsfbnewsinfo> listMap= new ArrayList<>();
		
		//此處應只有網頁核心資料已截取過區塊
		//外部檔案讀取可直接由此處讀入並寫入暫存

		try{
			
		    //網頁標籤轉換資料分隔格式
			Extractor_Property extract = new Extractor_Property();
			extract.setEncoding(parameter.getEncoding());
//			extract.setAccountLineFeed(false);
//			extract.setSeparatorAtHead(false);
			HtmlParser parser = new HtmlParser(source , extract);
			Extractor_Result extractResult = parser.getExtractResult();

			
			
			
			//1st layer
			Document doc = Jsoup.parse(source);
//			Elements links = doc.select("a[href]");
			Elements links = doc.getElementsByClass("ptitle1");

			String url = "";
			String ann_date = "";
			String topic = "";
			String news_txt = "";
			String Isdone = "";
			
			
			for (Element link : links) {
				if (link.text().contains("金融監督管理委員會證券期貨局每日新聞")) {
					String value = link.select("a").attr("href");
					url = "https://www.sfb.gov.tw/ch/" + value;
//					System.out.println("u="+url);
				
				
				//second layer
				Document doc2 = Jsoup.connect(url).get();
				Elements ListDiv = doc2.getElementsByAttributeValue("id", "maincontent");
				if(!ListDiv.select("div> h3").contains("相關附件")){
					topic = ListDiv.select("div> h3").first().text(); //TOPIC
				}
				ann_date = ListDiv.select("div.contentdate").text(); //ann_date
				news_txt = ListDiv.select("div.main-a_03").text();
				
//				System.out.println("t="+topic);
				
				
//				}//if
				

				
					//資料回圈中每筆資料處理都要try..catch避免剖析段中斷
					try{
							
						Txsfbnewsinfo bean = new Txsfbnewsinfo();
						bean.setAnn_date(checkDate(ann_date));
						bean.setTopic(topic);
						bean.setNews_txt(news_txt);
						bean.setLink(url);
						bean.setIsdone(Isdone);
						

						//分析資料寫入暫存部分
						System.out.println(bean.toString());
						listMap.add(bean);
					}catch(Exception e){
						logger.error(ErrorTitle.CONTENT_TITLE.getTitle(),e);
					}
				}//if
			}
			
		}catch(Exception e){
			//網頁轉換 或 其餘例外錯誤
			logger.error(ErrorTitle.ANALYSIS_TITLE.getTitle(),e);
		}finally{
			tableList.add(new Table(listMap.toArray(new Txsfbnewsinfo[0]) , "表格註解"));
		}
		
		//此function例外錯誤由抽象類別(DownloadFrame)統一回傳
		
		return tableList.toArray(new Table[0]);
		
	}
	/**
	 * 檢查日期格式
	 * 
	 * @param d
	 * @return
	 * @throws ParseException
	 */
	private java.sql.Date checkDate(String d) throws ParseException {
		if (d.trim().equals(""))
			return null;
		else
			return new java.sql.Date(sdf.parse(d).getTime());

	}
	/**
	 * BigDecimal 格式檢查
	 * 
	 * @param d
	 * @return
	 */
	private BigDecimal checkBigDecimal(String d) {

		d = d.replace(",", "").trim().replaceAll("[A-Za-z]", "");
		if (d.replace("-", "").trim().equals(""))
			return null;
		else
			return new BigDecimal(d);
	}
//	@Override
//	public String getData(String url) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
