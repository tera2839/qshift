package com.quickshift.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quickshift.Session.AdminSession;
import com.quickshift.entity.Member;
import com.quickshift.entity.RequestCalendar;
import com.quickshift.entity.Store;
import com.quickshift.entity.Timeplan;
import com.quickshift.form.AddStoreForm;
import com.quickshift.form.AdminLoginForm;
import com.quickshift.form.CreateAccountForm;
import com.quickshift.security.CustomSecurityContextRepository;
import com.quickshift.service.AllService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminSession session;
	private final AllService service;
	private final AuthenticationManager authenticationManager; 
	
	@GetMapping("/")
	public String showIndex(
			@ModelAttribute
			AdminLoginForm form
			) {
		return "index";
	}
	  


	@PostMapping("/completeLogin")
	public String completeLogin(
	        @Valid @ModelAttribute AdminLoginForm form,
	        BindingResult br,
	        HttpServletRequest request,  // HttpServletRequestを引数に追加
	        HttpServletResponse response,
	        Model model // HttpServletResponseを引数に追加
	) {
	    if (br.hasErrors()) {
	        return "index";  // バリデーションエラーの場合はログイン画面を再表示
	    }

	    // Spring Security の認証機能を使って認証を実行
	    try {
	        UsernamePasswordAuthenticationToken authenticationToken = 
	            new UsernamePasswordAuthenticationToken(form.getMail(), form.getPass());

	        // 認証を実行
	        Authentication authentication = authenticationManager.authenticate(authenticationToken);
	        System.out.println(authentication);

	        // 認証情報を SecurityContext に保存
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // カスタム SecurityContextRepository を使ってセキュリティコンテキストをセッションに保存
	        SecurityContext securityContext = SecurityContextHolder.getContext();
	        CustomSecurityContextRepository securityContextRepository = new CustomSecurityContextRepository();
	        securityContextRepository.saveContext(securityContext, request, response);  // カスタムリポジトリに保存

	        // 認証が成功した場合
	        if (authentication.isAuthenticated()) {
	            System.out.println("Authenticated user: " + authentication.getName());
	        } else {
	            System.out.println("No authenticated user.");
	        }


	        return "redirect:/selectStore";  // ログイン後のリダイレクト先
	    } catch (AuthenticationException e) {
	        br.rejectValue("mail", null, "メールアドレスもしくはパスワードが間違っています");
	        return "index";  // 認証失敗の場合は再度ログイン画面を表示
	    }
	}
