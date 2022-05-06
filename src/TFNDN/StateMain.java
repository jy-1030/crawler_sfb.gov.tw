package TFNDN;

import org.apache.log4j.Logger;

import com.tej.main.JobMain;

/** ***************************************************************************
-* 1.作者:
-* 2.日期:
-* 3.程式說明:
-* 4.工作人時:
-* 5.任務碼:
-* 6.開單者:
-* 7.程式分類:
-* ***************************************************************************
-* 1.修改者:
-* 2.日期:
-* 3.修改說明:
-* 4.工作人時:
-* 5.任務碼:
-* 6.開單者:
***************************************************************************
*/
public class StateMain {
	private static Logger logger = Logger.getLogger("IMPORT");
	public static void main(String[] args){
		try{
			//上線程式
			JobMain job = new JobMain(args);

			job.processJob("TEJ_221_NDB_2TFNDN_01");
		}catch(Exception e){
			logger.error("主程式運行例外", e);
		}
	}
}
