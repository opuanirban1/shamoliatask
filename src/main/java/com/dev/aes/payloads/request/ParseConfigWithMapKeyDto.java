package com.dev.aes.payloads.request;

public interface ParseConfigWithMapKeyDto {


    String  getSequence();
    void    setSequence(String  input);



    String  getName();
    void setName(String  input);


   /* String  getParse_key();
    void setParse_key(String input);*/


    String  getType();
    void setType(String  input);


    String getMapKey();
    void setMapKey(String input);

    String getLanType();
    void setLanType(String input);
}