//	@PostMapping("/completeLogin")//上の元のコード
//	public String completeLogin(
//			@Valid @ModelAttribute
//			AdminLoginForm form,
//			BindingResult br
//			) {
//		
//		if(!service.isCanLogin(form.getMail(), form.getPass())) {
//			br.rejectValue("mail", null, "メールアドレスもしくはパスワード違います");
//			return "index";
//		}
//		if(br.hasErrors()) {
//			return "index";
//		}
//		
//		session.setAdmin(service.findByAdminMail(form.getMail()));
//		return "redirect:/selectStore";
//	}
	
	@GetMapping("/selectStore")
	public String showSelectStore(Model model) {
		
		List<Store> stores = service.findStoreByAdmin(session.getAdmin());
		model.addAttribute("stores", stores);
		session.setStore(null);
		
		return "selectStore";
	}
	
	@RequestMapping("/adminHome")
	public String showAdminHome(
			@RequestParam(value = "id", required = false) String id
			) {
		
		Store store = service.findByStoreId(Long.parseLong(id));
		session.setStore(store);
		
		return "adminHome";
	}
	/*------------------------------------------------------------
	  シフト作成
	 ------------------------------------------------------------*/
	@PostMapping("/managementMember")
	public String showManagementMember(
			Model model
			) {
		
		List<Member> members = service.findMemberByStore(session.getStore());
		model.addAttribute("members", members);
		
		return "managementMember"; 
	}
	
	@PostMapping("/completeManagementMember")
	public String completeManagementMember(
			@RequestParam("name") String[] names
			) {
		
		List<Member> members = service.findMemberByStore(session.getStore());
		
		for(Member member : members) {
			
			service.updateMemberName(member.getMemberId(), null);
		}
		
		for(int i = 0; i < names.length; i++) {
			
			Member member = members.get(i);
			service.updateMemberName(member.getMemberId(), names[i]);
		}
		
		return "redirect:/timeplan";
	}
	
	@GetMapping("/timeplan")
	public String showMTimePlan(
			Model model
			) {
		
		List<Timeplan> timeplans = service.findTimeplanByStore(session.getStore());
		model.addAttribute("timeplans", timeplans);
		
		return "timeplan";
	}
	
	@PostMapping("/yearAndMonth")
	public String showYearAndMonth(
			@RequestParam("values") String[] values
			) {
		
		List<Timeplan> plans = service.findTimeplanByStore(session.getStore());
		
		for(Timeplan plan : plans) {
			
			service.updateTimeplan(plan.getPlanId(), null, null, null);
		}
		
		for(int i = 0; i < values.length; i++) {
			
			String name = "";
			String fromTime = "";
			String toTime = "";
			
			String[] str =  values[i].split(":");
			
			if(str.length == 3) {
				name = str[0];
				fromTime = str[1];
				toTime = str[2];
				
				Timeplan plan = plans.get(i);
				
				service.updateTimeplan(plan.getPlanId(), name, fromTime, toTime);
			}
		}
		
		session.setYear(null);
		session.setMonth(null);
		
		return "yearAndMonth";
	}
	
	@PostMapping("/completeYearAndMonth")
	public String completeYearAndMonth(
			@RequestParam("year") String year,
			@RequestParam("month") String month
			) {
		
		session.setYear(year);
		session.setMonth(month);
		
		return "redirect:/requestCalendar";
	}
	
	@GetMapping("/requestCalendar")
	public String showRequestCalendar(
			Model model
			) {
		
		List<Timeplan> timeplans = service.findTimeplanByStore(session.getStore());
		model.addAttribute("timeplans", timeplans);
		model.addAttribute("year", session.getYear());
		model.addAttribute("month", session.getMonth());
		
		return "requestCalendar";
	}
	
	@PostMapping("confirmRequestCalendar")
	public String showConfirmRequestCalendar(
			@RequestParam("requestShift") String[] shifts,
			Model model
			) {
		
		List<Timeplan> timeplans = service.findTimeplanByStore(session.getStore());
		model.addAttribute("timeplans", timeplans);
		model.addAttribute("year", session.getYear());
		model.addAttribute("month", session.getMonth());
		model.addAttribute("shifts", shifts);
		
		return "confirmRequestCalendar";
	}
	
	@PostMapping("/completeRequestCalendar")
	public String completeRequestCalendar(
			@RequestParam("requestShift")String[] shifts
			) {
		
		for(int i = 0; i < shifts.length; i++) {
			
			String shift = shifts[i];
			String[] str = shift.split(":");
			Long plan = Long.parseLong(str[0]);
			String date = str[1];
			int num = Integer.parseInt(str[2]);
			
			RequestCalendar request = new RequestCalendar();
			request.setTimeplan(service.findByTimeplanId(plan));
			request.setDate(date);
			request.setNum(num);
			request.setRequestYear(session.getYear());
			request.setRequestMonth(session.getMonth());
			request.setStore(session.getStore());
			
			service.saveRequestCalendar(request);
			
			}
		return "redirect:/resultRequestCalendar";
	}
	
	@GetMapping("/resultRequestCalendar")
	public String showResultRequestCalendar() {
		return "resultRequestCalendar";
	}
	/*------------------------------------------------------------
	  シフト編集
	 ------------------------------------------------------------*/
	@PostMapping("/adminEditHome")
	public String showAdminEditHome() {
		return "adminEditHome";
	}
	/*------------------------------------------------------------
	  アカウント作成
	 ------------------------------------------------------------*/
	@GetMapping("/createAccount")
	public String showCreateAccount(
			@ModelAttribute
			CreateAccountForm form
			) {
		return "createAccount";
	}
	@PostMapping("/confirmCreateAccount")
	public String showConfirmCreateAccount(
			@Valid @ModelAttribute
			CreateAccountForm form,
			BindingResult br
			) {
		
		if(!service.matchPass(form.getPass1(), form.getPass2())) {
			br.rejectValue("pass1", null, "パスワードが一致しません");
		}
		if(service.existMail(form.getMail())) {
			br.rejectValue("mail", null, "メールアドレスが既に存在しています");
		}
		if(br.hasErrors()) {
			return "createAccount";
		}
		return "confirmCreateAccount";
	}
	@PostMapping("/completeCreateAccount")
	public String completeCreateAccount(
			@Valid @ModelAttribute
			CreateAccountForm form,
			BindingResult br
			) {
		
		if(!service.matchPass(form.getPass1(), form.getPass2())) {
			br.rejectValue("pass1", null, "パスワードが一致しません");
		}
		if(service.existMail(form.getMail())) {
			br.rejectValue("mail", null, "メールアドレスが既に存在しています");
		}
		if(br.hasErrors()) {
			return "createAccount";
		}
		
		form.setPass1(service.Encoder(form.getPass1()));//passハッシュ化していれる
		form.setPass2(service.Encoder(form.getPass2()));
		
		service.saveAdmin(form);
		
		return "redirect:/resultCreateAccount";
	}
	@GetMapping("/resultCreateAccount")
	public String showResultCreateAccount() {
		return "resultCreateAccount";
	}
	
	/*------------------------------------------------------------
	  店舗新規登録
	 ------------------------------------------------------------*/
	@GetMapping("/addStore")
	public String showAddStore(
			@ModelAttribute
			AddStoreForm form
			) {
		return "addStore";
	}
	@PostMapping("/confirmAddStore")
	public String showConfirmAddStore(
			@Valid @ModelAttribute
			AddStoreForm form,
			BindingResult br
			) {
		if(!service.matchPass(form.getPass1(), form.getPass2())) {
			br.rejectValue("pass1", null, "パスワードが一致しません");
		}
		if(br.hasErrors()) {
			return "addStore";
		}
		return "confirmAddStore";
	}
	@PostMapping("/completeAddStore")
	public String completeAddStore(
			@Valid @ModelAttribute
			AddStoreForm form,
			BindingResult br
			) {
		if(!service.matchPass(form.getPass1(), form.getPass2())) {
			br.rejectValue("pass1", null, "パスワードが一致しません");
		}
		if(br.hasErrors()) {
			return "addStore";
		}
		
		service.saveStore(form, session.getAdmin());
		List<Store> stores = service.findStoreByAdmin(session.getAdmin());
		long max = 0;
		for(Store store : stores) {
			long id = store.getStoreId();
			if(id > max) {
				max = id;
			}
		}
		Store store = service.findByStoreId(max);
		
		for(int i = 0; i < 15; i++) {
			Timeplan plan = new Timeplan();
			plan.setStore(store);
			service.saveTimeplan(plan);
		}
		for(int i = 0; i < 30; i++) {
			Member member = new Member();
			member.setStore(store);
			service.saveMember(member);
		}
		
		return "redirect:/resultAddStore";
	}
	@GetMapping("/resultAddStore")
	public String showResultAddStore() {
		return "resultAddStore";
	}
	
	/*------------------------------------------------------------
	シフト締め切り
	------------------------------------------------------------*/
	@PostMapping("/closingHome")
	public String showClosingHome(
			Model model
			) {
		
		model.addAttribute(service.findClosingMonth(session.getStore()));
		
		return "closingHome";
	}
	
	@PostMapping("/closingShift")
	public String showClosingShift() {
		return "closingShift";
	}
}
