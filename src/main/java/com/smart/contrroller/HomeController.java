package com.smart.contrroller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import com.smart.duo.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@Controller
public class HomeController {
	@Autowired
	private PasswordEncoder passwordEncoder;

	
	@RequestMapping("/home")
	public String home(Model m)
	{
		m.addAttribute("homename","Home Smart Contact Manager");
		return "home";
	}
	
	
	@RequestMapping("/about")
	public String about(Model m2)
	{
		m2.addAttribute("aboutname","About Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model m3)
	{
		m3.addAttribute("title","signup Smart Contact Manager");
		m3.addAttribute("user",new User());
		return "signup";
	}
	//handler for registering user
	
	
	@Autowired
	private UserRepository userrepo;
	
	@RequestMapping(value = "/do_register" ,method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement" , defaultValue="false") boolean agreement, Model model4, HttpSession session )
	{
		
		try {
		if(!agreement)
		{
			System.out.println("you have not agred the term and conditions");
			throw new Exception("you have not agred the term and conditions");
		}
//		validation
		if(result.hasErrors())
		{
			System.out.println("ERROR" +result.toString());
			model4.addAttribute("user", user);
			return "signup";
		}
		
		
		user.setRole("ROLE_USER");
		user.setEnable(true);
		user.setImageurl("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		System.out.println("Agreement"+ agreement);
		System.out.println("User"+ user);

		
	User demo=this.userrepo.save(user);
		
		model4.addAttribute("user",new User());
		session.setAttribute("message", new Message("Successfully Register !!","alert-success"));
		
		

		
		}catch(Exception e)
		{
			e.printStackTrace();
			model4.addAttribute("user",user);
			session.setAttribute("message", new Message("something went wrong !!"+e.getMessage(),"alert-danger"));
		}
		return "signup";
		
	}
	
	//custom login
	@GetMapping("/login")
	public String customlogin(Model model)
	{
		model.addAttribute("title","login smart contact manager");
		return "login";
	}
	
//	----------------------------------------
//	@GetMapping("/demo")
//	public String d(Model moo,Principal p)
//	{
//		String uname= p.getName();
//		System.out.println("username is : "+uname);
//	User user=userrepo.getUserByUserName(uname);
//	moo.addAttribute("u", user);
//		return "demo";
//	}

}
