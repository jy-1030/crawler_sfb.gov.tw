package TFNDN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tej.error.ErrorTitle;
import com.tej.frame.DaoFrame;
import com.tej.postgresql.connection.IDBConnector;


public class TxsfbnewsinfoDAO extends DaoFrame{
	private String tableName;
	private IDBConnector market;
	public TxsfbnewsinfoDAO(String tableName  , IDBConnector market){
		this.tableName = tableName;
	    this.market = market;
	}

	
	/**
	 * 非版本別 先修改後新增
	 * @param listMap
	 * @throws SQLException 
	 * @throws Exception
	 */
	public void modify(Object[] tempMap) throws Exception {
		
		if(tempMap.length == 0){
			logger.warn("本次資料0筆,不處理匯入");
			return ;
		}
		
		Txsfbnewsinfo[] listMap = (Txsfbnewsinfo[])tempMap;

		int counter = 0;
		PreparedStatement pstmt = null ;
		Connection conn = null;
		try{
			conn = this.market.getConnection();
			
			//設定連線
			conn.setAutoCommit(false);
			
			logger.info(this.getClass().getName()+" 本次資料共"+listMap.length+"筆");
			//建立表格
			logger.info("step1 更新資料(SQL):");
			
			String updateSQL = getUpadteSQL();
			pstmt = conn.prepareStatement(updateSQL);
			logger.debug("更新指令:"+updateSQL);
			
			int[] ok = new int[listMap.length] ;
			for(int j=0;j<listMap.length;j++){
				Txsfbnewsinfo obj = listMap[j];
				try{
					pstmt.clearParameters();
					
					pstmt.setString(1,obj.getNews_txt());
					pstmt.setString(2,obj.getIsdone());
					pstmt.setString(3,obj.getLink());

					pstmt.setDate(4,obj.getAnn_date());
					pstmt.setString(5,obj.getTopic());


					ok[j] = pstmt.executeUpdate();
					
					conn.commit();
					counter++;
				}catch(Exception e){
				    logger.error(ErrorTitle.UPDATE_TITLE.getTitle(obj.getPK()) , e);
					
					ok[j] = -1;
					conn.rollback();
				}
			}
			logger.info("更新完畢, 共"+counter+"筆執行更新指令");
			counter = 0;
			
			logger.info("step2 新增資料(SQL):");
			String insertSQL = this.getInsertSQL();
			pstmt = conn.prepareStatement(insertSQL);
			logger.debug("新增指令:"+insertSQL);
			
			for(int i = 0;i<listMap.length;i++){
				//已 update 過的不再新增
				if(ok[i]==1)continue ;
				Txsfbnewsinfo obj = listMap[i];
				try{
					pstmt.clearParameters();
					
					pstmt.setDate(1,obj.getAnn_date());
					pstmt.setString(2,obj.getTopic());
					pstmt.setString(3,obj.getNews_txt());
					pstmt.setString(4,obj.getIsdone());
					pstmt.setString(5,obj.getLink());

					
					pstmt.execute();
					conn.commit();
					counter++;
				}catch(Exception e){
					logger.error(ErrorTitle.INSERT_TITLE.getTitle(obj.getPK()) , e);
					
					ok[i] = -1;
					conn.rollback();
				}
			}
			pstmt.close();
			logger.info("新增完畢, 共"+counter+"筆新增");
			counter = 0;
			
		}catch (Exception e) {
			try { if (conn != null) conn.rollback();} catch(Exception ee) {logger.error(e);}
			throw e ;
		}finally{
            try { if (pstmt != null) pstmt.close();} catch(Exception e) {logger.error(e);}
            try { if (conn != null) conn.close();} catch(Exception e) {logger.error(e);}
		}
		
		//此function例外錯誤(log紀錄)由抽象類別(DaoFrame)統一回傳
	}

	/**
	 * 新增
	 * @return
	 * @throws Exception
	 */
	public String getInsertSQL(){
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" INSERT INTO "+tableName);
		sql.append("( ann_date,topic,news_txt,isdone,link)");
		sql.append("  values ( ? , ? , ? , ? , ? )");
				
		return sql.toString();
		
	}	
	/**
	 * 更新
	 * @return
	 * @throws Exception
	 */
	public String getUpadteSQL(){
		
		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE "+tableName);
		sql.append(" SET news_txt = ? ,isdone = ? ,link = ?  "); 
		sql.append(" WHERE (ann_date = ?  AND topic = ?  )"); 
				
		return sql.toString();
		
	}
}
