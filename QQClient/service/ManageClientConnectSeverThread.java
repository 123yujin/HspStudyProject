package com.cwy.QQClient.service;

import java.util.HashMap;
import java.util.Map;

public class ManageClientConnectSeverThread {
    //把多线程放入一个HashMap的集合中，key就是用户id,value就是线程
   private static HashMap<String,ClientConnectSeverThread> hm  = new HashMap<>();
   public static void addClientConnectSeverThread(String userId,ClientConnectSeverThread clientConnectSeverThread){
            hm.put(userId,clientConnectSeverThread);
    }
    //通过userId获取到对应线程
    public static ClientConnectSeverThread getClientConnectSeverThread(String userId){
       return hm.get(userId);
    }
}
