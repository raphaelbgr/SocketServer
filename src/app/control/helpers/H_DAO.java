package app.control.helpers;

import net.sytes.surfael.api.model.messages.Message;

public class H_DAO {

	public static String prepareInsertMessageHistoryQuery(Message m) {

		return "INSERT INTO MESSAGELOG (OWNERLOGIN,OWNERNAME,TEXT,CREATIONTIME,SERVERRECEIVEDTIME,MSG_DATE,IP,PCNAME,NETWORK,TYPE,SERVRESPONSE,OWNERID,"
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
		+ "'" + m.getSenderId() + "',"
		+ "'" + 0 + "',"
		+ "'" + 0 + "',"
		+ "'" + m.getServerReceivedtimeString() + "',"
		+ "'" + m.getServerReceivedTimeLong()
		+ "')";
	}

	public static String prepareUpdateClientMessageCountQuery(Message m) {

		return "UPDATE CLIENTS SET MSGCOUNT="
		+ "(SELECT COUNT(OWNERLOGIN) FROM MESSAGELOG "
		+ "AS COUNT WHERE OWNERLOGIN='" + m.getOwnerLogin() + "') "
		+ "WHERE LOGIN='" + m.getOwnerLogin() + "'";
	}

}
