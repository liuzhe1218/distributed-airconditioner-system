package serveraction;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import base.BaseAction;
import base.serverdao;
import base.writefile;
import serverdao.controlDao;
import serverdao.serverDao;
import serverdto.serverstatus;
/*
 * 将前台的开关动作转化到后台的类
 */
public class controlaction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private List list;  //某一数据表list
	private List filelist; //房间某天记录list
	private List serverlist;
	public List getServerlist(){
		return serverlist;
	}
	public void setServerlist(List serverlist){
		this.serverlist = serverlist;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getFilelist() {
		return filelist;
	}
	public void setFilelist(List filelist) {
		this.filelist = filelist;
	}
	public String getfile() throws Exception{//定期调出某房间的记录
		String output = "success";
		String roomID = this.getRequest().getParameter("roomID"); //前台选择的房间
		String startday = this.getRequest().getParameter("startday"); //前台选择的记录起始日期   xxxx-xx-xx 00:00:00 - xxxx-xx-xx 24:00:00
		String endday = this.getRequest().getParameter("endday");
		Timestamp starttime = null;
		Timestamp endtime = null;
		try{
			starttime = Timestamp.valueOf(startday);
			endtime = Timestamp.valueOf(endday);
		}catch (Exception e){
			e.printStackTrace();
		}
		serverdao test = new serverdao();
		filelist = test.loadrecord(roomID,starttime,endtime);
		if (list.size()==0){ //无相关记录
			PrintWriter writer = this.getResponse().getWriter();
			writer.print("failure");
			writer.close();
		}else{ //写入固定文件夹
			writefile file = new writefile(filelist, roomID);
			PrintWriter writer = this.getResponse().getWriter();
			writer.print("success");
			writer.close();
		}		
		return output;
	}
	public String control() throws Exception{// 开关的总控制方法	
		String output="success";
		String input = this.getRequest().getParameter("control"); //前台开关按钮返回的请求 off-on
		serverdao test = new serverdao();
		output=test.serverControl(input);//调用函数，把总开关请求写进数据库
		return output;
	}
	/*
	 * 请求页面显示数据库list的方法
	 */
	public String gettable() throws Exception{
		String output="";
		int temp=0;
		String input = this.getRequest().getParameter("type");//前台按钮返回想要查看的列表的请求
		if (input.equals("record"))
			temp = 1;
		else if(input.equals("usage"))
			temp = 2;
		else if(input.equals("serverstatus"))
			temp = 3;
		serverDao test = new serverDao();
		list=test.load(temp);
		PrintWriter writer = this.getResponse().getWriter();
		writer.print("success");
		writer.close();
		if (list.size()!=0)
			output="success";
		else
			output="failure";
		return output;
	}
	public String getServer() throws Exception{
		String output = "result";
		serverstatus[] status;
		serverdao server = new serverdao();
		status = server.getServers();
		for (int i=0;i<status.length;i++){
			serverlist.add(status[i]);
		}
		return output;
	}
}