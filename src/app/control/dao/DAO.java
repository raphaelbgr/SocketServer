package app.control.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import app.ServerMain;
import app.model.clients.Client;
import app.model.messages.Message;

public class DAO {

	final private static String DATABASE_URL = "jdbc:mysql://"+ ServerMain.DATABASE_ADDR + ":" + ServerMain.DATABASE_PORT + "/" + ServerMain.DATABASE_SCHEMA;
	final private static String DATABASE_USER = ServerMain.DATABASE_LOGIN;
	final private static String DATABASE_PASSWD = ServerMain.DATABASE_PASS;
	
	static Connection c = null;
	
	public static synchronized void connect() throws SQLException {
		if (ServerMain.DB) {
			c = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWD);
		}
	}
	
	public String codifyPassword(String pass) {
		if (ServerMain.DB) {
			return MD5.getMD5(pass);
		}
		return null;
	}
	
	public synchronized static void updateSentMsgs(Message m) throws SQLException {
		if (ServerMain.DB) {
			String query = "UPDATE CLIENTS SET LASTMESSAGE=\""+ m.getText() + "\", LASTMESSAGEDATE='" + m.getMsg_DateCreatedSQL() + "', LASTMESSAGE_TIMESTAMP='" + m.getTimestamp() + "'"
					 + "WHERE LOGIN='"+ m.getOwnerLogin() + "';";

		//DEBUG
		if (ServerMain.DEBUG) {
			System.out.println(query);
		}
		
		Statement s = c.prepareStatement(query);
		s.execute(query);
		}
	}
	
	public synchronized static boolean verifyClientPassword(Client cl) throws SQLException {
		if (ServerMain.DB) {
			String query = "SELECT LOGIN, cast(aes_decrypt(cryptpassword,'"+ServerMain.DATABASE_CRYPT_KEY+"') as CHAR) AS CRYPTPASSWORD FROM CLIENTS WHERE LOGIN='"+cl.getLogin()+"'";
			Statement st = c.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			rs.next();
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			
			if (rs.getString("LOGIN").equalsIgnoreCase(cl.getLogin())) {
				if (rs.getString("CRYPTPASSWORD").equals(cl.getPassword())) {
					return true;
				} else return false;
			} else return false;
		}
		return true;
	}
	
