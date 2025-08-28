package com.smart.contrroller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.duo.ContactRepository;
import com.smart.duo.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {

    private final HomeController homeController;
	@Autowired
	private UserRepository us;
	@Autowired
	private ContactRepository contactrepo;


    UserController(HomeController homeController) {
        this.homeController = homeController;
    }
	
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal p)
	{
	
		String n = p.getName();
		System.out.println(n);
		User user=us.getUserByUserName(n);
		model.addAttribute("uno", user);
		return "normal/user_dashboard";
	}


//------------------------
	@GetMapping("/process")
	public String dash(Model model)
	{
		model.addAttribute("contact", new Contact());
		return "normal/add-contact";
	}
//	--------------------save data using form
	@PostMapping("/process-contact")
	public String contact(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,Principal p)
			throws Exception{
		if(file.isEmpty())
		{
			System.out.println("image not uploaded");
		}
		else
		{

			contact.setImage(file.getOriginalFilename());
			File savefile=new ClassPathResource("static/img").getFile();
		Path path=	Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path ,StandardCopyOption.REPLACE_EXISTING);
			System.out.println("image is uploaded");
		}
		System.out.println(contact);
		String user =p.getName();
		 User useme=us.getUserByUserName(user);
		 contact.setUser(useme); 
		 useme.getContacts().add(contact);
		 
		 us.save(useme);
		 System.out.println("data save");
		
		return "/normal/add-contact";
	}
	// show contact 
	@GetMapping("/show-contact")
	public String showcontact(Model m,Principal p)
	{
		m.addAttribute("title","show-contact");
		String uname=p.getName();
		User user = this.us.getUserByUserName(uname);
	List<Contact> contacts=	this.contactrepo.findContactsByUser(user.getId());
	
	m.addAttribute("contacts",contacts);
	
		return "normal/show_contact";
	}
	
//	----------delete
	@GetMapping("/delete/{cid}")
	public String delete(@PathVariable("cid") Integer cId,Model model)
	{
	Optional<Contact> findbyid=	this.contactrepo.findById(cId);
	Contact contact=findbyid.get();

	contact.setUser(null);
	this.contactrepo.delete(contact);
//	it ok but try diff
//	----------- for that add parameter Principle and add Contact constuct paramete rophanRemoval = true if user is null they remove automatically
//	String uname=p.getName();
//	User user = this.us.getUserByUserName(uname);
//	user.getContacts().remove(contact);
	
	System.out.println("contact delete successfully...!");
	
		return "redirect:/user/show-contact";
	}
//	--------update

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model)
	{
	Optional<Contact> contact=	this.contactrepo.findById(cid);
	contact.get();
	 model.addAttribute("title", "Update Contact");
		model.addAttribute("contact",contact);
		return "normal/update-form";
	}
//----------------update handler
	@PostMapping("/process-update")
	public String updatehandle(@ModelAttribute Contact contact,Principal p)
	{
		
		String username =p.getName();
		User user= this.us.getUserByUserName(username);
		contact.setUser(user);
		this.contactrepo.save(contact);
		System.out.println("Contact Id :"+contact.getcId());
		System.out.println("Contact Name :"+contact.getName());
		return "normal/show_contact";
	}



	
}

