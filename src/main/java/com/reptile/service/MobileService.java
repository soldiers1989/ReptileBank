package com.reptile.service;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.reptile.util.CountTime;
import com.reptile.util.Dates;
import com.reptile.util.PushSocket;
import com.reptile.util.PushState;
import com.reptile.util.Resttemplate;
import com.reptile.util.SimpleHttpClient;
import com.reptile.util.application;
import com.reptile.winio.CmbBank;



@SuppressWarnings("deprecation")
@Service("mobileService")
public class MobileService {

	Resttemplate resttemplate=new Resttemplate();
    application application=new application();
    
    private static Logger logger= LoggerFactory.getLogger(MobileService.class);
	
		
			public Map<String, Object> Queryinfo(HttpSession session,HttpServletResponse response,String codes,String sessid,String ClientNos,String idCard,String UUID,String timeCnt,String numbe)
					throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, java.text.ParseException {
				Map<String,Object>maps=new HashMap();	
				logger.warn("########【招商信用卡########登陆开始】########【用户名：】"
						+ numbe + "【身份证号：】" + idCard);	
				
				Map<String,Object> map=new HashMap<String, Object>();//请求头
				PushSocket.push(map, UUID, "1000","招商银行信用卡登录中");
				Thread.sleep(2000);
				boolean isok = CountTime.getCountTime(timeCnt);
				if(isok==true){
					PushState.state(idCard,"bankBillFlow", 100);
				}				
				SimpleHttpClient httclien=new SimpleHttpClient();
		    Map<String,Object> params=new HashMap<String, Object>();//参数
		    Map<String,Object> paramse=new HashMap<String, Object>();//参数
		    Map<String,Object> paramend=new HashMap<String, Object>();//参数
		    Map<String,Object> parmcode=new HashMap<String, Object>();//参数
		    Map<String,String> headers=new HashMap<String, String>();//请求头
		    
		    Map<String,Object> data=new HashMap<String, Object>();//请求头
		    String zhangdan="";
		    boolean codeflg=true;
		    PushSocket.push(map, UUID, "2000","招商银行信用卡登录成功");
		    logger.warn("########【招商信用卡 登录成功】########【卡号：】"+numbe);
		    String cookie=session.getAttribute(sessid).toString();		    
		    
			 if(codes.equals("0")){
				 logger.warn("########【招商信用卡 不需要验证码时获取数据】########");
				 //不需要验证码的处理
				 	System.out.println("直接授权登陆");
				 	Thread.sleep(1000);
				 	PushSocket.push(map, UUID, "5000","数据获取中");
				 	logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe);
			    	params.put("AuthName","<AuthName>CBANK_CREDITCARD_LOAN</AuthName>" );
			    	params.put("ClientNo",ClientNos);
			    	
			        String rest1=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/ApplyToken.aspx", params, headers);//开始发包
			        if(rest1.contains("当前用户不允许使用该业务")) {
			        	logger.warn("########【招商信用卡 数据获取失败  原因：当前用户不允许使用该业务】########【卡号：】"+numbe);
			        	PushSocket.push(map, UUID, "7000","当前用户不允许使用该业务");
			    		if(isok==true){
							PushState.state(idCard,"bankBillFlow", 200,"当前用户不允许使用该业务");
						}else {
							PushState.stateX(idCard,"bankBillFlow", 200,"当前用户不允许使用该业务");
						}
			    		map.put("errorCode","0002");
		    			map.put("errorInfo","当前用户不允许使用该业务");
		    			logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
			    		return map;
			        }
			    	params.put("ClientNo",ClientNos);
			    	logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe+"【rest1:】"+rest1);
			    	String toke=rest1.substring(rest1.indexOf("<AuthToken>"), rest1.indexOf("</AuthResponseBody>"));
			       	params.put("AuthToken",toke);
			       	String rest2=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Login/Login.aspx", params, headers);//开始发包
			       	logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe+"【rest2:】"+rest2);
			       	headers.put("Request-Line", "POST /CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningSurveyNew.aspx HTTP/1.1");
			       	headers.put("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");      		
			       	headers.put("Accept-Encoding", "gzip, deflate");   		
			       	headers.put("Accept-Language", "zh-CN");   		
			       	headers.put("Cache-Control", "no-cache");	
			       	headers.put("Connection", "Keep-Alive");
			       	headers.put("Content-Type", "application/x-www-form-urlencoded");
			       	headers.put("Host", "pbsz.ebank.cmbchina.com");	       		
			       	headers.put("Referer", "https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenIndex.aspx");
			       	headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E)");	    
			        headers.put("Cookie",cookie);
			    	paramse.put("ClientNo",rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", ""));
			       	String rest3=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningSurveyNew.aspx", paramse, headers);//开始发包
			       	logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe+"【rest3:】"+rest3);
			       	List list= getHtmlEarthBean(rest3);
			        List lists=new ArrayList();
			       	//获取账单详情
			        try {
			        	for (int i = 0; i < list.size()-6; i++) {
				         	paramend.put("ClientNo",rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", ""));
					       	paramend.put("CreditAccNo",list.get(i).toString().substring(list.get(i).toString().indexOf("CreditAccNo="),list.get(i).toString().indexOf("')")).replace("CreditAccNo=", ""));
					       	paramend.put("IN_YYYYMM",list.get(i).toString().substring(list.get(i).toString().indexOf("IN_YYYYMM="), list.get(i).toString().indexOf("'CreditAccNo")).replace("',", "").replace("IN_YYYYMM=",""));
					       	paramend.put("O_STMT_FLAG","Y");
					       	String endinfo=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningListNew.aspx", paramend, headers);//开始发包
					    	if(endinfo.contains("账单数据正在更新中")){					    		
					    		continue;
					    	}else{
					    		lists.add(endinfo);
					    	}
					   
						}
			        	if(list.size()<=0){
			        		logger.warn("########【招商信用卡 数据获取失败  原因：暂无账单】########【卡号：】"+numbe);
				    		PushSocket.push(map, UUID, "7000","暂无账单");
				    		if(isok==true){
								PushState.state(idCard,"bankBillFlow", 200,"暂无账单");
							}else {
								PushState.stateX(idCard,"bankBillFlow", 200,"暂无账单");
							}
				    		map.put("errorCode","0002");
			    			map.put("errorInfo","暂无账单");
			    			logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
				    		return map;
				    	}
					} catch (Exception e) {
					 	for (int i = 0; i < list.size(); i++) {
				         	paramend.put("ClientNo",rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", ""));
					       	paramend.put("CreditAccNo",list.get(i).toString().substring(list.get(i).toString().indexOf("CreditAccNo="),list.get(i).toString().indexOf("')")).replace("CreditAccNo=", ""));
					       	paramend.put("IN_YYYYMM",list.get(i).toString().substring(list.get(i).toString().indexOf("IN_YYYYMM="), list.get(i).toString().indexOf("'CreditAccNo")).replace("',", "").replace("IN_YYYYMM=",""));
					       	paramend.put("O_STMT_FLAG","Y");
					       	String endinfo=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningListNew.aspx", paramend, headers);//开始发包
					    	if(endinfo.contains("账单数据正在更新中")){
					    		continue;
					    	}else{
					    		lists.add(endinfo);
					    	}
						}
					}
			   

			    	data.put("html", lists);
			    	data.put("backtype", "CMB");
			    	data.put("idcard", idCard);
			    	data.put("userAccount", numbe);
			    	map.put("data",data);
			    
			 
			    
			 }else{
				 System.out.println(codes.replace("0,", "")+"---");
				 	parmcode.put("ClientNo", ClientNos);
			    	parmcode.put("PRID", "VerifyMSGCode");
			    	parmcode.put("SendCode",codes.replace("0,", ""));
			    	String seninfo=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenLoginVerifyM2.aspx", parmcode, headers);
			    	if(seninfo.contains("code>00</code>")){
			    		PushSocket.push(map, UUID, "2000","招商信用卡登录成功");
			    		logger.warn("########【招商信用卡  登录成功】########【卡号：】"+numbe);
			    		System.out.println("成功登陆");
			    		Thread.sleep(1000);
			    		System.out.println("继续发包请求");
			    		PushSocket.push(map, UUID, "5000","招商信用卡数据获取中");
			    		logger.warn("########【招商信用卡  数据获取中】########【卡号：】"+numbe);

				    	params.put("AuthName","<AuthName>CBANK_CREDITCARD_LOAN</AuthName>" );
				    	params.put("ClientNo",ClientNos);
				    	
				        String rest1=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/ApplyToken.aspx", params, headers);//开始发包
				        logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe+"【rest1:】"+rest1);
				    	params.put("ClientNo",ClientNos);
				    	String toke = null;
				    	try {
				    		toke =rest1.substring(rest1.indexOf("<AuthToken>"), rest1.indexOf("</AuthResponseBody>"));
				    	  	if(toke.contains("当前用户不允许使用该业务")){
				    	  		logger.warn("########【招商信用卡 数据获取失败  原因：当前用户不允许使用该业务】########【卡号：】"+numbe);
				    	  		PushSocket.push(map, UUID, "7000","当前用户不允许使用该业务");
				    	  		if(isok==true){
									PushState.state(idCard,"bankBillFlow", 200,"当前用户不允许使用该业务");
								}else {
									PushState.stateX(idCard,"bankBillFlow", 200,"当前用户不允许使用该业务");
								}
				    	  		map.put("errorCode","0002");
				    			map.put("errorInfo","当前用户不允许使用该业务");
				    			logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
					    		return map;
					    	}
						} catch (java.lang.StringIndexOutOfBoundsException e) {
							logger.warn("########【招商信用卡 数据获取失败  原因：当前用户不允许使用该业务】########【卡号：】"+numbe);
							PushSocket.push(map, UUID, "7000","当前用户不允许使用该业务");
							if(isok==true){
								PushState.state(idCard,"bankBillFlow", 200,"当前用户不允许使用该业务");
							}else {
								PushState.stateX(idCard,"bankBillFlow", 200,"当前用户不允许使用该业务");
							}
							map.put("errorCode","0002");
			    			map.put("errorInfo","当前用户不允许使用该业务");
			    			logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
				    		return map;
						}
			
				  
				    	System.out.println(toke);
				       	params.put("AuthToken",toke);
				       	String rest2=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Login/Login.aspx", params, headers);//开始发包
				        logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe+"【rest2:】"+rest2);
				       	if(rest2.contains("您的查询密码输错次数过多，建议登录我行掌上生活或手机银行重设查询密码")) {
				       		logger.warn("########【招商信用卡 数据获取失败  原因：您的查询密码输错次数过多】########【卡号：】"+numbe);
							  PushSocket.push(map, UUID, "7000","您的查询密码输错次数过多");
					    		if(isok==true){
									PushState.state(idCard,"bankBillFlow", 200,"您的查询密码输错次数过多");
								}else {
									PushState.stateX(idCard,"bankBillFlow", 200,"您的查询密码输错次数过多");
								}
					    		map.put("errorCode","0002");
				    			map.put("errorInfo","您的查询密码输错次数过多");	
				    			logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
				    			return  map;
						  }
				   
				       	headers.put("Request-Line", "POST /CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningSurveyNew.aspx HTTP/1.1");
				       	headers.put("Accept", "image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");      		
				       	headers.put("Accept-Encoding", "gzip, deflate");   		
				       	headers.put("Accept-Language", "zh-CN");   		
				       	headers.put("Cache-Control", "no-cache");//		
				       	headers.put("Connection", "Keep-Alive");
				       	headers.put("Content-Type", "application/x-www-form-urlencoded");
				       	headers.put("Host", "pbsz.ebank.cmbchina.com");	       		
				       	headers.put("Referer", "https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenIndex.aspx");
				       	headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E)");	    
				        headers.put("Cookie",cookie);
				    	paramse.put("ClientNo",rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", ""));
				    	
				       	System.out.println(rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", "")+"666");

				       	String rest3=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningSurveyNew.aspx", paramse, headers);//开始发包
				       	logger.warn("########【招商信用卡 数据获取中】########【卡号：】"+numbe+"【rest3:】"+rest3);
				       	
				       	//获取账单详情
				        List list= getHtmlEarthBean(rest3);
				        //循环查询所有账单
					    List lists=new ArrayList();
					    
					   try {
						
				
				        for (int i = 0; i < list.size()-6; i++) {
				          	//获取账单详情
					       	paramend.put("ClientNo",rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", ""));
					       	paramend.put("CreditAccNo",list.get(i).toString().substring(list.get(i).toString().indexOf("CreditAccNo="),list.get(i).toString().indexOf("')")).replace("CreditAccNo=", ""));
					       	paramend.put("IN_YYYYMM",list.get(i).toString().substring(list.get(i).toString().indexOf("IN_YYYYMM="), list.get(i).toString().indexOf("'CreditAccNo")).replace("',", "").replace("IN_YYYYMM=",""));
					       	paramend.put("O_STMT_FLAG","Y");
					       	String endinfo=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningListNew.aspx", paramend, headers);//开始发包
					    	if(endinfo.contains("账单数据正在更新中")){
					    		continue;
					    	}else{
					    		lists.add(endinfo);
					    	}
					    	
				        }
					      
				        if(list.size()<=0){
				        	logger.warn("########【招商信用卡 数据获取失败  原因： 暂无账单】########【卡号：】"+numbe);
				    		PushSocket.push(map, UUID, "7000","暂无账单");
				    		if(isok==true){
								PushState.state(idCard,"bankBillFlow", 200,"暂无账单");
							}else {
								PushState.stateX(idCard,"bankBillFlow", 200,"暂无账单");
							}
				    		map.put("errorCode","0002");
			    			map.put("errorInfo","暂无账单");
			    			logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
				    		return map;
				    	}
						} catch (Exception e) {
						    for (int i = 0; i < list.size(); i++) {
					          	//获取账单详情
						       	paramend.put("ClientNo",rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", ""));
						       	paramend.put("CreditAccNo",list.get(i).toString().substring(list.get(i).toString().indexOf("CreditAccNo="),list.get(i).toString().indexOf("')")).replace("CreditAccNo=", ""));
						       	paramend.put("IN_YYYYMM",list.get(i).toString().substring(list.get(i).toString().indexOf("IN_YYYYMM="), list.get(i).toString().indexOf("'CreditAccNo")).replace("',", "").replace("IN_YYYYMM=",""));
						       	paramend.put("O_STMT_FLAG","Y");
						       	String endinfo=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_CreditCard_Loan/UI/CreditCard/Loan/am_QueryReckoningListNew.aspx", paramend, headers);//开始发包
						    	if(endinfo.contains("账单数据正在更新中")){
						    		continue;
						    	}else{
						    		lists.add(endinfo);
						    	}
						       
					        }
							// TODO: handle exception
						}
				    	data.put("html", lists);
				    	data.put("backtype", "CMB");
				    	data.put("idcard", idCard);
				    	data.put("userAccount", numbe);
				    	map.put("data",data);
				    
			    	}else{
			    
						codeflg=false;
			    	}			 
			 }
			 if(codeflg==true){
				 PushSocket.push(map, UUID, "6000","招商信用卡数据获取成功");
				 logger.warn("########【招商信用卡 数据获取成功】########【卡号：】"+numbe);
//				 map=resttemplate.SendMessage(map,"http://113.200.105.37:8080/HSDC/BillFlow/BillFlowByreditCard"); 
				 logger.warn("########【招商信用卡 开始推送】########【卡号：】"+numbe);
				 map=resttemplate.SendMessage(map,application.sendip+"/HSDC/BillFlow/BillFlowByreditCard"); 
				 logger.warn("########【招商信用卡推送完成    【卡号：】"+numbe+"数据中心返回结果："+map.toString());
//				 map=resttemplate.SendMessage(map,"http://192.168.3.4:8081/HSDC/BillFlow/BillFlowByreditCard"); 
				 if(map.toString().contains("0000")){
					 //下版本替换
					 if(isok==true){
				    	PushState.state(idCard, "bankBillFlow",300);
					 }					 
//					 PushState.stateByFlag(idCard,"bankBillFlow",300,isok);
					 PushSocket.push(map, UUID, "8000","认证成功");
				    	
		                map.put("errorInfo","查询成功");
		                map.put("errorCode","0000");
		        }else{
		            	//--------------------数据中心推送状态----------------------
		        	if(isok==true){
		            	PushState.state(idCard, "bankBillFlow",200,map.get("errorInfo").toString());
		        	}else {
						PushState.stateX(idCard,"bankBillFlow", 200,map.get("errorInfo").toString());
					}
		        	PushSocket.push(map, UUID, "9000",map.get("errorInfo").toString());
		            	//---------------------数据中心推送状态----------------------
		                map.put("errorInfo","查询失败");
		                map.put("errorCode","0003");
		            }
			 }else{
				 	if(isok==true){
		            	PushState.state(idCard, "bankBillFlow",200,"验证码错误，登录失败");
		        	}else {
						PushState.stateX(idCard,"bankBillFlow", 200,"验证码错误，登录失败");
					}
				 	PushSocket.push(map, UUID, "3000","验证码错误，登录失败");
				 	 logger.warn("########【招商信用卡登陆失败  原因：验证码错误#########");
					map.put("errorCode","0001");
	    			map.put("errorInfo","验证码错误");
			 }
			 logger.warn("----招商信用卡第二个接口------errorCode："+map.get("errorCode")+"-----errorInfo："+map.get("errorInfo"));
			 return  map;

			
			
		}
		
		 public static List getHtmlEarthBean (String html) { 
			 List list=new ArrayList();
		        if (html != null && !"".equals(html)) { 
		            Document doc = Jsoup.parse(html);    
		            Elements linksElements = doc.getElementsByAttributeValue("class", "dgMain");//获取class名字为 news-table  
		            for (Element ele : linksElements) { 
		                Elements linksElements1 = ele.getElementsByTag("td");//获取网页td的标签元素  
		                for (Element ele1 : linksElements1) { 
		                	if(ele1.html().contains("IN_YYYYMM")&&ele1.html().contains("CreditAccNo")){
		                		list.add(ele1.html());
		                
		                	}
		            	
		                
		                } 
		            }    
		        } 
		  return list;
		} 
		
		 /**
		  * 储蓄卡查询
		 * @throws IOException 
		 * @throws ParseException 
		 * @throws InterruptedException 
		  */
		 
		 public Map<String,Object> CmbQueryInfo(String code,String sessid,String num,String idcard,HttpServletRequest re,String UUID,String Sendcode,String numbe,boolean flag,String userName) throws ParseException, IOException, InterruptedException{
				SimpleHttpClient httclien=new SimpleHttpClient();
				logger.warn("########【招商储蓄卡第二接口     登陆开始】########【短信验证码：】"
						+ code + "【账号：】" + numbe+"【身份证号：】"+idcard);	
				Map<String,Object> params=new HashMap<String, Object>();
				Map<String,String> headers=new HashMap<String, String>();
				Map<String,Object> parmcode=new HashMap<String, Object>();
				Map<String,Object> data1=new HashMap<String, Object>();
				Map<String,String> head1=new HashMap<String, String>();
				PushState.state(idcard, "savings", 100);
//				PushState.stateByFlag(idcard, "savings", 100,flag);
				PushSocket.push(params, UUID, "1000","招商储蓄卡登陆中");
				Map status=new HashMap();
				HttpSession session=re.getSession();
				System.out.println("*******获取sessid******");
				String cookieid= session.getAttribute(sessid).toString();
				System.out.println("*******获取sessid之后******");
				parmcode.put("ClientNo", num);
				parmcode.put("PRID", "VerifyMSGCode");
				parmcode.put("SendCode",code);
	    	//是否需要验证码
				System.out.println("num="+num+",code="+code);
	    	if(!code.equals("0")){
	    		//开始发包登陆
		    	String seninfo=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenLoginVerifyM2.aspx", parmcode, headers);
		    	if(seninfo.contains("code>00</code>")){
		    	 
		    		System.out.println("成功登陆");		    		
		    		PushSocket.push(params, UUID, "2000","招商储蓄卡登陆成功");
		    		logger.warn("########【招商储蓄卡   登陆成功】########【身份证号：】"+idcard);
		    		try {
		    			System.out.println("继续发包请求");
			    		Thread.sleep(2000);
			    		PushSocket.push(params, UUID, "5000","招商储蓄卡数据获取中");
			    		logger.warn("########【招商储蓄卡  数据获取中】########【身份证号：】"+idcard);
				    	params.put("AuthName","<AuthName>CBANK_DEBITCARD_ACCOUNTMANAGER</AuthName>" );
				    	params.put("ClientNo",num);
				        String rest1=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/ApplyToken.aspx", params, headers);//开始发包
				    	logger.warn("########【招商储蓄卡 数据获取中】########【身份证号：】"+idcard+"【rest1:】"+rest1);
				        String toke =rest1.substring(rest1.indexOf("<AuthToken>"), rest1.indexOf("</AuthResponseBody>"));	
				       	System.out.println(toke);
				       	params.put("AuthToken",toke);
				       	String rest2=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_DebitCard_AccountManager/UI/DebitCard/Login/Login.aspx", params, headers);//开始发包
				       	logger.warn("########【招商储蓄卡 数据获取中】########【身份证号：】"+idcard+"【rest2:】"+rest2);
				       	String num1=rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", "");
				       	data1.put("ClientNo",num1);//开始获取有用信息了
				       	head1.put("Request-Line", "POST /CmbBank_DebitCard_AccountManager/UI/DebitCard/AccountQuery/am_QueryHistoryTrans.aspx HTTP/1.1");
				       	head1.put("Accept", "text/html, application/xhtml+xml, */*");
				       	head1.put("Accept-Encoding", "gzip, deflate");
				       	head1.put("Accept-Language", "zh-CN");
				       	head1.put("Cache-Control", "no-cache");
				       	head1.put("Connection", "Keep-Alive");
				       	head1.put("Cookie",cookieid.toString().replaceAll("path=/,", "").replaceAll("path=/","").replace("; ;", ";"));
				       	head1.put("Content-Type", "application/x-www-form-urlencoded");
				       	head1.put("Host", "pbsz.ebank.cmbchina.com");
				       	head1.put("Referer", "https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenIndex.aspx");
				       	head1.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
				
				        String loginstr=	httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_DebitCard_AccountManager/UI/DebitCard/AccountQuery/am_QueryHistoryTrans.aspx", data1, head1);
			
				        System.out.println(loginstr+num1+"-----");
				        
				        
				        
				        Document doc = Jsoup.parse(loginstr);    
			            Element linksElements = doc.getElementById("__EVENTVALIDATION");
			            Element VIEWSTATE = doc.getElementById("__VIEWSTATE");
			            Element VIEWSTATEGENERATOR = doc.getElementById("__VIEWSTATEGENERATOR");
			            Element ddlDebitCardLists = doc.getElementById("ddlDebitCardList");
			            
			            String EVENTVALIDATION=linksElements.val();
			            String VIEWSTATES=VIEWSTATE.val();
			            String VIEWSTATEGENERATORS=VIEWSTATEGENERATOR.val();
			            String ssid=ddlDebitCardLists.getElementsByTag("option").val();
			            params.clear();
			            params.put("__EVENTVALIDATION", EVENTVALIDATION);
			            params.put("__VIEWSTATE",VIEWSTATES);
			            params.put("__VIEWSTATEGENERATORS",VIEWSTATEGENERATORS);
			            List<Map> lists=yuefen();
			            params.put("BeginDate",lists.get(0).get("begin"));
			            params.put("EndDate",lists.get(5).get("end"));
			            String EndDate = Dates.currentTime();
			            System.out.println("***************************************获取前六个月月份");
			            String BeginDate = Dates.beforMonth(6);
			            params.put("BeginDate",BeginDate);
			            params.put("EndDate",EndDate);
			            params.put("BtnOK","查 询");
			            params.put("ClientNo",num1);
			            params.put("ddlDebitCardList",ssid);
			            String loginstr1=	httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_DebitCard_AccountManager/UI/DebitCard/AccountQuery/am_QueryHistoryTrans.aspx", params, headers);
			            logger.warn("########【招商储蓄卡 数据获取中】########【身份证号：】"+idcard+"【流水信息loginstr1:】"+loginstr1);
			            System.out.println(loginstr1+"流水信息");
			            //无流水信息
			            Document  infotable=  Jsoup.parse(loginstr1);  
			            Elements tags= infotable.getElementsByTag("title");
			            String title = tags.get(0).text();
			            if(title.contains("moved")) {	
			            	logger.warn("########【招商储蓄卡 数据获取失败  原因：请确认您的银行卡是否为储蓄卡】");
			            	params.put("errorInfo", "请确认您的银行卡是否为储蓄卡");
				    		params.put("errorCode", "0002");
				    		PushSocket.push(params, UUID, "7000","请确认您的银行卡是否为储蓄卡");
				    		PushState.stateByFlag(idcard, "savings", 200,"请确认您的银行卡是否为储蓄卡",flag);
				    		System.out.println("*********************************************请确认您的银行卡是否为储蓄卡");
				    		logger.warn("----招商储蓄卡第二个接口------errorCode："+params.get("errorCode")+"-----errorInfo："+params.get("errorInfo"));
				    		return params;
			            }
				        Document docs = Jsoup.parse(loginstr1);   
				 	   Element trs = docs.getElementById("dgHistoryTransRecSet");
				 	   System.out.println(trs.html());
					   Elements tr=trs.select("tr");
					
			    	  List list=new ArrayList();
					   for (int i = 1; i < tr.size(); i++) {
						   Map<String,Object>map=new HashMap<String, Object>();
						   System.out.println("开始");
						   Elements td=tr.get(i).select("td");
						   for (int j = 0; j < td.size(); j++) {
							   map.put("expendMoney", "");//交易金额
							   if(j==0){
								   map.put("dealTime", td.get(j).text());//交易时间
							   }
							   if(j==2){
								   map.put("incomeMoney", td.get(j).text());//交易金额
							   }
							   if(j==5){
								   map.put("dealDitch", td.get(j).text());//交易渠道
							   }
							   if(j==4){
								   map.put("balanceAmount", td.get(j).text());//余额
							   }
							   if(j==6){
								   map.put("dealReferral", td.get(j).text());//业务摘要
							   }
							   
							
						   }
						   map.put("oppositeSideName", "");
						   map.put("oppositeSideNumber", "");
						   map.put("currency", "");
						   list.add(map);					   
					   }
					   if(list.size()==0) {			
						   logger.warn("########【招商储蓄卡 数据获取失败  原因：您的账号暂无账单】【身份证号：】"+idcard);
						   params.put("errorInfo", "您的账号暂无账单");
			    		   params.put("errorCode", "0002");
			    		   PushSocket.push(params, UUID, "7000","您的账号暂无账单");
			    		   PushState.stateByFlag(idcard, "savings", 200,"您的账号暂无账单",flag);
			    		   logger.warn("----招商储蓄卡第二个接口------errorCode："+params.get("errorCode")+"-----errorInfo："+params.get("errorInfo"));
			    		   System.out.println("*********************************************您的账号暂无账单");
					   }
					   params.clear();
					   headers.clear();
					   headers.put("accountType","");
					   headers.put("openBranch","");
					   headers.put("openTime","");

					    params.put("billMes", list);
					    params.put("baseMes", headers);
					    params.put("bankName", "中国招商银行");
					    params.put("IDNumber", idcard);
					    params.put("cardNumber", numbe);
					    params.put("userName", userName);
					    System.out.println(JSONObject.fromObject(params));
					    Thread.sleep(1000);
					    PushSocket.push(params, UUID, "6000","招商储蓄卡数据获取成功");
					    logger.warn("########【招商储蓄卡 数据获取成功】【身份证号：】"+idcard);
						Resttemplate resttemplate=new Resttemplate();
						logger.warn("########【招商储蓄卡 开始推送】【身份证号：】"+idcard);
						status=	resttemplate.SendMessage(JSONObject.fromObject(params), application.sendip+"/HSDC/savings/authentication",idcard,UUID);
						logger.warn("########【招商储蓄卡推送完成    身份证号：】"+idcard+"数据中心返回结果："+status.toString());
		    		}catch(Exception e) {
		    			logger.warn("--------招商储蓄卡-------"+e);
		    			logger.warn("########【招商储蓄卡 进入try-catch  原因：网络异常,请重试】【身份证号：】"+idcard);
		    			params.put("errorInfo", "网络异常,请重试！！");
		    			params.put("errorCode", "0003");
		    			PushSocket.push(params, UUID, "9000","网络异常，认证失败");
		    			PushState.stateByFlag(idcard, "savings", 200,"网络异常，认证失败",flag);
		    			logger.warn("----招商储蓄卡第二个接口------errorCode："+params.get("errorCode")+"-----errorInfo："+params.get("errorInfo"));
		    		}
				 }else {
					 logger.warn("########【招商储蓄卡   登陆失败   原因：查询失败】【身份证号：】"+idcard);
					 params.put("errorInfo", "查询失败");
					 params.put("errorCode", "0001");
					 PushSocket.push(params, UUID, "3000","查询失败");
					 PushState.stateByFlag(idcard, "savings", 200,"查询失败",flag);
					 logger.warn("----招商储蓄卡第二个接口------errorCode："+params.get("errorCode")+"-----errorInfo："+params.get("errorInfo"));
				 }
	    	}else{
	    		try {
	    			System.out.println("成功登陆");
	    			logger.warn("########【招商储蓄卡   登陆成功】########【身份证号：】"+idcard);
		    		PushSocket.push(status, UUID, "2000","招商储蓄卡登陆成功");
		    		System.out.println("继续发包请求");
		    		Thread.sleep(2000);
		    		PushSocket.push(params, UUID, "5000","招商储蓄卡数据获取中");
		    		logger.warn("########【招商储蓄卡   数据获取中】########【身份证号：】"+idcard);
			    	params.put("AuthName","<AuthName>CBANK_DEBITCARD_ACCOUNTMANAGER</AuthName>" );
			    	params.put("ClientNo",num);
			        String rest1=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/ApplyToken.aspx", params, headers);//开始发包
			        logger.warn("########【招商储蓄卡 数据获取中】########【身份证号：】"+idcard+"【rest1:】"+rest1);
			        String toke =rest1.substring(rest1.indexOf("<AuthToken>"), rest1.indexOf("</AuthResponseBody>"));	
			       	System.out.println(toke);
			       	params.put("AuthToken",toke);
			       	String rest2=httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_DebitCard_AccountManager/UI/DebitCard/Login/Login.aspx", params, headers);//开始发包
			       	logger.warn("########【招商储蓄卡 数据获取中】########【身份证号：】"+idcard+"【rest2:】"+rest2);
			       	String num1=rest2.substring(rest2.indexOf("<ClientNo>"), rest2.indexOf("</ClientNo>")).replace("<ClientNo>", "");
			       	data1.put("ClientNo",num1);//开始获取有用信息了
			       	head1.put("Request-Line", "POST /CmbBank_DebitCard_AccountManager/UI/DebitCard/AccountQuery/am_QueryHistoryTrans.aspx HTTP/1.1");
			       	head1.put("Accept", "text/html, application/xhtml+xml, */*");
			       	head1.put("Accept-Encoding", "gzip, deflate");
			       	head1.put("Accept-Language", "zh-CN");
			       	head1.put("Cache-Control", "no-cache");
			       	head1.put("Connection", "Keep-Alive");
			       	head1.put("Cookie",cookieid.toString().replaceAll("path=/,", "").replaceAll("path=/","").replace("; ;", ";"));
			       	head1.put("Content-Type", "application/x-www-form-urlencoded");
			       	head1.put("Host", "pbsz.ebank.cmbchina.com");
			       	head1.put("Referer", "https://pbsz.ebank.cmbchina.com/CmbBank_GenShell/UI/GenShellPC/Login/GenIndex.aspx");
			       	head1.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
			
			        String loginstr=	httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_DebitCard_AccountManager/UI/DebitCard/AccountQuery/am_QueryHistoryTrans.aspx", data1, head1);
		
			        System.out.println(loginstr+num1+"-----");
			        
			        
			        
			        Document doc = Jsoup.parse(loginstr);    
		            Element linksElements = doc.getElementById("__EVENTVALIDATION");
		            Element VIEWSTATE = doc.getElementById("__VIEWSTATE");
		            Element VIEWSTATEGENERATOR = doc.getElementById("__VIEWSTATEGENERATOR");
		            Element ddlDebitCardLists = doc.getElementById("ddlDebitCardList");
		            
		            String EVENTVALIDATION=linksElements.val();
		            String VIEWSTATES=VIEWSTATE.val();
		            String VIEWSTATEGENERATORS=VIEWSTATEGENERATOR.val();
		            String ssid=ddlDebitCardLists.getElementsByTag("option").val();
		            params.clear();
		            params.put("__EVENTVALIDATION", EVENTVALIDATION);
		            params.put("__VIEWSTATE",VIEWSTATES);
		            params.put("__VIEWSTATEGENERATORS",VIEWSTATEGENERATORS);
		            List<Map> lists=yuefen();
		            params.put("BeginDate",lists.get(0).get("begin"));
		            params.put("EndDate",lists.get(5).get("end"));
		            String EndDate = Dates.currentTime();
		            System.out.println("***************************************获取前六个月月份");
		            String BeginDate = Dates.beforMonth(6);		            
		            params.put("BeginDate",BeginDate);
		            params.put("EndDate",EndDate);
		            params.put("BtnOK","查 询");
		            params.put("ClientNo",num1);
		            params.put("ddlDebitCardList",ssid);
		            String loginstr1=	httclien.post("https://pbsz.ebank.cmbchina.com/CmbBank_DebitCard_AccountManager/UI/DebitCard/AccountQuery/am_QueryHistoryTrans.aspx", params, headers);
		            logger.warn("########【招商储蓄卡 数据获取中】########【身份证号：】"+idcard+"【流水信息1loginstr1:】"+loginstr1);
		            Document  infotable=  Jsoup.parse(loginstr1);  
		            Elements tags= infotable.getElementsByTag("title");
		            String title = tags.get(0).text();
		            if(title.contains("moved")) {			            				
		            	params.put("errorInfo", "请确认您的银行卡是否为储蓄卡");
			    		params.put("errorCode", "0002");
			    		PushSocket.push(params, UUID, "7000","请确认您的银行卡是否为储蓄卡");
			    		PushState.stateByFlag(idcard, "savings", 200,"请确认您的银行卡是否为储蓄卡",flag);
			    		System.out.println("*********************************************请确认您的银行卡是否为储蓄卡");
			    		return params;
		            }
			        Document docs = Jsoup.parse(loginstr1);   
			 	   Element trs = docs.getElementById("dgHistoryTransRecSet");
			 	   System.out.println(trs.html());
				   Elements tr=trs.select("tr");
				
		    	   List list=new ArrayList();
				   for (int i = 1; i < tr.size(); i++) {
					   Map<String,Object>map=new HashMap<String, Object>();
					   System.out.println("开始");
					   Elements td=tr.get(i).select("td");
					   for (int j = 0; j < td.size(); j++) {
						   map.put("expendMoney", "");//交易金额////
						   if(j==0){
							   map.put("dealTime", td.get(j).text());//交易时间///
						   }
						   if(j==2){
							   map.put("incomeMoney", td.get(j).text());//交易金额
						   }
						   if(j==5){
							   map.put("dealDitch", td.get(j).text());//交易渠道////
						   }
						   if(j==4){
							   map.put("balanceAmount", td.get(j).text());//余额
						   }
						   if(j==6){
							   map.put("dealReferral", td.get(j).text());//业务摘要///
						   }
						  
					}
					   map.put("oppositeSideName", "");///
					   map.put("oppositeSideNumber", "");///
					   map.put("currency", "");///
					   list.add(map);
					  
					   
				}
				   logger.warn("########【招商储蓄卡 账单获取完成】########【身份证号：】"+idcard+"【流水信息1loginstr1:】"+loginstr1);
				   if(list.size()==0) {					
					   logger.warn("########【招商储蓄卡 您的账号暂无账单】########【身份证号：】"+idcard);
					   params.put("errorInfo", "您的账号暂无账单");
		    		   params.put("errorCode", "0002");
		    		   PushSocket.push(params, UUID, "7000","您的账号暂无账单");
		    		   PushState.stateByFlag(idcard, "savings", 200,"您的账号暂无账单",flag);
		    		   logger.warn("----招商储蓄卡第二个接口------errorCode："+params.get("errorCode")+"-----errorInfo："+params.get("errorInfo"));
		    		   System.out.println("*********************************************您的账号暂无账单");
		    		   return status;
				   }
				   params.clear();
				   headers.clear();
				   headers.put("accountType","");
				   headers.put("openBranch","");
				   headers.put("openTime","");

				   params.put("billMes", list);
				   params.put("baseMes", headers);
				   params.put("IDNumber", idcard);
				   params.put("bankName", "中国招商银行");
				   params.put("cardNumber",numbe);
				   params.put("userName", userName);
				   System.out.println(JSONObject.fromObject(params));
				   Thread.sleep(1000);
				   
				   PushSocket.push(params, UUID, "6000","招商储蓄卡数据获取成功");
				   logger.warn("########【招商储蓄卡   数据获取成功】########【身份证号：】"+idcard);
				   Resttemplate resttemplate=new Resttemplate();
				   logger.warn("########【招商储蓄卡  开始推送】#"+JSONObject.fromObject(params)+"#####【身份证号：】"+idcard);
				   status=	resttemplate.SendMessage(JSONObject.fromObject(params), application.sendip+"/HSDC/savings/authentication",idcard,UUID);
				   logger.warn("########【招商储蓄卡推送完成    身份证号：】"+idcard+"数据中心返回结果："+status.toString());
				   System.out.println(status);
	    		}catch(Exception e) {
	    			logger.warn("########【招商储蓄卡  进入try-catch  网络异常，认证失败】########【身份证号：】"+idcard);
	    			logger.error("招商储蓄卡", e);
	    			params.put("errorInfo", "网络异常,请重试！！");
	    			params.put("errorCode", "0003");
	    			PushSocket.push(params, UUID, "9000","网络异常，认证失败");
	    			PushState.stateByFlag(idcard, "savings", 200,"网络异常，认证失败",flag);
	    			logger.warn("----招商储蓄卡第二个接口------errorCode："+params.get("errorCode")+"-----errorInfo："+params.get("errorInfo"));
	    		}	    		
	    	}	    	
			 return status;
		 }
		 
		 public static List<Map> yuefen() {
				List list = new ArrayList();

				for (int i = -5; i < 1; i++) {

					SimpleDateFormat matter = new SimpleDateFormat("yyyyMM");
					SimpleDateFormat mattery = new SimpleDateFormat("yyyy");
					SimpleDateFormat matterm = new SimpleDateFormat("MM");
					Calendar calendar = Calendar.getInstance();
					// 将calendar装换为Date类型
					Date date = calendar.getTime();
					// 将date类型转换为BigDecimal类型（该类型对应oracle中的number类型）
					BigDecimal time01 = new BigDecimal(matter.format(date));
					// 获取当前时间的前6个月
					calendar.add(Calendar.MONTH, i);
					Date date02 = calendar.getTime();
					BigDecimal time02 = new BigDecimal(matter.format(date02));
					String s1 = mattery.format(date02);
					String s2 = matterm.format(date02);
					String end = getLastDayOfMonth(Integer.valueOf(s1), Integer.valueOf(s2));
					Map map = new HashMap();
					map.put("begin", s1+s2);
					map.put("end", end);
					list.add(map);

				}
				return list;

			}
			public static String getLastDayOfMonth(int year, int month) {
				Calendar cal = Calendar.getInstance();
				// 设置年份
				cal.set(Calendar.YEAR, year);
				// 设置月份
				cal.set(Calendar.MONTH, month - 1);
				// 获取某月最大天数
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				// 设置日历中月份的最大天数
				cal.set(Calendar.DAY_OF_MONTH, lastDay);
				// 格式化日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String lastDayOfMonth = sdf.format(cal.getTime());

				return lastDayOfMonth;
			}
}
