package com.cwy.QQClient.service;

import com.cwy.qqcommon.Message;
import com.cwy.qqcommon.MessageType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectSeverThread extends Thread{
    //该线程需要持有socket
    //为了更方便得到socket
    private Socket socket;
    public ClientConnectSeverThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true){
            System.out.println("不断接受消息");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message ms = (Message) ois.readObject();//如果没有发送信息，线程就会阻塞在这里
                if(ms.getMessageType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    //取出在线用户列表
                    String[] onlineUsers = ms.getContent().split(" ");
                    System.out.println("=======在线用户列表=======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户： " + onlineUsers[i]);
                    }
                }else if(ms.getMessageType().equals(MessageType.MESSAGE_COMM_MES)){
                    System.out.println(ms.getSender() + "对" + ms.getGetter() + "说：" + ms.getContent() + "    " + ms.getSendTime());
                } else if (ms.getMessageType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    System.out.println("\n" + ms.getSender() + "对大家说" + ms.getContent());
                } else if (ms.getMessageType().equals(MessageType.MESSAGE_FILE_MES)) {
                    System.out.println("\n" + ms.getSender() + "给" + ms.getGetter() + "发文件" + ms.getSrc() + "到我的电脑目录" + ms.getDest());

                    //取出message的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(ms.getDest(),true);
                    fileOutputStream.write(ms.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n保存文件成功");

                } else{
                    System.out.println("是其他类型的message,不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public Socket getSocket(){
        return socket;
    }
}
