package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelExtensionsKt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        // 왜 비어있는 MemberForm 을 넘겨줄까?
        // 타임리프에서 추적할 수 있게 해줌!!! 헐.... 신기하네...
        // MemberForm 필드에 없는 필드를 html 에 호출되면 컴파일 에러까지 뱉어줌!!!
        // JSP 는 그런거 없었는데!!!!
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    // MemberForm 를 왜 사용할까? Member 있는데?
    // 화면과 도메인 밸리데이션이 서로 다르기 때문에 화면용 따로 두는거다
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; // 보통 리다이렉트로 보내준다
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

}