//	public synchronized static void mD5criptifyAllDatabasePasswords() throws SQLException {
//		if (ServerMain.DB) {
//			String query = "SELECT PASSWORD FROM CLIENTS";
//			String queryPassDelete = "DELETE FROM CLIENTS WHERE PASSWORD LIKE '%'";
//			System.out.println(query);
//			Statement st = c.prepareStatement(query);
//			ResultSet rs = st.executeQuery(query);
//			while (rs.next()) {
//				String queryUpdate = "UPDATE CLIENTS SET cryptPASSWORD='" + MD5.getMD5(rs.getString("PASSWORD")) + "' WHERE PASSWORD LIKE '%'";
//				Statement st2 = c.prepareStatement(queryUpdate);
//				st2.execute(queryUpdate);
//				System.out.println(queryUpdate);
//			}
//		}
//	}
	
	public synchronized static Client loadClientData(Client cl) throws SQLException {
		if (ServerMain.DB) {
			String query = "SELECT * FROM CLIENTS WHERE LOGIN='"+ cl.getLogin() +"'";
			Statement st = c.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			rs.next();
			cl.setName(rs.getString("NAME"));
			cl.setEmail(rs.getString("EMAIL"));
			cl.setMembertype(rs.getString("MEMBERTYPE"));
			cl.setId(Long.valueOf(rs.getString("ID")));
			cl.setLastMessage(new Message(rs.getString("LASTMESSAGE")));
			cl.setMsgCount(Long.valueOf(rs.getString("MSGCOUNT")));
			cl.setOnlinetime(Long.valueOf(rs.getString("ONLINETIME")));
			cl.setLastIp(rs.getString("LASTIP"));
			cl.setRegistrationDate(rs.getDate("REGISTRATIONDATE"));
			cl.setLastOnline(rs.getDate("LASTONLINE"));
			cl.setSex(rs.getString("SEX"));
			cl.setCollege(rs.getString("COLLEGE"));
			cl.setCourse(rs.getString("COURSE"));
			cl.setStartTrimester(rs.getString("COURSESTART"));
			cl.setInfnetMail(rs.getString("INFNETID"));
			cl.setWhatsapp(rs.getString("WHATSAPP"));
			cl.setFacebook(rs.getString("FACEBOOK"));
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			return cl;
		}
		
		Client c = new Client();
		c.setName("Programmer");
		c.setEmail("programmer@program.com");
		c.setMembertype("DEVELOPER");
		c.setId(111111L);
		c.setLastMessage(new Message("Last message."));
		c.setMsgCount(1L);
		c.setOnlinetime(10L);
		c.setLastIp("127.0.0.1");
//		c.setRegistrationDate(new Date("10-08-2015"));
//		c.setLastOnline(new Date("10-08-2015"));
		c.setSex("Male");
		c.setCollege("INFNET");
		c.setCourse("GEC");
		c.setStartTrimester("2013.2");
		c.setInfnetMail("raphaelb.rocha@al.infnet.edu.br");
		c.setWhatsapp("21988856697");
		c.setFacebook("fb.com/raphaelbgr");
		return c;
	}
	
	public synchronized static int getOwnerMessagesSent(String login) throws SQLException {
		if (ServerMain.DB) {
			String query = "SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG AS COUNT WHERE OWNERLOGIN='" + login + "'";
//			String queryClient = "UPDATE CLIENTS SET MSGCOUNT=(SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG AS COUNT WHERE OWNERLOGIN='" + login + "') WHERE LOGIN='" + login + "'";
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			int count = 0;
			while(rs.next()) {
				count =  rs.getInt("COUNT(OWNERLOGIN)");
			}
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			
			return count;
		}
		return 0;
	}
	
	public synchronized static int generateOwnerID(String login) throws SQLException {
		if (ServerMain.DB) {
			String query = "SELECT DISTINCT COUNT(LOGIN) FROM CLIENTS AS COUNT";
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			int id = 0;
			while(rs.next()) {
				id =  rs.getInt("COUNT(LOGIN)");
			}
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			
			return id + 1;
		}
		return 0;
	}
	
	public synchronized static void storeMessage(Message m) throws SQLException {
		if (ServerMain.DB) {
			String query = "INSERT INTO MESSAGELOG (OWNERLOGIN,OWNERNAME,TEXT,CREATIONTIME,SERVERRECEIVEDTIME,MSG_DATE,IP,PCNAME,NETWORK,TYPE,SERVRESPONSE,OWNERID,"
					+ "`MESSAGESERVER#`,`MESSAGEOWNER#`, `SERV_REC_TIMESTAMP`, `SERV_REC_TIME`) "
					
					+ "VALUES ('" + m.getOwnerLogin() + "',"
					+ "'" + m.getOwnerName() + "',"
					+ "\"" + m.getText() + "\","
					+ "'" + m.getCreationtime() + "',"
					+ "'" + m.getServerReceivedTimeSQLDate() + "',"
					+ "'" + m.getMsg_DateCreatedSQL() + "',"
					+ "'" + m.getIp() + "',"
					+ "'" + m.getPcname() + "',"
					+ "'" + m.getNetwork() + "',"
					+ "'" + m.getType() + "',"
					+ "'" + m.getServresponse() + "',"
					+ "'" + m.getOwnerID() + "',"
					+ "'" + m.getMessageServerCount() + "',"
					+ "'" + getOwnerMessagesSent(m.getOwnerLogin()) + "',"
					+ "'" + m.getServerReceivedtimeString() + "',"
					+ "'" + m.getServerReceivedTimeLong() + "')";
			Statement s = c.prepareStatement(query);
			s.execute(query);
			
			String updateClient = "UPDATE CLIENTS SET MSGCOUNT=(SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG AS COUNT WHERE OWNERLOGIN='" + m.getOwnerLogin() + "') WHERE LOGIN='" + m.getOwnerLogin() + "'";
			Statement s2 = c.prepareStatement(updateClient);
			s2.execute(updateClient);
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
				System.out.println(updateClient);
			}
		}
	}
	
	public static synchronized void disconnect() throws SQLException {
		if (ServerMain.DB) {
			if (c != null) {
				c.close();
			}
		}
	}

	public static synchronized String getClientNameByLogin(String login) throws SQLException {
		if (ServerMain.DB) {
			DAO.connect();
			String query = "SELECT NAME FROM CLIENTS WHERE LOGIN='"+ login +"'";
			Statement st = c.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			rs.next();
			String result = rs.getString("NAME");
			DAO.disconnect();
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			
			return result;
		}
		return null;
	}
	
}
