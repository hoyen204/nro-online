package com.nro.nro_online.login;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
                List<Session> list = sessions.stream().filter(t -> t.loginSuccess).collect(Collectors.toList());
                ms.writer().writeInt(serverID);
                ms.writer().writeInt(list.size());
                for (Session session : list) {
                    ms.writer().writeInt(session.id);
                    ms.writer().writeInt(session.userId);
                    ms.writer().writeUTF(session.uu);
                    ms.writer().writeUTF(session.pp);
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