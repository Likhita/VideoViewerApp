package com.example.videoapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Message {
	String message;
	
	String getMessage(){
		return message;
	}
	
	String escapeSpecialCharacter(String aString){
		if(aString.contains(";"))
			aString.replaceAll(";", "\\;");
		else if(aString.contains(":"))
			aString.replaceAll(":", "\\:");
		else if(aString.contains("\\"))
			aString.replaceAll(";", "\\");
		return aString;
	}
}

class Nonce extends Message{
	
	Nonce(){
		createMessage();
	}
	
	void createMessage(){
		message="nonce;;";
	}
	
	boolean isNonce(){
		return true;
	}
}


class SafeLogin extends Message{
	
	static final String HEXES = "0123456789ABCDEF";
	String userID,password,hex;
	
	SafeLogin(String aUserID, String aPassword,String nounce) throws NoSuchAlgorithmException{
		userID=aUserID;
		password=aPassword;
		computeHash(nounce);
		createMessage();
	}
	
	void checkData(){
		if(userID.contains(";"))
			userID.replaceAll(";", "\\;");
		else if(userID.contains(":"))
			userID.replaceAll(":", "\\:");
		else if(userID.contains("\\"))
			userID.replaceAll(";", "\\");
	}
	
	private void computeHash(String nounce) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update((password+nounce).getBytes());
		byte[] md5Hash = md5.digest();
		hex=getHex(md5Hash);
	}
	
	private String getHex(byte [] hash){
		if(hash==null){
			return null;
		}
		StringBuilder hex =new StringBuilder(2 * hash.length);
		for(final byte b : hash){
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	
	void createMessage(){
		checkData();
		message="safelogin;id:"+userID+";hash:"+hex+";;";
	}
	
	boolean isLogin(){
		return true;
	}
}

class CourseList extends Message{
	
	CourseList(){
		createMessage();
	}
	
	void createMessage(){
		message="courseList;;";
	}
	
	boolean isCourseList(){
		return true;
	}
}

class VideoList extends Message{
	
	String courseId;
	
	VideoList(String courseID){
		courseId=courseID;
		createMessage();
	}
	
	VideoList(String courseID,String videoID){
		courseId=courseID;
		createMessage(videoID);
	}
	
	void createMessage(){
		message="videoList;course:"+courseId+";;";
	}
	
	void createMessage(String videoID){
		message="videoList;course:"+courseId+";after:"+videoID+";;";
	}
	
	boolean isVideoList(){
		return true;
	}
}

class QuestionList extends Message{
	
	String videoId;
	
	QuestionList(String videoID){
		videoId=videoID;
		createMessage();
	}
	
	QuestionList(String videoID,String questionID){
		videoId=videoID;
		createMessage(questionID);
	}
	
	void createMessage(){
		message="questionList;video:"+videoId+";;";
	}
	
	void createMessage(String questionID){
		message="questionList;video:"+videoId+";after:"+questionID+";;";
	}
	
	boolean isQuestionList(){
		return true;
	}
}

class AnswerList extends Message{
	
	String questionId;
	
	AnswerList(String questionID){
		questionId=questionID;
		createMessage();
	}
	
	AnswerList(String questionID,String answerID){
		questionId=questionID;
		createMessage(answerID);
	}
	
	void createMessage(){
		message="answerList;question:"+questionId+";;";
	}
	
	void createMessage(String answerID){
		message="questionList;question:"+questionId+";after:"+answerID+";;";
	}
	
	boolean isAnswerList(){
		return true;
	}
}

class QuestionAdd extends Message{
	
	String videoId;
	String question,time;
	
	QuestionAdd(String videoID,String aQuestion,String aTime){
		videoId=videoID;
		question=aQuestion;
		time= aTime;
		createMessage(videoId,escapeSpecialCharacter(question),time);
	}
	
	void createMessage(String videoId,String question,String MillisecondsFromStart){
		message="questionAdd;video:"+videoId+";text:"+question+";time:"+MillisecondsFromStart+";;";
	}
}

class AnswerAdd extends Message{
	
	String questionId;
	String answer;
	
	AnswerAdd(String questionID,String aAnswer){
		questionId=questionID;
		answer=aAnswer;
		createMessage(questionId,escapeSpecialCharacter(answer));
	}
	
	void createMessage(String questionId,String answer){
		message="answerAdd;question:"+questionId+";text:"+answer+";;";
	}
}

class Logout extends Message{
	
	Logout(){
		createMessage();
	}
	
	void createMessage(){
		message="logout;;";
	}
	
	boolean isLogoutMessage(){
		return true;
	}
}