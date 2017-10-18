package banip.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import banip.action.*;
import banip.action.board.*;
import banip.action.exception.*;
import banip.action.reply.*;
import banip.action.user.*;
import banip.exception.IsNotActionURLException;
import banip.util.StringUtil;





@SuppressWarnings("unused")
public class BoardFrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardFrontController() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		Action action;
		try {
			String actionName = new StringUtil().getActionClassName(requestURI);
			action = getAction(actionName);
		} catch (IsNotActionURLException ee) {
			// TODO: handle exception
			action = new WrongURLAction();
		}

		action.execute(request,response);
		printJSON(request,response);
	}
	


	private Action getAction(String command) {
		Action action;
		try {
			System.out.println(command);
			action = (Action) Class.forName(command).newInstance();
		}  catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			action = (Action) new IllegalAccessAction();
		} catch (ClassNotFoundException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			action = (Action) new ImproperURLAction();
		}
		return action;
	}
	
	private void printJSON(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/board/queryJSON.servlet");
		dispatcher.forward(request, response);

	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("httpProtocol", "GET");
		doProcess(request,response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("httpProtocol", "POST");
		doProcess(request,response);
		
	}

}
