package com.excelente.geek_soccer.date_convert;

public class Date_Covert {
	public static String Day_ConV(String D_in){
		String D_out = "";
		if(D_in.contains("Sun")){
			D_out = D_in.replace("Sun", "อาทิตย์");
		}else if(D_in.contains("Mon")){
			D_out = D_in.replace("Mon", "จันทร์");
		}else if(D_in.contains("Tue")){
			D_out = D_in.replace("Tue", "อังคาร");
		}else if(D_in.contains("Wed")){
			D_out = D_in.replace("Wed", "พุธ");
		}else if(D_in.contains("Thu")){
			D_out = D_in.replace("Thu", "พฤหัสบดี");
		}else if(D_in.contains("Fri")){
			D_out = D_in.replace("Fri", "ศุกร์");
		}else if(D_in.contains("Sat")){
			D_out = D_in.replace("Sat", "เสาร์");
		}
		return D_out;
	}
	
	public static String Mont_ConV(String M_in){
		String M_out = "";
		if(M_in.contains("Jan")){
			M_out = M_in.replace("Jan", "ม.ค.");
		}else if(M_in.contains("Feb")){
			M_out = M_in.replace("Feb", "ก.พ.");
		}else if(M_in.contains("Mar")){
			M_out = M_in.replace("Mar", "มี.ค.");
		}else if(M_in.contains("Apr")){
			M_out = M_in.replace("Apr", "เม.ย.");
		}else if(M_in.contains("May")){
			M_out = M_in.replace("May", "พ.ค.");
		}else if(M_in.contains("Jun")){
			M_out = M_in.replace("Jun", "มิ.ย.");
		}else if(M_in.contains("Jul")){
			M_out = M_in.replace("Jul", "ก.ค.");
		}else if(M_in.contains("Aug")){
			M_out = M_in.replace("Aug", "ส.ค.");
		}else if(M_in.contains("Sep")){
			M_out = M_in.replace("Sep", "ก.ย.");
		}else if(M_in.contains("Oct")){
			M_out = M_in.replace("Oct", "ต.ค.");
		}else if(M_in.contains("Nov")){
			M_out = M_in.replace("Nov", "พ.ย.");
		}else if(M_in.contains("Dec")){
			M_out = M_in.replace("Dec", "ธ.ค.");
		}
		return M_out;
	}
}
