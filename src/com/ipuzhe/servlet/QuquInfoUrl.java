package com.ipuzhe.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.ipuzhe.data.Persistence;

public class QuquInfoUrl extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	
	@Test
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ypid = request.getParameter("ypid");
		String sccode = request.getParameter("sccode");
		String r1 = request.getParameter("r1");
		String r2 = request.getParameter("r2");
		
		if(ypid == null || sccode == null || r1 == null || r2 == null)
			throw new IllegalArgumentException("参数不满足！");
		
		
		String authParam = "?ypid="+ypid+"&sccode="+sccode+"&r1="+r1+"&r2="+r2;
		
		try {
			Persistence.updateAuthParam(Long.valueOf(ypid), authParam);
			System.out.println("inserd："+authParam);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}

}
