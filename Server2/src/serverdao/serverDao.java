package serverdao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
//���������õײ�bean��
import serverdto.record;
import serverdto.serverstatus;
import serverdto.usagetimes;

public class serverDao {
	public static SessionFactory sessionFactory;
	static{
		try{
			Configuration config = new Configuration().configure();
			sessionFactory = config.buildSessionFactory();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	/*
	 * load:  �����࣬�����������
	 * ���Գɹ�
	 */
	public List load(int i) throws Exception{  
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List list = new ArrayList();
		String sql1 = "from record c";
		String sql2 = "from usage c";
		String sql3 = "from serverstatus c";
		try{
			tx = session.beginTransaction();
			switch (i){
			case 1:
				list = session.createQuery(sql1).list();
				tx.commit();
				break;
			case 2:
				list = session.createQuery(sql2).list();
				//tx.commit();
				break;
			case 3:
				list = session.createQuery(sql3).list();
				tx.commit();
				break;
			default:
				System.out.println("the request for displaying data is wrong");
				break;
			}
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
		return list;
	}
	/*
	 * saveϵ�з���: ���Ƕ��ڴ�client�����������ֶ���д�����ݿ�
	 */
	/*public void saveRecord(record test) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(test);
			tx.commit();
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
	}*/
	public void saveServer(serverstatus test) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(test);
			tx.commit();
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
	}
	public void saveUsage(usagetimes test) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			session.save(test);
			tx.commit();
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
	}
	/*
	 * ȡ�����й���ĳ������������¼, δ����Ҫ����ʱ��ӿ�
	 */
	/*public List getrelatedrecord(String roomID) throws Exception{
		List list = new ArrayList();
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Query query;
		String selectSql = "from record t where t.roomID=?";
		try{
			tx = session.beginTransaction();
			query = session.createQuery(selectSql);
			query.setString(0,roomID);
			list = query.list();
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
    	return list;
	}*/
	/*public static void main(String[] args){
		String roomID = "509";
		serverDao test = new serverDao();
		List list = new ArrayList();
		boolean flag;
		try {
			flag = test.updateusage(roomID);
			System.out.println(flag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
