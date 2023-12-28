package com.bn.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bn.model.CityVo;
import com.bn.model.ContentVo;
import com.bn.model.DtailPlanVo;
import com.bn.model.PlanVo;
import com.bn.service.DbService;
import com.bn.service.PlanService;

import lombok.extern.log4j.Log4j;



@Controller
@Log4j
@PropertySource("classpath:/config/props/apiKey.properties")
public class PlanController {
	
	@Inject
	private PlanService pservice;
	
	@Value("${NAVER_MAPS_KEY}")
	private String NAVER_MAPS_KEY;
	
	@Value("${NAVER_MAPS_SECRET_KEY}")
	private String NAVER_MAPS_SECRET_KEY;
	
	@Inject
	private DbService dService;
	
	private PlanVo pvo;
	
	private ContentVo cvo;
	
	private CityVo cityvo;

	@GetMapping("/plan")
	public String plan(@RequestParam String search,Model model) {
		model.addAttribute("NAVER_MAPS_KEY", NAVER_MAPS_KEY);
		model.addAttribute("NAVER_MAPS_SECRET_KEY", NAVER_MAPS_SECRET_KEY);
		cityvo=pservice.cityloc(search);
		model.addAttribute("cityvo",cityvo);
		System.out.println(cityvo.getLATITUDE());
		return "plan";
	}
	
	@ResponseBody
	@RequestMapping("/plan")
	public String saveplan(@RequestBody PlanVo vo) {
		System.out.println(vo);
		int n=pservice.insert(vo);
		String x="plan";
	
		
		return x;
	}
	@ResponseBody
	@RequestMapping("/myplan")
	public ModelMap myplan(@RequestParam("p_id") int p_id) {
		System.out.println(p_id);
		ModelMap map=new ModelMap();
		pvo=pservice.selectPlan(p_id);
		map.addAttribute("vo",pvo);
		return map;
	}
	@ResponseBody
	@RequestMapping("/memberplan")
	public ModelMap memberplan(@RequestParam("m_id") int m_id) {
		System.out.println(m_id);
		ModelMap map=new ModelMap();
		List<PlanVo> lvo=pservice.selectAll(m_id);
		map.addAttribute("lvo",lvo);
		return map;
	}

	@ResponseBody
	@RequestMapping("/tour")
	public ModelMap showinfo (@RequestParam String x,@RequestParam String y,@RequestParam(required=false)String cat, @RequestParam(required=false)String ctype,HttpSession session) {
		ModelMap map=new ModelMap();
		try {
		Map<String,Object> cd=new HashMap<>();
		double mapx=Double.parseDouble(x);
		double mapy=Double.parseDouble(y);
		cd.put("mapx",mapx);
		cd.put("mapy",mapy);
		cd.put("cat", cat);
		cd.put("ctype", ctype);
		List<ContentVo>nd=dService.searchInRange(cd);
		map.addAttribute("contentList",nd);
		session.setAttribute("contentList",nd);
		System.out.println("controller:" +nd.size());
		System.out.println(nd.toString());
		//cd.put("contentList.get(ContentVo).size", map.get(ContentVo));
		}catch (NumberFormatException e) {
	        // �닽�옄 蹂��솚 以� �삁�쇅 諛쒖깮 �떆 泥섎━
	        map.addAttribute("error", "Invalid coordinates. Please provide valid numeric values for x and y.");
	        e.printStackTrace(); // �삁�쇅 濡쒓렇 異쒕젰 (媛쒕컻 以묒뿉留� �궗�슜, �슫�쁺�뿉�꽌�뒗 濡쒓퉭 �떆�뒪�뀥 �솢�슜)
	    } catch (Exception e) {
	        // �떎瑜� �삁�쇅 諛쒖깮 �떆 泥섎━
	        map.addAttribute("error", "An unexpected error occurred.");
	        e.printStackTrace(); // �삁�쇅 濡쒓렇 異쒕젰 (媛쒕컻 以묒뿉留� �궗�슜, �슫�쁺�뿉�꽌�뒗 濡쒓퉭 �떆�뒪�뀥 �솢�슜)
	    }
		return map;
	}
	
	


	
    @ResponseBody
    @PostMapping("/registercontent")
	public int registcontent(@RequestParam ContentVo cvo) {
    	int n=dService.insertdb(cvo);
    	
    	return n;
    }
    @ResponseBody
    @PostMapping("/dpsave")
    public int dtailplan(@RequestParam DtailPlanVo vo) {
    	int n=pservice.insertDp(vo);
    	
    	return n;
    }
    @ResponseBody
    @PostMapping("/dpretrieve")
    public int dtailplanretrieve(@RequestParam DtailPlanVo vo) {
    	int n=pservice.insertDp(vo);
    	
    	return n;
    }

    @ResponseBody
    @GetMapping("/Csearch")
    public String[] csearch(@RequestParam String keyword, HttpSession session) {
        List<ContentVo> voList = (List<ContentVo>) session.getAttribute("contentList");

        // 결과를 저장할 리스트
        List<String> selectedContents = new ArrayList<>();

        // 원하는 검색 결과 개수
        int count = 0;

        // voList를 순회하면서 검색 키워드와 일치하는 결과를 찾음
        for (ContentVo content : voList) {
            // 예시: content의 title이나 다른 필드에서 keyword와 비교하여 조건을 만족하는 경우 추가
            if (content.getTitle().contains(keyword)) {
                selectedContents.add(content.getTitle());
                count++;
                // 원하는 개수만큼 결과를 찾았으면 종료
                if (count >= 5) {
                    break;
                }
            }
        }
        String[] resultArray=null;
        // List<String>을 String[]로 변환
        try {
        resultArray = selectedContents.toArray(new String[0]);
        }catch(NullPointerException e) {
        	
        }
        return resultArray;
    }
 

    @GetMapping("/NewFile")
    public String go() {
    	
    	
    	return "NewFile";
    }

}
