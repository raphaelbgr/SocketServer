package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sendable.Client;
import sendable.Message;

public class DAO {

	final private static String DATABASE_URL = "jdbc:mysql://surfael.sytes.net:3307/chatdb";
	
	static Connection c = null;
	
	public static synchronized void connect() throws SQLException {
		c = DriverManager.getConnection(DATABASE_URL, "chatclient", "nopass");
	}
	
	public synchronized static void updateSentMsgs(Message m) throws SQLException {
		String query = "UPDATE CLIENTS SET LASTMESSAGE=\""+ m.getText() + "\", LASTMESSAGEDATE='" + m.getMsg_DateCreatedSQL() + "', LASTMESSAGE_TIMESTAMP='" + m.getTimestamp() + "'"
					 + "WHERE LOGIN='"+ m.getOwnerLogin() + "';";	
		Statement s = c.prepareStatement(query);
		s.execute(query);
	}
	
	public synchronized static boolean verifyClientPassword(Client cl) throws SQLException {
		String query = "SELECT LOGIN, PASSWORD FROM CLIENTS WHERE LOGIN='"+cl.getLogin()+"'";
		Statement st = c.prepareStatement(query);
		ResultSet rs = st.executeQuery(query);
		rs.next();
		if (rs.getString("LOGIN").equalsIgnoreCase(cl.getLogin())) {
			if (rs.getString("PASSWORD").equals(cl.getPassword())) {
				return true;
			} else return false;
		} else return false;
	}
	
	public synchronized static Client loadClientData(Client cl) throws SQLException {
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
		return cl;
	}
	
	public static int getOwnerMessagesSent(String login) throws SQLException {
		String query = "SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG AS COUNT WHERE OWNERLOGIN='" + login + "'";
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(query);
		int count = 0;
		while(rs.next()) {
			count =  rs.getInt("COUNT(OWNERLOGIN)");
		}
		return count;
	}
	
	public static int generateOwnerID(String login) throws SQLException {
		String query = "SELECT DISTINCT COUNT(LOGIN) FROM CLIENTS AS COUNT";
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(query);
		int id = 0;
		while(rs.next()) {
			id =  rs.getInt("COUNT(LOGIN)");
		}
		return id + 1;
	}
	
	public static void storeMessage(Message m) throws SQLException {
		String query = "INSERT INTO MESSAGELOG (OWNERLOGIN,OWNERNAME,TEXT,CREATIONTIME,SERVERRECEIVEDTIME,MSG_DATE,IP,PCNAME,NETWORK,TYPE,SERVRESPONSE,OWNERID,"
				+ "`MESSAGESERVER#`,`MESSAGEOWNER#`, `SERV_REC_TIMESTAMP`, `SERV_REC_TIME`) "
				
				+ "VALUES ('" + m.getOwnerLogin() + "',"
				+ "'" + m.getOwnerName() + "',"
				+ "'" + m.getText() + "',"
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
//		System.out.println(query);
		s.execute(query);
	}
	
	public static synchronized void disconnect() throws SQLException {
		c.close();
	}
	
}
