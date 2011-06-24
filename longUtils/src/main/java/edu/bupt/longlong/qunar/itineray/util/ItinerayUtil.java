package edu.bupt.longlong.qunar.itineray.util;

import java.util.regex.Pattern;

public class ItinerayUtil {
	public final static Pattern numberPattern = Pattern.compile("\\d+");
	public final static Pattern punctuationPattern = Pattern.compile("[,!，。！？;；]");
		
	public static int normalizeNumber(String number) {
		int num = 0;

		if (numberPattern.matcher(number).matches()) {
			return Integer.parseInt(number);
		}

		return parseChineseNumber(number);
		/*
		if (number.equals("一")) {
			num = 1;
		} else if (number.equals("二")) {
			num = 2;
		} else if (number.equals("三")) {
			num = 3;
		} else if (number.equals("四")) {
			num = 4;
		} else if (number.equals("五")) {
			num = 5;
		} else if (number.equals("六")) {
			num = 6;
		} else if (number.equals("七")) {
			num = 7;
		} else if (number.equals("八")) {
			num = 8;
		} else if (number.equals("九")) {
			num = 9;
		} else if (number.equals("十")) {
			num = 10;
		} else {
			num = 0;
		}
		
		return num;
		*/
	}

	 /** 
     * 把中文数字解析为阿拉伯数字(Integer) 
     * @param chineseNumber 中文数字 
     * @return 阿拉伯数字(Integer),如果是无法识别的中文数字则返回-1 
     */  
    public static int parseChineseNumber(String chineseNumber){  
        chineseNumber=chineseNumber.replace("仟", "千");  
        chineseNumber=chineseNumber.replace("佰", "百");  
        chineseNumber=chineseNumber.replace("拾", "十");  
        chineseNumber=chineseNumber.replace("玖", "九");  
        chineseNumber=chineseNumber.replace("捌", "八");  
        chineseNumber=chineseNumber.replace("柒", "七");  
        chineseNumber=chineseNumber.replace("陆", "六");  
        chineseNumber=chineseNumber.replace("伍", "五");  
        chineseNumber=chineseNumber.replace("肆", "四");  
        chineseNumber=chineseNumber.replace("叁", "三");  
        chineseNumber=chineseNumber.replace("贰", "二");  
        chineseNumber=chineseNumber.replace("壹", "一");  
        return parseChineseNumber(chineseNumber,1);  
    }  
      
    /** 
     * 把中文数字解析为阿拉伯数字(Integer) 
     * @param preNumber 第二大的进位 
     * @param chineseNumber 中文数字 
     * @return 阿拉伯数字(Integer),如果是无法识别的中文数字则返回-1 
     */  
    private static int parseChineseNumber(String chineseNumber,int preNumber){  
        int ret=0;  
        if(chineseNumber.indexOf("零")==0){  
            int index=0;  
            int end=chineseNumber.length();  
            String prefix=chineseNumber.substring(index+1,end);  
            ret=parseChineseNumber(prefix,1);  
        }else if(chineseNumber.indexOf("亿")!=-1){  
            int index=chineseNumber.indexOf("亿");  
            int end=chineseNumber.length();  
            String prefix=chineseNumber.substring(0,index);  
            if(prefix.length()==0){  
                prefix="一";  
            }  
            String postfix=chineseNumber.substring(index+1,end);  
            ret=parseChineseNumber(prefix,1)*100000000+parseChineseNumber(postfix,10000000);  
        }else if(chineseNumber.indexOf("万")!=-1){  
            int index=chineseNumber.indexOf("万");  
            int end=chineseNumber.length();  
            String prefix=chineseNumber.substring(0,index);  
            if(prefix.length()==0){  
                prefix="一";  
            }  
            String postfix=chineseNumber.substring(index+1,end);  
            ret=parseChineseNumber(prefix,1)*10000+parseChineseNumber(postfix,1000);  
        }else if(chineseNumber.indexOf("千")!=-1){  
            int index=chineseNumber.indexOf("千");  
            int end=chineseNumber.length();  
            String prefix=chineseNumber.substring(0,index);  
            if(prefix.length()==0){  
                prefix="一";  
            }  
            String postfix=chineseNumber.substring(index+1,end);  
            ret=parseChineseNumber(prefix,1)*1000+parseChineseNumber(postfix,100);  
        }else if(chineseNumber.indexOf("百")!=-1){  
            int index=chineseNumber.indexOf("百");  
            int end=chineseNumber.length();  
            String prefix=chineseNumber.substring(0,index);  
            if(prefix.length()==0){  
                prefix="一";  
            }  
            String postfix=chineseNumber.substring(index+1,end);  
            ret=parseChineseNumber(prefix,1)*100+parseChineseNumber(postfix,10);  
        }else if(chineseNumber.indexOf("十")!=-1){  
            int index=chineseNumber.indexOf("十");  
            int end=chineseNumber.length();  
            String prefix=chineseNumber.substring(0,index);  
            if(prefix.length()==0){  
                prefix="一";  
            }  
            String postfix=chineseNumber.substring(index+1,end);  
            ret=parseChineseNumber(prefix,1)*10+parseChineseNumber(postfix,1);  
        }else if(chineseNumber.equals("一")){  
            ret=1*preNumber;  
        }else if (chineseNumber.equals("二")){  
            ret=2*preNumber;  
        }else if (chineseNumber.equals("三")){  
            ret=3*preNumber;      
        }else if (chineseNumber.equals("四")){  
            ret=4*preNumber;  
        }else if (chineseNumber.equals("五")){  
            ret=5*preNumber;  
        }else if (chineseNumber.equals("六")){  
            ret=6*preNumber;  
        }else if (chineseNumber.equals("七")){  
            ret=7*preNumber;  
        }else if (chineseNumber.equals("八")){  
            ret=8*preNumber;  
        }else if (chineseNumber.equals("九")){  
            ret=9*preNumber;  
        }else if (chineseNumber.length()==0){  
            ret=0;  
        }else {  
            ret=-1;  
        }  
        return ret;  
    } 
    
	public static boolean containPunctuation(String content) {
		return punctuationPattern.matcher(content).find();
	}
	
	public static void main(String[] args) {
		String number = "二";

		int num = 0;
		number = "二";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);

		number = "2";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		number = "21";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		number = "十";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		number = "十二";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		number = "二十二";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		number = "零";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		number = "非法字符";
		num = ItinerayUtil.normalizeNumber(number);
		System.out.println("num: " + num);
		
		String str = "你好啊123";
		System.out.println(str.substring(3, str.length()));
	}
}
