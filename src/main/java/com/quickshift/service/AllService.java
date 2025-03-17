package com.quickshift.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quickshift.entity.Admin;
import com.quickshift.entity.Member;
import com.quickshift.entity.RequestCalendar;
import com.quickshift.entity.Store;
import com.quickshift.entity.Timeplan;
import com.quickshift.form.AddStoreForm;
import com.quickshift.form.CreateAccountForm;
import com.quickshift.repository.AdminRepository;
import com.quickshift.repository.MemberRepository;
import com.quickshift.repository.RequestCalendarRepository;
import com.quickshift.repository.StoreRepository;
import com.quickshift.repository.TimeplanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllService {
	
	private final AdminRepository adminRep;
	private final StoreRepository storeRep;
	private final MemberRepository memberRep;
	private final TimeplanRepository timeplanRep;
	private final RequestCalendarRepository requestRep;
	private final PasswordEncoder passwordEncoder;
	
	public boolean existMail(String mail) {
		
		return adminRep.findByAdminMail(mail) != null;
	}
	
	public boolean matchPass(String pass1, String pass2) {
		
	    if (pass1 == null || pass2 == null) {
	        return false;
	    }
	    return pass1.equals(pass2);
	}
	
	public boolean isCanLogin(String mail, String pass) {
		
		Admin admin = adminRep.findByAdminMail(mail);
	    //return admin != null && pass.equals(admin.getAdminPass());
		return admin != null && passwordEncoder.matches(pass, admin.getAdminPass());
	}
	/*--------------------------------------------------------
	  Adminテーブル操作
	 --------------------------------------------------------*/
	public Admin findByAdminMail(String mail) {
		return adminRep.findByAdminMail(mail);
	}
	@Transactional
	public void saveAdmin(CreateAccountForm form) {
		
		Admin admin = new Admin();
		admin.setAdminMail(form.getMail());
		admin.setAdminName(form.getName());
		admin.setAdminPass(form.getPass1());
		
		adminRep.save(admin);
	}
	/*--------------------------------------------------------
	  Storeテーブル操作
	 --------------------------------------------------------*/
	public Store findByStoreId(Long id) {
		
		return storeRep.findByStoreId(id);
	}
	
	public List<Store> findStoreByAdmin(Admin admin){
		
		return storeRep.findByAdmin(admin);
	}
	
	@Transactional
	public void saveStore(AddStoreForm form, Admin admin) {
		
		Store store = new Store();
		store.setStoreName(form.getName());
		store.setStorePass(form.getPass1());
		store.setAdmin(admin);
		
		storeRep.save(store);
	}
	/*--------------------------------------------------------
	  Memberテーブル操作
	 --------------------------------------------------------*/
	public List<Member> findMemberByStore(Store store){
		
		return memberRep.findByStore(store);
	}
	
	@Transactional
	public void updateMemberName(Long id, String name) {
		
		memberRep.updateName(id, name);
	}
	
	@Transactional
	public void deleteAllMember(Store store) {
		
		memberRep.deleteAllByStore(store);
	}
	@Transactional
	public void saveMember(Member member) {
		
		memberRep.save(member);
	}
	/*--------------------------------------------------------
	  Timeplanテーブル操作
	 --------------------------------------------------------*/
	public Timeplan findByTimeplanId(Long id) {
		return timeplanRep.findByPlanId(id);
	}
	
	public List<Timeplan> findTimeplanByStore(Store store){
		
		return timeplanRep.findByStore(store);
	}
	
	@Transactional
	public void deleteAllTimeplan(Store store) {
		
		timeplanRep.deleteAllByStore(store);
	}
	
	@Transactional
	public void saveTimeplan(Timeplan timeplan) {
		
		timeplanRep.save(timeplan);
	}
	
	@Transactional
	public void updateTimeplan(Long id, String name, String from, String to) {
		
		timeplanRep.updateName(id, name, from, to);
	}
	/*--------------------------------------------------------
	  RequestCalendarテーブル操作
	 --------------------------------------------------------*/
	@Transactional
	public void saveRequestCalendar(RequestCalendar request) {
		
		requestRep.save(request);
	}
	
	/*--------------------------------------------------------
	  Shiftテーブル操作
	 --------------------------------------------------------*/
	public List<String> findClosingMonth(Store store) {
		
		List<RequestCalendar> requests = requestRep.findByStore(store);
		List<String> months = new ArrayList<String>();
		
		for(RequestCalendar req : requests) {
			
			String str = req.getRequestYear() + ":" + req.getRequestMonth();
			
			if(!months.contains(str)) {
				months.add(str);
			}
		}
		
		return months;
	}
	
	/*--------------------------------------------------------
	  パスワード操作
	 --------------------------------------------------------*/
	
	public String Encoder(String pass) {
      return passwordEncoder.encode(pass);
    }
	
	
}