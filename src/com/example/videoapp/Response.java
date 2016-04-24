package com.example.videoapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

public class Response implements Serializable {

	private static final long serialVersionUID = 1L;
	private String response;
	
	Response(String aResponse){
		response=aResponse;
	}
	
	String getResponse(){
 		return response;
	}
	
	String unescapeSpecialCharacter(String aString){
    	if(aString.contains("\\;"))
			aString=aString.replace("\\;", ";");
		else if(aString.contains("\\:"))
			aString=aString.replace("\\:", ":");
		else if(aString.contains("\\"))
			aString=aString.replace("\\", "");
		return aString;
    }
}	

class NonceResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String nounce;
	
	NonceResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	private void parseResponse(String result){
		int startIndex=0,endIndex=0;
		String key;
		String value;
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			result=result.substring(endIndex+1);
			if(key.equalsIgnoreCase("ok"))
				nounce=value;	
		}
	}
	
	String getNounce(){
		return nounce;
	}
	
}

class SafeLoginResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String status,safeLoginError;
	
	SafeLoginResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	private void parseResponse(String result){
		int startIndex=0,endIndex=0;
		String key;
		String value;
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			result=result.substring(endIndex+1);
			if(key.equalsIgnoreCase("ok"))
				status= value;	
			else if(key.equalsIgnoreCase("error"))
				status= value;	
		}
	}
	
	String getStatus(){
		return status;
	}
	
	String getError(){
		return safeLoginError;
	}
}


//parses and stores the course details in a hashTable 
class CourseListResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	int numberOfCourses;
	private Hashtable<String,ArrayList<String>> courseList = new Hashtable<String,ArrayList<String>>();
	String error;
	
	CourseListResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	//parses the response till carriage return
	private void parseResponse(String result){
		int startIndex=0,endIndex=0,crIndex;
		String key,trimmedResult;
		String value;
		startIndex=result.indexOf(':');
		endIndex=result.indexOf(';');
		key=result.substring(0, startIndex);
		value=result.substring(startIndex+1,endIndex);
		if(key.equals("ok")){
			numberOfCourses= Integer.valueOf(value);
			result=result.substring(endIndex+1);
			while(result.contains(":")){
				crIndex=result.indexOf('\r');
				if(crIndex!=-1){
					trimmedResult= result.substring(0, crIndex);
					storeCourseDetails(trimmedResult);
					result=result.substring(crIndex+1);
				}else if(result.indexOf('\r')==-1){
					endIndex=result.lastIndexOf(';');
					trimmedResult= result.substring(0, endIndex);
					storeCourseDetails(trimmedResult);
					result=result.substring(endIndex+1);
				}
			}
		}else if(key.equals("error"))
			error=value;
	}
	
	//parses and stores the course details in a list
	private void storeCourseDetails(String result){
		int startIndex=0,endIndex=0;
		String key,value,courseName = "";
		ArrayList<String> courseDetails= new ArrayList<String>();
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			if(key.equalsIgnoreCase("name"))
				courseName=value;	
			else if(key.equalsIgnoreCase("id"))
				courseDetails.add(value);
			result=result.substring(endIndex+1);
		}
		courseList.put(courseName, courseDetails);
	}
	
	ArrayList<String> getCourseNameList(){
		Enumeration<String> keys =courseList.keys();
		ArrayList<String> courseName = new ArrayList<String>();
		while (keys.hasMoreElements()) 
			courseName.add(keys.nextElement());
		Collections.reverse(courseName);
		return courseName;
	}
	
	Hashtable<String,ArrayList<String>> getCourseList(){
		return courseList;
	}
	
	//returns a course Id 
	String getCourseId(String courseName){
		ArrayList<String> course=courseList.get(courseName);
		return course.get(0);
	}
}


