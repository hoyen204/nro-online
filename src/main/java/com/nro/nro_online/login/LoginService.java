package com.nro.nro_online.login;

import java.io.IOException;
import java.util.List;

import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.utils.Log;

public class LoginService {

    private final LoginSession session;

    public LoginService(LoginSession session) {
        this.session = session;
    }

    public void login(byte server, int clientID, String username, String password) {
        try (Message ms = new Message(Cmd.LOGIN)) {
            ms.writer().writeByte(server);
            ms.writer().writeInt(clientID);
            ms.writer().writeUTF(username);
            ms.writer().writeUTF(password);
            sendMessage(ms);
        } catch (IOException e) {
            Log.error(LoginService.class, e);
        }
    }

    public void logout(int userID) {
        try (Message ms = new Message(Cmd.LOGOUT)) {
            ms.writer().writeInt(userID);
            sendMessage(ms);
        } catch (IOException e) {
            Log.error(LoginService.class, e);
        }
    }

    public void setServer(int serverID, Client client) {
        try (Message ms = new Message(Cmd.SERVER)) {
            List<Session> sessions = client.getSessions();
            synchronized (sessions) {
                List<Session> list = sessions.stream().filter(t -> t.loginSuccess).toList();
                ms.writer().writeInt(serverID);
                ms.writer().writeInt(list.size());
                for (Session ses : list) {
                    ms.writer().writeInt(ses.id);
                    ms.writer().writeInt(ses.userId);
                    ms.writer().writeUTF(ses.uu);
                    ms.writer().writeUTF(ses.pp);
                }
                sendMessage(ms);
            }
        } catch (IOException e) {
            Log.error(LoginService.class, e);
        }
    }

    public void sendMessage(Message ms) {
        session.sendMessage(ms);
    }
}