package shenjing.web;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import shenjing.domain.Dsg;
import shenjing.service.ShenjingService;

@Controller
public class ShenjingController {

	@Autowired
	ShenjingService shenjingService;
	
	/**
	 * 存放数据
	 * @return
	 */
	/*@RequestMapping("/")
	public String findall() { // 1

		List<Rdata5> rdata = rtest5Repository.findAll();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rdata.size(); i++) {
			sb.append(rdata.get(i).toString());
			sb.append("<br/>");
		}
		return sb.toString();

	}*/

	@RequestMapping("/")
	public String input(
	        @RequestParam(value = "province",required=true) String province){
	    if(province.equals("44")){
	        return "guangdong";
	    }else if(province.equals("45")){
	        return "guangxi";
	    }else if(province.equals("46")){
	        return "hainan";
	    }else if(province.equals("52")){
	        return "guizhou";
	    }else if(province.equals("53")){
	        return "yunnan";
	    }else{
	        return "inputError";
	    }
	}
    	@RequestMapping("/inputError")
    	public String inputError(){
	    return "inputError";
    	}
	@RequestMapping("/guangdong")
	public String input1(){
		return "guangdong";
	}
	@RequestMapping("/guangxi")
	public String input2() {
	    return "guangxi";
	}
	@RequestMapping("/hainan")
	public String input3(){
		return "hainan";
	}
	@RequestMapping("/guizhou")
	public String input4(){
		return "guizhou";
	}
	@RequestMapping("/yunnan")
	public String input5(){
		return "yunnan";
	}
	
	@RequestMapping("/index")
	public String index(Model model) throws REXPMismatchException { // 1
	    RConnection c = null;
	    try {
	        c = new RConnection();// 调用R
	        c.eval("library(AMORE)");

	        String mp1 = shenjingService.matrix1();
	        double p[][] = c.eval(
		       "matrix(c(" + mp1.substring(0, mp1.length() - 1)
			      + "),11,10,byrow=T)").asDoubleMatrix();
	        double t[] = {2673.5356, 2991.0529, 3393.0057, 3504.8229,
		       3609.4029, 4060.1257, 4399.0168, 4619.4102};
	        Dsg dsg1 = shenjingService.Shenjing("广东", p, 11, 10, t, 4830.1315,
		       c);

	        String mp2 = shenjingService.matrix2();
	        double p2[][] = c.eval(
		       "matrix(c(" + mp2.substring(0, mp2.length() - 1)
			      + "),11,11,byrow=T)").asDoubleMatrix();
	        double t2[] = {509.5791, 576.3197, 679.2197, 753.3871, 836.3691,
		       992.8000, 1112.3222, 1153.8532};
	        Dsg dsg2 = shenjingService.Shenjing("广西", p2, 11, 11, t2,
		       1237.0546, c);

	        String mp3 = shenjingService.matrix3();
	        double p3[][] = c.eval(
		       "matrix(c(" + mp3.substring(0, mp3.length() - 1)
			      + "),11,9,byrow=T)").asDoubleMatrix();
	        double t3[] = {81.6081, 97.6759, 112.1463, 121.7400, 133.7672,
		       158.1660, 185.0762, 210.3110};
	        Dsg dsg3 = shenjingService.Shenjing("海南", p3, 11, 9, t3, 230.9839,
		       c);

	        String mp4 = shenjingService.matrix4();
	        double p4[][] = c.eval(
		       "matrix(c(" + mp4.substring(0, mp4.length() - 1)
			      + "),9,8,byrow=T)").asDoubleMatrix();
	        double t4[] = {669.0929, 679.1767, 763.7931, 835.5267, 944.1282,
		       1046.7188};
	        Dsg dsg4 = shenjingService.Shenjing("贵州", p4, 9, 8, t4, 1126.2695,
		       c);

	        String mp5 = shenjingService.matrix5();
	        double p5[][] = c.eval(
		       "matrix(c(" + mp5.substring(0, mp5.length() - 1)
			      + "),9,15,byrow=T)").asDoubleMatrix();
	        double t5[] = {745.0480, 829.4435, 890.1047, 1003.4069, 1204.0671,
		       1315.8629};
	        Dsg dsg5 = shenjingService.Shenjing("云南", p5, 9, 15, t5, 1459.8074,
		       c);

	        model.addAttribute("dsg1", dsg1);
	        model.addAttribute("dsg2", dsg2);
	        model.addAttribute("dsg3", dsg3);
	        model.addAttribute("dsg4", dsg4);
	        model.addAttribute("dsg5", dsg5);
	    } catch (REngineException e) {
	        e.printStackTrace();
	    } finally {
	        c.close();
	    }

	    return "index";

	}

}