//parses and stores the video details in a hashTable
class VideoListResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	String status;
	int numberOfVideos;
	Hashtable<String,ArrayList<String>> videoList = new Hashtable<String,ArrayList<String>>();
	String error;
	
	VideoListResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	//parses the response till the carriage return 
	private void parseResponse(String result){
		int startIndex=0,endIndex=0,crIndex;
		String key,trimmedResult;
		String value;
		startIndex=result.indexOf(':');
		endIndex=result.indexOf(';');
		key=result.substring(0, startIndex);
		value=result.substring(startIndex+1,endIndex);
		result=result.substring(endIndex+1);
		if(key.equals("ok")){
			numberOfVideos= Integer.valueOf(value);
			while(result.contains(":")){
				crIndex=result.indexOf('\r');
				if(crIndex!=-1){
					trimmedResult= result.substring(0, crIndex);
					storeVideoDetails(trimmedResult);
					result=result.substring(crIndex+1);
				}else if(result.indexOf('\r')==-1){
					endIndex=result.lastIndexOf(';');
					trimmedResult= result.substring(0, endIndex);
					storeVideoDetails(trimmedResult);
					result=result.substring(endIndex+1);
				}
			}
		}else if(key.equals("error"))
			error=value;
	}
	
	//parses and stores the video details in a list
	private void storeVideoDetails(String result){
		int startIndex=0,endIndex=0;
		String key;
		String value;
		String videoName = "";
		ArrayList<String> videoDetails= new ArrayList<String>();
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			if(key.equalsIgnoreCase("name"))
				videoName=value;	
			else if(key.equalsIgnoreCase("id"))
				videoDetails.add(value);
			else if(key.equalsIgnoreCase("date"))
				videoDetails.add(value);
			else if(key.equalsIgnoreCase("url")){
				String url=unescapeSpecialCharacter(value);
				videoDetails.add(url);
			}
			else if(key.equalsIgnoreCase("questions"))
				videoDetails.add(value);
			result=result.substring(endIndex+1);
		}
		videoList.put(videoName, videoDetails);
	}
	
	//returns the list of the video names
	ArrayList<String> getVideoNameList(){
		Enumeration<String> keys =videoList.keys();
		ArrayList<String> videoName = new ArrayList<String>();
		while (keys.hasMoreElements()) 
			videoName.add(keys.nextElement());
		Collections.reverse(videoName);
		return videoName;
	}
	
	String getVideoId(String videoName){
		ArrayList<String> videoDetails=videoList.get(videoName);
		return videoDetails.get(0);	
	}
	
	String getVideoUrl(String videoName){
		ArrayList<String> videoDetails=videoList.get(videoName);
		return videoDetails.get(2);	
	}
	
}


class QuestionListResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	String status;
	int numberOfQuestions;
	Hashtable<String,ArrayList<String>> questionList = new Hashtable<String,ArrayList<String>>();
	String error;
	
	QuestionListResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	//parses the response till the carriage return 
	private void parseResponse(String result){
		int startIndex=0,endIndex=0,crIndex;
		String key,trimmedResult;
		String value;
		startIndex=result.indexOf(':');
		endIndex=result.indexOf(';');
		key=result.substring(0, startIndex);
		value=result.substring(startIndex+1,endIndex);
		result=result.substring(endIndex+1);
		if(key.equals("ok")){
			numberOfQuestions= Integer.valueOf(value);
			while(result.contains(":")){
				crIndex=result.indexOf('\r');
				if(crIndex!=-1){
					trimmedResult= result.substring(0, crIndex);
					storeQuestionDetails(trimmedResult);
					result=result.substring(crIndex+1);
				}else if(result.indexOf('\r')==-1){
					endIndex=result.lastIndexOf(';');
					trimmedResult= result.substring(0, endIndex);
					storeQuestionDetails(trimmedResult);
					result=result.substring(endIndex+1);
				}
			}
		}else if(key.equals("error"))
			error=value;
	}
	
	//parses and stores the video details in a list
	private void storeQuestionDetails(String result){
		int startIndex=0,endIndex=0;
		String key,value,questionId="";
		ArrayList<String> questionDetails= new ArrayList<String>();
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			if(key.equalsIgnoreCase("id"))
				questionId=value;	
			else if(key.equalsIgnoreCase("text"))
				questionDetails.add(unescapeSpecialCharacter(value));
			else if(key.equalsIgnoreCase("time"))
				questionDetails.add(unescapeSpecialCharacter(value));
			else if(key.equalsIgnoreCase("timeStamp"))
				questionDetails.add(unescapeSpecialCharacter(value));
			else if(key.equalsIgnoreCase("answers"))
				questionDetails.add(unescapeSpecialCharacter(value));
			result=result.substring(endIndex+1);
		}
		questionList.put(questionId, questionDetails);
	}
	
	//returns the list of the question texts
	ArrayList<String> getQuestionTextList(){
		Enumeration<String> keys =questionList.keys();
		ArrayList<String> questionTextList = new ArrayList<String>();
		while (keys.hasMoreElements()) 
			questionTextList.add(getQuestionText(keys.nextElement()));
		//Collections.reverse(videoName);
		return questionTextList;
	}
	
	String getQuestionText(String questionId){
		return questionList.get(questionId).get(0);
	}
	
	String getQuestionId(String questionText){
		Enumeration<String> keys =questionList.keys();
		String key = "";
		while (keys.hasMoreElements()){
			key=keys.nextElement();
			if(getQuestionText(key).equals(questionText))
				return key;
		}
		return key; 
	}
	
	String getVideoTime(String questionId){
		return questionList.get(questionId).get(1);
	}
}


class AnswerListResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	String status;
	int numberOfAnswers;
	Hashtable<String,ArrayList<String>> answerList = new Hashtable<String,ArrayList<String>>();
	String error;
	
	AnswerListResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	//parses the response till the carriage return 
	private void parseResponse(String result){
		int startIndex=0,endIndex=0,crIndex;
		String key,trimmedResult,value;
		startIndex=result.indexOf(':');
		endIndex=result.indexOf(';');
		key=result.substring(0, startIndex);
		value=result.substring(startIndex+1,endIndex);
		result=result.substring(endIndex+1);
		if(key.equals("ok")){
			numberOfAnswers= Integer.valueOf(value);
			while(result.contains(":")){
				crIndex=result.indexOf('\r');
				if(crIndex!=-1){
					trimmedResult= result.substring(0, crIndex);
					storeAnswerDetails(trimmedResult);
					result=result.substring(crIndex+1);
				}else if(result.indexOf('\r')==-1){
					endIndex=result.lastIndexOf(';');
					trimmedResult= result.substring(0, endIndex);
					storeAnswerDetails(trimmedResult);
					result=result.substring(endIndex+1);
				}
			}
		}else if(key.equals("error"))
			error=value;
	}
	
	//parses and stores the video details in a list
	private void storeAnswerDetails(String result){
		int startIndex=0,endIndex=0;
		String key,value,answerId = "";
		ArrayList<String> answerDetails= new ArrayList<String>();
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			if(key.equalsIgnoreCase("id"))
				answerId=value;	
			else if(key.equalsIgnoreCase("text"))
				answerDetails.add(unescapeSpecialCharacter(value));
			else if(key.equalsIgnoreCase("timeStamp"))
				answerDetails.add(unescapeSpecialCharacter(value));
			result=result.substring(endIndex+1);
		}
		answerList.put(answerId, answerDetails);
	}
	
	//returns the list of the answer texts
	ArrayList<String> getAnswerTextList(){
		Enumeration<String> keys =answerList.keys();
		ArrayList<String> answerTextList = new ArrayList<String>();
		while (keys.hasMoreElements()) 
			answerTextList.add(getAnswerText(keys.nextElement()));
		Collections.reverse(answerTextList);
		return answerTextList;
	}
	
	String getAnswerText(String answerId){
		return answerList.get(answerId).get(0);
	}
	
	String getAnswerId(String answerText){
		Enumeration<String> keys =answerList.keys();
		String key = "";
		while (keys.hasMoreElements()){
			key=keys.nextElement();
			if(getAnswerText(key).equals(answerText))
				return key;
		}
		return key; 
	}
}


class QuestionAddResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String status,questionAddError;
	
	QuestionAddResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	private void parseResponse(String result){
		int startIndex=0,endIndex=0;
		String key;
		String value;
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			result=result.substring(endIndex+1);
			if(key.equalsIgnoreCase("ok"))
				status= value;	
			else if(key.equalsIgnoreCase("error"))
				questionAddError= value;	
		}
	}
	
	String getStatus(){
		return status;
	}
	
	String getError(){
		return questionAddError;
	}
}



class AnswerAddResponse extends Response implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String status;
	
	AnswerAddResponse(String response){
		super(response);
		parseResponse(response);
	}
	
	private void parseResponse(String result){
		int startIndex=0,endIndex=0;
		String key,value;
		while(true){
			startIndex=result.indexOf(':');
			if(startIndex==-1){
				break;
			}
			endIndex=result.indexOf(';');
			key=result.substring(0, startIndex);
			value=result.substring(startIndex+1,endIndex);
			result=result.substring(endIndex+1);
			if(key.equalsIgnoreCase("ok"))
				status= value;	
			else if(key.equalsIgnoreCase("error"))
				status= value;	
		}
	}
	
	String getStatus(){
		return status;
	}
	
}
