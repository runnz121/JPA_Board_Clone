package JPA_Board_Clone.settings;


import JPA_Board_Clone.account.AccountService;
import JPA_Board_Clone.account.CurrentUser;
import JPA_Board_Clone.domain.Account;
import JPA_Board_Clone.domain.Tag;
import JPA_Board_Clone.domain.Zone;
import JPA_Board_Clone.settings.form.*;
import JPA_Board_Clone.settings.validator.NicknameFormValidator;
import JPA_Board_Clone.settings.validator.PasswordFormValidator;
import JPA_Board_Clone.tag.TagRepository;
import JPA_Board_Clone.zone.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    //하위 변수 초기화 모두 thyemleaf form 에 연결
    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile"; //final 선언되어 상수(선언 후 변경 x) 이기 때문에 햇갈리지 말라고 모두 대문자로 적음 https://jdpb.tistory.com/entry/Java-final-%EC%83%81%EC%88%98
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;

    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;

    static final String SETTINGS_ZONES_VIEW_NAME = "settings/zones";
    static final String SETTINGS_ZONES_URL = "/" + SETTINGS_ZONES_VIEW_NAME;

    private final AccountService accountService;
    private final NicknameFormValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm") //이 컨트롤러에 들어오는 요청중 특정 객체에만 바인딩 할때(passwordform 객체에 대해서 이 매소드가 바인딩된다)  https://goodgid.github.io/Spring-MVC-InitBinder/
    public void passwordFormInitBinder(WebDataBinder webDataBinder) // WebDataBinder는 web request parameter를 JavaBean 객체에 바인딩하는 특정한 DataBinder이다.
                                                                    // WebDataBinder는 웹 환경이 필요하지만, Servlet API에 의존적이지 않다.
                                                                    // Servlet API에 의존적인 ServletRequestDataBinder와 같이 특정한 DataBinder를 위한 더 많은 base classs를 제공한다.
                                                                    //https://mycup.tistory.com/154 [IT.FARMER]


    {
        webDataBinder.addValidators(new PasswordFormValidator()); //클래스를 객체로 생성
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes) { // 리다이렉트의 경우 전달 설정 https://galid1.tistory.com/563
        // 현재 account는 detached 상태이다. 한번이라도 영속성 컨텍스트에 들어간 객체 (id값이 있는 상태)
    if(errors.hasErrors()) {
        model.addAttribute(account); //addAttribute("이름", 객체) : 이름을 이용해 객체를 사용할 수 있다.
                                     //addAttribute(객체) : 이름을 지정하지 않을 경우 자동적으로 객체의 클래스명 앞글자를 소문자로 처리해서 이름으로 사용한다. https://all-record.tistory.com/167

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    accountService.updateProfile(account, profile);
    attributes.addFlashAttribute("message","프로필을 수정했습니다." ); //addFlashAttribute : session에 객체 정보 저장, 핸들러 종료시 자동 종료
    return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes redirectAttributes) {

        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        redirectAttributes.addFlashAttribute("message","패스워드를 변경하였습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotificationForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notification.class));
        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_URL)
    public String updateNotification(@CurrentUser Account account, @Valid Notification notification, Errors errors,
                                    Model model, RedirectAttributes redirectAttributes ) {
        if(errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }

        accountService.updateNotification(account, notification);
        redirectAttributes.addFlashAttribute("message","알림 설정을 변경하였습니다");
        return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }
    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream()
                .map(Tag::getTitle) //method reference 표기법 (list -> tag.getTitle(list)) https://tourspace.tistory.com/7
                .collect(Collectors.toList()); //https://futurecreator.github.io/2018/08/26/java-8-streams/
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags)); //writeValueAsString을 통해서 allTags를 string 타입으로 변환

        return SETTINGS_TAGS_VIEW_NAME;

    }

    @PostMapping(SETTINGS_TAGS_URL + "/add")
    @ResponseBody//http reponsebody의 messageconverter에서 변환이 이뤄진 후 쓰여진다(view반환 말고) https://ismydream.tistory.com/140
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title)
                .orElseGet(//optional class 객체가 갖고 있는 실제값이 null 일경우 어떠한값으로 대체해서 return 해줄것인가 , orelseget의 경우 값이 null 경우에만 실행 https://zgundam.tistory.com/174
                        () -> tagRepository.save(Tag.builder()
                .title(tagForm.getTagTitle())
                .build())
                );
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build(); //responsentity : HTTP response를 모두 대표하는 것
    }

    @PostMapping(SETTINGS_TAGS_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title).get();
        if(Objects.isNull(tag)){
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(SETTINGS_ZONES_URL)
    public String updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream()
                .map(Zone::toString)
                .collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return SETTINGS_ZONES_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ZONES_URL + "/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) throws JsonProcessingException {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest()
                    .build();
        }
        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(SETTINGS_ZONES_URL + "/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) throws JsonProcessingException {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest()
                    .build();
        }
        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }

}


