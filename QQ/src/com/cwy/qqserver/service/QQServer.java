package com.cwy.qqserver.service;

import com.cwy.qqcommon.Message;
import com.cwy.qqcommon.MessageType;
import com.cwy.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class QQServer {
    private ServerSocket ss =null;
    //创建一个集合，存放多个用户，如果是这些用户登入，则认为合法
    //HashMap无法处理线程安全的，ConcurrentHashMap处理的线程安全，即线程同步处理，可以处理并发的集合，没有线程安全
    private static ConcurrentHashMap<String,User> validUsers = new ConcurrentHashMap<>();

    static {
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "888888"));
        validUsers.put("300", new User("300", "932399"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
    }
    public static void OffSendMessage(String getterId){
        ConcurrentHashMap<String, ArrayList<Message>> hashMap =OffMessageHashMap.getUm();
        if(hashMap.containsKey(getterId)) {
            ArrayList<Message> messages = hashMap.get(getterId);
            for (Message message : messages) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getSeverConnectClientThread(getterId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            OffMessageHashMap.removeOffMessage(getterId);
        }
    }

public static ConcurrentHashMap<String,User> getConcurrentHashMap(){
        return validUsers;
}
    //验证用户是否有效的方法
    public boolean checkUser(String userId,String passwd){
        User user = validUsers.get(userId);
        if(user == null){
            return false;
        }
        if(!user.getPassword().equals(passwd)){
            return false;
        }
        return true;
    }

    public QQServer() throws ClassNotFoundException, IOException {
        try {
            new Thread(new SendNewsToAllService()).start();
            //端口可以写在配置文件中
            ss = new ServerSocket(9999);
            while (true){
                Socket socket = ss.accept();//如果没有客户端连接，就会阻塞
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();//读取客户端的User对象
                Message message = new Message();
                //验证
                //创建一个Message 对象,准备回复客户端
                if(checkUser(u.getUserId(), u.getPassword())){
                    message.setMessageType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    //创建一个线程，和客户端保持通信，线程需要持有socket对象
                    ServerConnectClientThread thread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动线程
                    thread.start();
                    //把线程放到一个集合中进行管理
                    ManageClientThreads.addClientThread(u.getUserId(), thread);
                    OffSendMessage(u.getUserId());
                 }else{//登入失败
                    System.out.println("用户 id=" + u.getUserId() + "pwd=" + u.getPassword());
                  message.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                  oos.writeObject(message);
                  socket.close();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //如果服务器退出了while循环，说明不在监听，关闭SeverSocket
            ss.close();
        }

    }
}
