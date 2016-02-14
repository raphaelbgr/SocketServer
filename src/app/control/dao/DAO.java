package app.control.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import net.sytes.surfael.api.control.classes.MD5;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.clients.NewClient;
import net.sytes.surfael.api.model.exceptions.LocalException;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.History;
import net.sytes.surfael.api.model.messages.Message;
import app.ServerMain;

public class DAO {

	final private static String DATABASE_URL = "jdbc:mysql://"+ ServerMain.DATABASE_ADDR + ":" + ServerMain.DATABASE_PORT + "/" + ServerMain.DATABASE_SCHEMA;
	final private static String DATABASE_USER = ServerMain.DATABASE_LOGIN;
	final private static String DATABASE_PASSWD = ServerMain.DATABASE_PASS;

	static Connection c = null;
	private static List<Message> messagesToStore = Collections.synchronizedList(new ArrayList<Message>());
	private static boolean storeAgentRunning;
	private static ArrayList<Message> array;

	public static synchronized void connect() throws SQLException {
		if (ServerMain.DB) {
			c = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWD);
		}
	}
	
	public static void registerUser(Client nc) throws SQLException {
		DAO.connect();
		
		if (nc.getMD5Password() == null && nc.getFbToken() == null && nc.getPassword() == null) {
			nc.setMD5Password(MD5.getMD5("P@ssw0rd"));
		} else if (nc.getPassword() != null) {
			nc.setMD5Password(MD5.getMD5(nc.getPassword()));
		} else if (nc.getFbToken() != null) {
			nc.setMD5Password(MD5.getMD5("P@ssw0rd"));
		} else {
			nc.setMD5Password(nc.getMD5Password());
		}
		nc.setPassword("");
		
		java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		
		nc.setRegistrationDate(date);
		
		String query = "INSERT INTO CLIENTS ("
				+ "LOGIN,"
				+ "NAME,"
				+ "EMAIL,"
				+ "CRYPTPASSWORD,"
				+ "MEMBERTYPE,"
				+ "REGISTRATIONDATE,"
				+ "SEX,"
				+ "COLLEGE,"
				+ "`COURSE`,"
				+ "`COURSESTART`,"
				+ "`INFNETID`,"
				+ "`WHATSAPP`,"
				+ "`COUNTRY`,"
				+ "`STATE`,"
				+ "`CITY`,"
				+ "`FACEBOOK_TOKEN`,"
				+ "`PHOTO_URL`,"
				+ "`FACEBOOK`) "
				
				+ "VALUES ("
				+ "'" + nc.getLogin() + "',"
				+ "'" + nc.getName() + "',"
				+ "'" + nc.getEmail() + "',"
				+ "'" + nc.getMD5Password() + "',"
				+ "'" + nc.getMembertype() + "',"
				+ "'" + nc.getRegistrationDate() + "',"
				+ "'" + nc.getSex() + "',"
				+ "'" + nc.getCollege() + "',"
				+ "'" + nc.getCourse() + "',"
				+ "'" + nc.getStartTrimester() + "',"
				+ "'" + nc.getInfnetMail() + "',"
				+ "'" + nc.getWhatsapp() + "',"
				+ "'" + nc.getCountry() + "',"
				+ "'" + nc.getState() + "',"
				+ "'" + nc.getCity() + "',"
				+ "'" + nc.getFbToken() + "',"
				+ "'" + nc.getPhotoUrl() + "',"
				+ "'" + nc.getFacebook() + "'" 
				+ ");";
		
		Statement s = c.prepareStatement(query);
		
		s.execute(query);
		DAO.disconnect();
	}
	
	public synchronized static void updateSentMsgs(Message m) throws SQLException {

		DAO.connect();
		if (ServerMain.DB) {
			String query = "UPDATE CLIENTS SET LASTMESSAGE=\""+ m.getText()
					+ "\", LASTMESSAGEDATE='" + m.getMsg_DateCreatedSQL()
					+ "', LASTMESSAGE_TIMESTAMP='" + m.getTimestamp()
					+ "'"
					+ "WHERE LOGIN='"+ m.getOwnerLogin()
					+ "';";

			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			Statement s = c.prepareStatement(query);
			s.execute(query);
			DAO.disconnect();
		}

	}

	public synchronized static boolean verifyClientPassword(Client cl) throws SQLException {
		DAO.connect();
		ResultSet rs = null;
		if (ServerMain.DB) {
			String query = "SELECT LOGIN, CRYPTPASSWORD FROM CLIENTS WHERE LOGIN='"+cl.getLogin()+"'";
			String column = "LOGIN";
			if (cl.getLogin() == null) {
				query = "SELECT EMAIL, CRYPTPASSWORD FROM EMAIL WHERE EMAIL='"+cl.getEmail()+"'";
				column = "EMAIL";
			}

			Statement st = c.prepareStatement(query);
			rs = st.executeQuery(query);	

			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
			
			if (!rs.next()) {
				return false;
			} 

			if (cl.getFbToken() == null) {
				String hashedPassword;
				if (cl.getPassword() != null) {
					hashedPassword = MD5.getMD5(cl.getPassword());
				} else {
					hashedPassword = cl.getMD5Password();
				}

				if (rs.getString(column).equalsIgnoreCase(cl.getLogin()) || rs.getString(column).equalsIgnoreCase(cl.getEmail())) {
					if (rs.getString("CRYPTPASSWORD").equals(hashedPassword)) {
						DAO.disconnect();
						return true;
					} else {
						DAO.disconnect();
						return false;
					}
				} else {
					DAO.disconnect();
					return false;
				}
			}
		}
		DAO.disconnect();
		return true;
	}

	public synchronized static Client loadFakeDebugAccount() throws LocalException {
		if (!ServerMain.DB) {
			Client c = new Client();
			c.setName("Programmer");
			c.setEmail("programmer@program.com");
			c.setMembertype("DEVELOPER");
			c.setId(100000);
			c.setLastMessage(new Message("Last message."));
			c.setMsgCount(1);
			c.setOnlinetime(10);
			c.setLastIp("127.0.0.1");
			c.setRegistrationDate(Calendar.getInstance().getTime());
			c.setLastOnline(Calendar.getInstance().getTime());
			c.setSex("Male");
			c.setCollege("INFNET");
			c.setCourse("GEC");
			c.setStartTrimester("2013.2");
			c.setInfnetMail("raphaelb.rocha@al.infnet.edu.br");
			c.setWhatsapp("21988856697");
			c.setFacebook("fb.com/raphaelbgr");
			c.setConnect(true);
			return c;
		}
		else throw new LocalException("Server db is set to -on, cannot generate fake data.");
	}

	public synchronized static Client loadClientData(Client cl) throws ServerException {
		if (ServerMain.DB) {
			try {
				DAO.connect();
				String query = "SELECT * FROM CLIENTS WHERE LOGIN='"+ cl.getLogin() +"'";

				if (cl.getLogin() == null) {
					query = "SELECT * FROM CLIENTS WHERE EMAIL='"+ cl.getEmail() +"'";
				}

				Statement st = c.prepareStatement(query);
				ResultSet rs = st.executeQuery(query);
				rs.next();

				if (cl.getName() == null) {
					cl.setName(rs.getString("NAME"));
				}
				if (cl.getEmail() == null) {
					cl.setEmail(rs.getString("EMAIL"));
				}
				if (cl.getSex() == null) {
					cl.setSex(rs.getString("SEX"));
				}
				if (cl.getFbToken() == null) {
					cl.setFbToken(rs.getString("FACEBOOK_TOKEN"));
				}
				if (cl.getPhotoUrl() == null) {
					cl.setFbToken(rs.getString("PHOTO_URL"));
				}

				cl.setMembertype(rs.getString("MEMBERTYPE"));
				cl.setId(Integer.valueOf(rs.getString("ID")));
				cl.setLastMessage(new Message(rs.getString("LASTMESSAGE")));
				cl.setMsgCount(Integer.valueOf(rs.getString("MSGCOUNT")));
				cl.setOnlinetime(Integer.valueOf(rs.getString("ONLINETIME")));
				cl.setLastIp(rs.getString("LASTIP"));
				cl.setRegistrationDate(rs.getDate("REGISTRATIONDATE"));
				cl.setLastOnline(rs.getDate("LASTONLINE"));
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
				DAO.disconnect();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ServerException("Could not connect to the Database Server at this time.", true);
			}

			return cl;
		} else throw new ServerException("DB option is set to -off, server is not authorized to perform a conenction to the Datababse Server.");
	}
	
	public static int getOwnerMessagesSentAsync(String login) throws ServerException {
		int count = 0;
		if (ServerMain.DB) {
			String query = "SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG AS COUNT WHERE OWNERLOGIN='" + login + "'";
			Statement st;
			try {
				st = c.createStatement();
				ResultSet rs = st.executeQuery(query);
				while(rs.next()) {
					count =  rs.getInt("COUNT(OWNERLOGIN)");
				}
				while(rs.next()) {
					count =  rs.getInt("COUNT(OWNERLOGIN)");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
		} else throw new ServerException("DB option is set to -off, server is not authorized to perform a conenction to the Datababse Server.");
		return count;
	}

	public static synchronized void disconnect() {
		if (ServerMain.DB && c != null) {
			try {
				if (!c.isClosed()) {
                    c.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static History getHistory(int rowLimit) throws SQLException {
		connect();
		History data = new History();
		String query = null;
		if (rowLimit == 0) {
			query = "SELECT `MESSAGESERVER#`, SERV_REC_TIMESTAMP, OWNERNAME, TEXT FROM MESSAGELOG";
		} else {
			query = "SELECT `MESSAGESERVER#`, SERV_REC_TIMESTAMP, OWNERNAME, TEXT FROM MESSAGELOG LIMIT " + rowLimit;
		}
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(query);
		
		data.getHeaders().add("ID");
		data.getHeaders().add("Timestamp");
		data.getHeaders().add("Owner");
		data.getHeaders().add("Message");
		
		while(rs.next()) {
			data.getColumn1().add(String.valueOf(rs.getInt(1)));
			data.getColumn2().add(rs.getString(2));
			data.getColumn3().add(rs.getString(3));
			data.getColumn4().add(rs.getString(4));
		}
		
		disconnect();
		return data;
	}
	
	public static String getLoginByEmail(String email) throws SQLException {
		String result = null;
		if (ServerMain.DB) {
			DAO.connect();
			String query = "SELECT LOGIN FROM CLIENTS WHERE EMAIL='"+ email +"'";
			Statement st = c.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			rs.next();
			result = rs.getString("LOGIN");
			DAO.disconnect();

			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
		}
		return result;
	}
	
	public static boolean doLogin(Client client) throws SQLException, ServerException {
		boolean result;
		
		String login = client.getLogin();
		String password = client.getPassword();
		String MD5Password = client.getMD5Password();
		String email = client.getEmail();
		
		if (ServerMain.DB) {
			if (login == null || login.contentEquals("")) {
				if (email == null) {
					throw new ServerException(ServerMain.getTimestamp() + " SERVER> Please input a login/email.");
				}
				if (email != null && (email.contains("@") && email.contains("."))) {
					login = getLoginByEmail(email);
				} else throw new ServerException(ServerMain.getTimestamp() + " SERVER> Please input a VALID email.");
			}

			if (client.getFbToken() == null) {
				if ((MD5Password == null || MD5Password.equalsIgnoreCase(""))) {
					if (password == null) {
						throw new ServerException(ServerMain.getTimestamp() + " SERVER> Please input a password.");
					}
					MD5Password = MD5.getMD5(password);
				}
			} else {
				MD5Password = MD5.getMD5("P@ssw0rd");
			}

			DAO.connect();
			String query = "SELECT LOGIN FROM CLIENTS WHERE LOGIN='"+ login +"'"
					+ "AND CRYPTPASSWORD='"+ MD5Password + "';";
			Statement st = c.prepareStatement(query);
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				rs.getString("LOGIN").equalsIgnoreCase(login);
				result = true;
			} else {
				result = false;
			}
			DAO.disconnect();

			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}
		} else throw new ServerException(ServerMain.getTimestamp() + " SERVER> The option -db off is set, unable to access de database.");
		return result;
	}

	public synchronized static void aSyncStoreMessage(final Message m) {
		messagesToStore.add(m);

		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				while (storeAgentRunning) {
					try {
						Thread.sleep(10000);
						List<Message> messages = new ArrayList<>(messagesToStore);
						messagesToStore.clear();

						DAO dao = new DAO();
						dao.aSyncDataBaseWrite(messages);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		if (storeAgentRunning == false) {
			storeAgentRunning = true;
			t1.start();
		}
	}

	public synchronized void aSyncDataBaseWrite(final List<Message> messages) {

		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (Message m : messages) {
						DAO.connect();
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
									+ "'" + 0 + "',"
									+ "'" + 0 + "',"
									+ "'" + m.getServerReceivedtimeString() + "',"
									+ "'" + m.getServerReceivedTimeLong() + "')";
							Statement s = c.prepareStatement(query);
							s.execute(query);

							String updateClient = "UPDATE CLIENTS SET MSGCOUNT="
									+ "(SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG "
									+ "AS COUNT WHERE OWNERLOGIN='" + m.getOwnerLogin() + "') "
									+ "WHERE LOGIN='" + m.getOwnerLogin() + "'";

							Statement s2 = c.prepareStatement(updateClient);
							s2.execute(updateClient);

							//DEBUG
							if (ServerMain.DEBUG) {
								System.out.println(query);
								System.out.println(updateClient);
							}
							updateSentMsgs(m);
							DAO.disconnect();
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					DAO.disconnect();
				}
			}
		});
		t1.start();
		storeAgentRunning = true;
	}

	public static DAO getInstance() {
		if (instance == null) {
			instance =  new DAO();
		} return instance;
	}

	private static DAO instance;

	public static boolean clientExists(Client cl) throws SQLException {
		DAO.connect();
		ResultSet rs = null;
		String credential = null;
		String query = null;

		if (ServerMain.DB) {
			String MD5Password = MD5.getMD5("P@ssw0rd");

			if (cl.getFbToken() == null) {
				MD5Password = cl.getMD5Password();
			}
			if (cl.getLogin() == null) {
				credential = cl.getEmail();
				query = "SELECT EMAIL FROM CLIENTS WHERE EMAIL='" + credential + "' AND CRYPTPASSWORD='" + MD5Password + "' LIMIT 1;";
			} else {
				credential = cl.getLogin();
				query = "SELECT LOGIN FROM CLIENTS WHERE LOGIN='" + credential + "' AND CRYPTPASSWORD='" + MD5Password + "' LIMIT 1;";
			}
			System.out.println(query);
			//DEBUG
			if (ServerMain.DEBUG) {
				System.out.println(query);
			}

			Statement st = c.prepareStatement(query);
			rs = st.executeQuery(query);
		}

		if (rs.next()) {
			DAO.disconnect();
			return true;
		} else {
			DAO.disconnect();
			return false;
		}
	}
}
