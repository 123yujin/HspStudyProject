package com.cwy.qqserver.service;

import com.cwy.qqcommon.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class OffMessageHashMap {
    private static ConcurrentHashMap<String, ArrayList<Message>>  um  = new ConcurrentHashMap<>();

    public static void addOffMessageHashMap(String getter,Message message){
        if(!um.containsKey(getter)){
            ArrayList<Message> value = new ArrayList<>();
            value.add(message);
            um.put(getter,value);
        }else {
            um.get(getter).add(message);
            um.put(getter,um.get(getter));
        }
    }
    public static ConcurrentHashMap<String,ArrayList<Message>> getUm(){
        return um;
    }
   public static void removeOffMessage(String getter){
        um.remove(getter);
   }

}
