package base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import serverdto.record;

/*
 * 将记录对象写进文件中, 取roomID 以及月日年
 */
public class writefile {
	public writefile(List list, String roomID){
		String suffix1 = "E://work//record//room";
		String suffix2 = ".txt";
		String outputfile = suffix1+roomID+suffix2;
		String content = "";
		File file = new File(outputfile);
		Long minus;
		double timeminus;
		try {
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			record test;
			int i = 0;
			for (;i<list.size();i++){
				test = (record)list.get(i);
				minus = ((test.getStarttime().getTime()-test.getEndtime().getTime())/60);
				timeminus = Math.ceil(minus.doubleValue());
				content = "房间号: "+test.getRoomID()+" 温度方向: "+test.getTemperdirection()+" 从"+test.getStarttemper()+"到"+test.getEndtemper()+" 总时间长度:"+timeminus+
				" 总风量: "+test.getFlowvolume()+" 风强: "+test.getFlowpower();
				bw.write(content);
				bw.write("\r\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
