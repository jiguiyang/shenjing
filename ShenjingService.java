package shenjing.service;

import java.math.BigDecimal;
import java.util.List;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shenjing.dao.*;
import shenjing.domain.*;

@Service
public class ShenjingService {

    	@Autowired
	CsgPowerConsNnDao rtest0Repository;
	@Autowired
	CsgPowerConsNnGdDao rtestRepository;
	@Autowired
	CsgPowerConsNnGxDao rtest2Repository;
	@Autowired
	CsgPowerConsNnHiDao rtest3Repository;
	@Autowired
	CsgPowerConsNnGzDao rtest4Repository;
	@Autowired
	CsgPowerConsNnYnDao rtest5Repository;

	public String matrix1(){
		List<CsgPowerConsNnGd> rdata = rtestRepository.findAll();
		StringBuilder sb =new  StringBuilder();
		for(int i=0;i<rdata.size();i++){
			sb.append(rdata.get(i).toString());
			sb.append(",");
		}	
		return sb.toString();
	}
	public String matrix2(){
		List<CsgPowerConsNnGx> rdata = rtest2Repository.findAll();
		StringBuilder sb =new  StringBuilder();
		for(int i=0;i<rdata.size();i++){
			sb.append(rdata.get(i).toString());
			sb.append(",");
		}	
		return sb.toString();
	}
	public String matrix3(){
		List<CsgPowerConsNnHi> rdata = rtest3Repository.findAll();
		StringBuilder sb =new  StringBuilder();
		for(int i=0;i<rdata.size();i++){
			sb.append(rdata.get(i).toString());
			sb.append(",");
		}	
		return sb.toString();
	}
	public String matrix4(){
		List<CsgPowerConsNnGz> rdata = rtest4Repository.findAll();
		StringBuilder sb =new  StringBuilder();
		for(int i=0;i<rdata.size();i++){
			sb.append(rdata.get(i).toString());
			sb.append(",");
		}	
		return sb.toString();
	}
	public String matrix5(){
		List<CsgPowerConsNnYn> rdata = rtest5Repository.findAll();
		StringBuilder sb =new  StringBuilder();
		for(int i=0;i<rdata.size();i++){
			sb.append(rdata.get(i).toString());
			sb.append(",");
		}	
		return sb.toString();
	}
	
	public Dsg Shenjing(String pro,double p[][], int row, int col, double t[], double tt,RConnection c) throws REXPMismatchException, REngineException {
		
		/**
		 * 矩阵归一化
		 */
		double p0[][] = new double[row][col];
		for (int i = 0; i < col; i++) {
			double max = p[0][i];
			double min = p[0][i];
			for (int jj = 1; jj < row; jj++) {
				if (p[jj][i] > max)
					max = p[jj][i];
				if (p[jj][i] < min)
					min = p[jj][i];
			}
			double a = max - min;
			for (int j = 0; j < row; j++) {
				p0[j][i] = (p[j][i] - min) / a;
			}
		}

		/**
		 * 归一化后的矩阵转化成字符串作为参数，方便调用R函数
		 */
		StringBuilder chulihou = new StringBuilder();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				chulihou.append(p0[i][j] + ",");
			}
		}

		/**
		 * 归一化后的矩阵赋值给p0
		 */
		c.assign(
				"p0",
				c.eval("matrix(c("
						+ chulihou.substring(0, chulihou.length() - 1) + "),"
						+ row + "," + col + ",byrow=T)"));

		/**
		 * 测试数据归一化，并将归一化后的结果赋值给tt0
		 */
		double t0[] = new double[t.length];
		double tmax = t[0];
		double tmin = t[0];
		for (int i = 1; i < t.length; i++) {
			if (t[i] > tmax)
				tmax = t[i];
			if (t[i] < tmin)
				tmin = t[i];
		}
		for (int i = 0; i < t.length; i++) {
			t0[i] = (t[i] - tmin) / (tmax - tmin);
		}
		c.assign("tt0", t0);

		int count = 0;// 训练次数
		double alter = 1;// 训练误差
		double betteralter = 1;// 训练误差用途：若训练次数大于20次，选取20次中最接近真实值的预测值
		double[] y = new double[3];// 存放预测值
		double[] y0 = new double[3];// 存放更好的预测值
		while (Math.abs(alter) > 0.03 && count < 20) {
			/**
			 * 神经网络核心代码部分
			 */
			c.assign(
					"net",
					c.eval("newff(n.neurons = c("
							+ col
							+ ","
							+ col
							+ ",2,1),learning.rate.global=1e-4, momentum.global=0.05,error.criterium=\"LMS\", Stao=NA, hidden.layer=\"tansig\", output.layer=\"purelin\", method=\"ADAPTgdwm\")"));
			c.assign(
					"result",
					c.eval("train(net,p0[1:"
							+ t.length
							+ ",],tt0[1:"
							+ t.length
							+ "],error.criterium=\"LMS\", report=TRUE, show.step=10000, n.shows=5)"));
			y = c.eval(
					"sim(result$net,p0[" + (t.length + 1) + ":" + row + ",])")
					.asDoubles();

			/**
			 * 反归一化
			 */
			for (int i = 0; i < y.length; i++) {
				y[i] = y[i] * t[t.length - 1];
			}

			alter = (y[0] - tt) / tt;// 校验误差

			if (Math.abs(alter) < betteralter) {// 当前训练结果比较好，将值赋给y0
				betteralter = Math.abs(alter);
				for (int i = 0; i < y0.length; i++) {
					BigDecimal b = new BigDecimal(y[i]);
					y0[i] = b.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();//保留四位有效小數
				}
			}

			count++;
		}
		
		/*String[] year = { "2013年的用电量预测： ", "2014年的用电量预测： ", "2015年的用电量预测： " };
		StringBuilder sb2 = new StringBuilder();
		sb2.append("训练次数为：");
		sb2.append(count);
		sb2.append("<br/>");
		for(int i=0;i<y0.length;i++){
			sb2.append(year[i]);
			sb2.append(y0[i]);
			sb2.append("<br/>");
		}*/
		//StringBuilder sb2 = new StringBuilder();
		Dsg dsg = new Dsg();
		dsg.setProvince(pro);
		dsg.setYisan(y0[0]);
		dsg.setYisi(y0[1]);
		dsg.setYiwu(y0[2]);
		/*for(int i=0;i<y0.length;i++){
			sb2.append("<td align=\"center\">");
			sb2.append(y0[i]);
			sb2.append("</td>");
		}*/
		
		//return sb2.toString();
		return dsg;
	}
}
