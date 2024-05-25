package com.cwy.qqserver.service;

import com.cwy.qqcommon.Message;
import com.cwy.qqcommon.MessageType;
import com.cwy.qqcommon.User;
import com.cwy.utiliy.Utility;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SendNewsToAllService implements Runnable{
    @Override
    public void run() {
        while (true) {
            System.out.println("请输入服务器要推送的新闻[输入exit表示退出推送]：");
            String news = Utility.readString(100);
            if("exit".equals(news)){
                break;
            }
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            message.setMessageType(MessageType.MESSAGE_TO_ALL_MES);
            System.out.println("服务器推送消息给所有人 说：" + news);


            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onlineUserId = iterator.next();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
