package com.cwy.qqserver.service;

import com.cwy.qqcommon.Message;
import com.cwy.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//该类一个对象，和某个客户端保持通信
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;
    private boolean isAlive = true;

    public Socket getSocket() {
        return socket;
    }

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        while (isAlive) {
            try {
                System.out.println("服务端和客户端" + userId + "保持通信，读取数据");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                QQServer.OffSendMessage(message.getGetter());
                if (message.getMessageType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    System.out.println(message.getSender() + "要在线用户列表");
                    //客户端要在线用户列表
                    Message message2 = new Message();
                    message2.setContent(ManageClientThreads.getOnlineUser());
                    message2.setMessageType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setGetter(message.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                } else if (message.getMessageType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + "退出系统");
                    //将客户端对应的线程删除
                    ManageClientThreads.removeSeverConnectClientThread(message.getSender());
                    socket.close();
                    isAlive = false;
                } else if (message.getMessageType().equals(MessageType.MESSAGE_COMM_MES)) {
                    if (ManageClientThreads.checkOnlineUser(message.getGetter())){
                        ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getSeverConnectClientThread(message.getGetter()).getSocket().getOutputStream());
                        oos.writeObject(message);
                    }else if (QQServer.getConcurrentHashMap().containsKey(message.getGetter())) {
                        OffMessageHashMap.addOffMessageHashMap(message.getGetter(),message);
                    } else {
                        System.out.println("查无此用户");
                    }
                } else if (message.getMessageType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //需要遍历管理线程的集合，把message进行转发
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        //取出在线用户id
                        String onlineUserId = iterator.next().toString();
                        if (!onlineUserId.equals(message.getSender())) {//排除群发给自己
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                       }
                    }
                else if (message.getMessageType().equals(MessageType.MESSAGE_FILE_MES)) {
                ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getSeverConnectClientThread(message.getGetter());
                ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                oos.writeObject(message);
            } else {
                System.out.println("处理其他信息");
            }

        } catch(Exception e){
            e.printStackTrace();
        }
       }
    }

}



