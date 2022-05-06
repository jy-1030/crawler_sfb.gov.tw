package TFNDN;

//import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.xml.DOMConfigurator;

import com.cake.net._useage.Parameter;
import com.tej.error.ErrorTitle;
import com.tej.frame.PorcessFrame;
import com.tej.frame.Table;
import com.tej.postgresql.DBAdminConnector;
import com.tej.postgresql.connection.ConnectorTableBuilder;
import com.tej.postgresql.connection.IDBConnector;


/**
 * 獨立開發程式
 * @author : 
 *
 */
public class TEJ_221_NDB_2TFNDN_01 extends PorcessFrame{
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public void taskDescription(Parameter param , IDBConnector market,String[] args){
		super.parameter = param;  //參數檔
		super.market    = market; //匯入連線資訊
		
	    String url       = parameter.getUrl();
	    String tableName = parameter.getTableName();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


	    DL_221_NDB_2TFNDN_01 dl = new DL_221_NDB_2TFNDN_01(parameter);

	    String endDate = ""; 
		String begDate = ""; 
	    
	    if (args.length == 0) {
			Calendar cal = Calendar.getInstance();// 系統時間
			endDate = String.valueOf(sdf.format(cal.getTime()));
//			cal.add(Calendar.DAY_OF_MONTH, -30); // 系統日-30
			begDate = String.valueOf(sdf.format(cal.getTime()));
		}
	    
	    else if(args.length == 2){
	    	if (args[0].matches("[0-9]{8}") || !args[1].matches("[0-9]{8}")) {
	   	     endDate = args[1]; // 當天
			 begDate = args[0]; // 起日
			 
				
				try{
					java.util.Date formate1 = null;
					java.util.Date formate2 = null;
					formate1 = new SimpleDateFormat("yyyyMMdd").parse(begDate);
					formate2 = new SimpleDateFormat("yyyyMMdd").parse(endDate);
					begDate= new SimpleDateFormat("yyyy-MM-dd").format(formate1);
					endDate= new SimpleDateFormat("yyyy-MM-dd").format(formate2);
	
				}
				catch(ParseException e){
					logger.error(e);
				}
			    }
	 
	    	} else {
				logger.error("日期輸入格式錯誤:輸入格式= yyyy-MM-d" + args[0] + ", " + args[1]);
			}


	    

//	    
//		String endDate = ""; // 當天
//		String begDate = ""; // 起日
//		
//		if (args.length == 0) {
//			Calendar cal = Calendar.getInstance();// 系統時間
//			endDate = String.valueOf(sdf.format(cal.getTime()));
////			cal.add(Calendar.DAY_OF_MONTH, -30); // 系統日-30
//			begDate = String.valueOf(sdf.format(cal.getTime()));
////		} else if (args.length == 1) {
////			if (args[0].matches("[0-9]{8}")) {
////				begDate = args[0];
////				endDate = args[0];
////			} else {
////				logger.error("日期輸入格式錯誤:" + args[0]);
////			}
//		} else if (args.length == 2) {
//			if (args[0].matches("[0-9]{8}") || !args[1].matches("[0-9]{8}")) {
//				
//				begDate = args[0];
//				endDate = args[1];
//			
//			} else {
//				logger.error("日期輸入格式錯誤:輸入格式= yyyy-MM-d" + args[0] + ", " + args[1]);
//			}
//		}
//		
//		
//		///////////////////////DATE PARA//////////////////////////////
	    
	    System.out.println("start:" + begDate + "    ，end:" + endDate);
	    
//		logger.info("起日:" + begDate + "    ，迄日:" + endDate);
		
	    //第一段 : 抓檔 與 網頁處理(若讀取外部檔匯入可以不需此段)
	    String source = "";
	    try{
	    	logger.info("網址 : "+url);
// 	    	source = dl.getData(url);
	    	
	    	
	    	

	    	for(int i = 1; i<=20; i++){
	    
	    		String page = Integer.toString(i);
//	    		System.out.println("url="+url.replace("[begDate]", begDate).replace("[endDate]", endDate).replace("[page]",page));
	    		source = dl.getData(url.replace("[begDate]", begDate).replace("[endDate]", endDate).replace("[page]",page));
	    	
//	    	} //for loop

	    	
//	    }catch(Exception e){
//	    	logger.error(ErrorTitle.CONNECT_TITLE.getTitle(),e);
//	    }
	    
	    //第二段 : 分析資料存入暫存處理
	    Table[] tableList = null;
	    try{
	    	tableList = dl.parseListMap(source);
	    }catch(Exception e){
	    	logger.error(ErrorTitle.PROCESS_TITLE.getTitle(),e);
	    }
//	}//for loop

	    
	    //資料處理暫存筆數為0 不需再執行匯入	    
		if (parameter.isDltestMode()) {   //偵側網改程式，參數test
			if (tableList != null && tableList[0].getTableBean().length == 0)
				logger.error(ErrorTitle.IMPORT_TITLE.getTitle("截取 0 筆資料"));
		} else if (tableList != null){
			//第三段 : 匯入資料庫處理
			TxsfbnewsinfoDAO dao = new TxsfbnewsinfoDAO(tableName,market);
			try{
				//依照分析資料存入暫存  處理的內容決定使用的表格
				dao.modify(tableList[0].getTableBean());
			}catch(Exception e){
				logger.error(ErrorTitle.IMPORT_TITLE.getTitle(),e);
			}
			
			
		}
	    	}//for loop
		
		    }catch(Exception e){
		    	logger.error(ErrorTitle.CONNECT_TITLE.getTitle(),e);
		    }    
	}
	public static void main(String[] args){
		
		DOMConfigurator.configure(".\\log4j.xml");

		//admin連線資訊
		IDBConnector admin =  DBAdminConnector.getInstance();
				
		//宣告RFP名稱字串
		String spt = System.getProperty("file.separator");
		String propertyPath = System.getProperty("user.dir") + spt + "property"+ spt + "TEJ_221_NDB_2TFNDN_01.property";
				
		//宣告參數檔物件(給予檔案路徑)
		Parameter param = new Parameter(propertyPath);
				
		//初始化物件
		param.initial();
				
		//欲匯入表格連線資訊
		ConnectorTableBuilder builder = 
				new ConnectorTableBuilder(admin,param.getDbName());

		try{
			IDBConnector market = builder.buildConnector();

			TEJ_221_NDB_2TFNDN_01 runPg = new TEJ_221_NDB_2TFNDN_01();
					
			runPg.taskDescription(param , market,args);

		}catch(Exception e){
		     logger.error(e);
		}
	}
	
	//date
	private java.util.Date checkDate(String d) throws ParseException {
		if (d.trim().equals(""))
			return null;
		else
			return new java.sql.Date(sdf.parse(d).getTime());

	}
	


	
}
