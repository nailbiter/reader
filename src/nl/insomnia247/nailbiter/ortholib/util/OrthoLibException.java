package nl.insomnia247.nailbiter.ortholib.util;

public class OrthoLibException extends Exception{
	public OrthoLibException(String msg){
		super("OrthoLibException: "+msg);
	}
}
