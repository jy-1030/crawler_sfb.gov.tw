package TFNDN;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.math.BigDecimal;


public class Txsfbnewsinfo {
	
	private Date ann_date;
	private String topic;
	private String news_txt;
	private String isdone;
	private String link;
	public Date getAnn_date(){
		return ann_date;
	}
	public void setAnn_date(Date Ann_date ){
		this.ann_date = Ann_date;
	}
	public String getTopic(){
		return topic;
	}
	public void setTopic(String Topic ){
		this.topic = Topic;
	}
	public String getNews_txt(){
		return news_txt;
	}
	public void setNews_txt(String News_txt ){
		this.news_txt = News_txt;
	}
	public String getIsdone(){
		return isdone;
	}
	public void setIsdone(String Isdone ){
		this.isdone = Isdone;
	}
	public String getLink(){
		return link;
	}
	public void setLink(String Link ){
		this.link = Link;
	}
	public String getPK(){
		return  ann_date+","+topic;
	}
	
	@Override
	public String toString() {
		return "Txsfbnewsinfo [ann_date=" + ann_date + ", topic=" + topic + ",news_txt=" + news_txt+ ",link="
				+ link 	+ "]";
	}


}
