package com.cwy.QQClient.service;

import com.cwy.QQClient.utiliy.Utility;
import com.cwy.qqcommon.Message;
import com.cwy.qqcommon.MessageType;
import com.cwy.qqcommon.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class UserClientService implements MessageType {

    //因为在其他地方用到user信息，因此做成成员属性
    private User u = new User();
    boolean b = false;
    //因为其他地方用到socket，所以创建一个socket属性
    private Socket socket;

    //根据userid and pwd到服务器验证用户是否合法
    public boolean checkUser(String userID, String pwd) {
        //创建user对象
        //连接服务器，发送u对象
        u.setUserId(userID);
        u.setPassword(pwd);

        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if(ms.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){//登入OK

               //创建一个和服务器保持通信的线程
                ClientConnectSeverThread ccst = new ClientConnectSeverThread(socket);
                //启动线程
                ManageClientConnectSeverThread.addClientConnectSeverThread(userID,ccst);
                ccst.start();
                b = true;
            }else {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  b;
    }
    public  void onlineFriendList()  {
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        try {
            ClientConnectSeverThread clientConnectSeverThread = ManageClientConnectSeverThread.getClientConnectSeverThread(u.getUserId());
            ObjectOutputStream oos = new ObjectOutputStream(clientConnectSeverThread.getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void logout(){
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定哪个客户端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectSeverThread.getClientConnectSeverThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + "退出系统");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void privateMessage(){
        String user = "";
        Message message = new Message();
        System.out.println(u.getUserId() + "请求私聊");
        System.out.println("请选择你要私聊的对象:");
        user = Utility.readString(50);
        System.out.println("你想说的话：");
        String content = Utility.readString(200);
        message.setMessageType(MessageType.MESSAGE_COMM_MES);
        message.setSender(u.getUserId());
        message.setGetter(user);
        message.setSendTime(new java.util.Date().toString());
        message.setContent(content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectSeverThread.getClientConnectSeverThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessageToAll(){
        Message message = new Message();
        System.out.println(u.getUserId() + "要对大家说话");
        System.out.println("你想对大家说的话：");
        String content = Utility.readString(200);
        message.setMessageType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSender(u.getUserId());
        message.setSendTime(new java.util.Date().toString());
        message.setContent(content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectSeverThread.getClientConnectSeverThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFileToOne(){
        System.out.println("请输入你想把文件发送给的用户(在线用户): ");
        String getterId = Utility.readString(50);
        System.out.println("请输入发送文件的路径：");
        String src = Utility.readString(100);
        System.out.println("请输入把文件发送到对应的路径：");
        String dest = Utility.readString(100);
        String senderId = u.getUserId();
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];
        message.setFileBytes(fileBytes);
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("\n" + senderId + "给" + getterId + "发送文件" + src);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectSeverThread.getClientConnectSeverThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
