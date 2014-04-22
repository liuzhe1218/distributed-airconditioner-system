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
 * ��ǰ̨�Ŀ��ض���ת������̨����
 */
public class controlaction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private List list;  //ĳһ���ݱ�list
	private List filelist; //����ĳ���¼list
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
	public String getfile() throws Exception{//���ڵ���ĳ����ļ�¼
		String output = "success";
		String roomID = this.getRequest().getParameter("roomID"); //ǰ̨ѡ��ķ���
		String startday = this.getRequest().getParameter("startday"); //ǰ̨ѡ��ļ�¼��ʼ����   xxxx-xx-xx 00:00:00 - xxxx-xx-xx 24:00:00
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
		if (list.size()==0){ //����ؼ�¼
			PrintWriter writer = this.getResponse().getWriter();
			writer.print("failure");
			writer.close();
		}else{ //д��̶��ļ���
			writefile file = new writefile(filelist, roomID);
			PrintWriter writer = this.getResponse().getWriter();
			writer.print("success");
			writer.close();
		}		
		return output;
	}
	public String control() throws Exception{// ���ص��ܿ��Ʒ���	
		String output="success";
		String input = this.getRequest().getParameter("control"); //ǰ̨���ذ�ť���ص����� off-on
		serverdao test = new serverdao();
		output=test.serverControl(input);//���ú��������ܿ�������д�����ݿ�
		return output;
	}
	/*
	 * ����ҳ����ʾ���ݿ�list�ķ���
	 */
	public String gettable() throws Exception{
		String output="";
		int temp=0;
		String input = this.getRequest().getParameter("type");//ǰ̨��ť������Ҫ�鿴���б������
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