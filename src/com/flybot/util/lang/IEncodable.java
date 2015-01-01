package com.flybot.util.lang;


public interface IEncodable
{
  public byte[] encode();
  
  public void decode(byte[] pBytes);
}
