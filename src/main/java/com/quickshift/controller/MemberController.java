package com.quickshift.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	@GetMapping("/memberLogin")
	public String showMemberLogin() {
		return "memberLogin";
	}

}
