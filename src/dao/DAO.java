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
	
	public static synchronized void disconnect() throws SQLException {
		c.close();
	}
	
}
