package com.kakaopay.project;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.Message;
import com.kakaopay.exception.NotValidTokenException;

import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor
class ApplicationTests {
    @Autowired
    protected MockMvc mockMvc;
    
    //private final UserJpaRepo userJpaRepo;
    
    public static void assertEqualsResCode(MvcResult result, String expectCode) throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException {
		String content = result.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(content, Map.class);
		
		String resCd = (String) map.get("resCd");
		assertTrue(expectCode.equals(resCd));
    }
    
    public static Object getResVal(MvcResult result, String key) throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException {
		String content = result.getResponse().getContentAsString();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(content, Map.class);
		
		return map.get(key);
    }
	
	@Test
	@DisplayName("TC01 : X-USER-ID가 숫자형태 아닐 때 문제 발생 확인")
	void TC01() throws Exception {
		MvcResult result = 
				mockMvc.perform(post("/token")
				.header("X-USER-ID", "123A")		// 문자포함
				.header("X-ROOM-ID", "ABC")
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertEqualsResCode(result, Message.M01.getCd());
	}
	
	@Test
	@DisplayName("TC02 : X-ROOM-ID가 문자형태 아닐 때 문제 발생 확인")
	void TC02() throws Exception {
		MvcResult result = 
				mockMvc.perform(post("/token")
				.header("X-USER-ID", "123")
				.header("X-ROOM-ID", "ABC1")		// 숫자포함
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertEqualsResCode(result, Message.M02.getCd());
	}
	
	@Test
	@DisplayName("TC03 : X-USER-ID값이 없을 때 문제 발생 확인")
	void TC03() throws Exception {
		MvcResult result = 
				mockMvc.perform(post("/token")
//				.header("X-USER-ID", "123")			// X-USER-ID 없음
				.header("X-ROOM-ID", "ABC")		
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertEqualsResCode(result, Message.M03.getCd());
	}

	@Test
	@DisplayName("TC04 : X-ROOM-ID값이 없을 때 문제 발생 확인")
	void TC04() throws Exception {
		MvcResult result =
				mockMvc.perform(post("/token")
				.header("X-USER-ID", "123")			
//				.header("X-ROOM-ID", "ABC1")		// X-ROOM-ID 없음
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertEqualsResCode(result, Message.M04.getCd());
	}
	
	@Test
	@DisplayName("TC10 : 요청 바디에, amt없을 때 문제 발생 확인")
	void TC10() throws Exception {
		MvcResult result =
				mockMvc.perform(post("/token")
				.header("X-USER-ID", "123")			
				.header("X-ROOM-ID", "ABC")
//				.content("{\"amt\": 10000, \"personNum\": 5}")
				.content("{\"amt123\": 10000, \"personNum\": 5}")			// amt 없음
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		assertEqualsResCode(result, Message.M10.getCd());
	}

	@Test
	@DisplayName("TC20 : 첫번째 유저가 금액을 뿌리고, 두번째 유저가 두번의 받기 시도하면 예외 발생")
	void TC20() throws Exception {
		// 123번 유저가 5명이 가져갈 10000원을 뿌림
		MvcResult sprayReq = 
				mockMvc.perform(post("/token").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC")
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		// 124번 유저가 첫번째 받기 요청
		String tokenStr = (String)getResVal(sprayReq, "token");
		
		mockMvc.perform(put("/token" + "/" + tokenStr).header("X-USER-ID", "124").header("X-ROOM-ID", "ABC"))
			   .andExpect(status().isOk())
			   .andReturn();
		
		// 124번 유저가 첫번째 받기 요청
		mockMvc.perform(put("/token" + "/" + tokenStr).header("X-USER-ID", "124").header("X-ROOM-ID", "ABC"))
			   .andExpect(status().isBadRequest())
			   .andReturn();
	}
	
	@Test
	@DisplayName("TC21 : 한 유저가 뿌리고 받기를 연속적으로 하면 예외 발생")
	void TC21() throws Exception {
		// 123번 유저가 5명이 가져갈 10000원을 뿌림
		MvcResult sprayReq = 
				mockMvc.perform(post("/token").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC")
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		// 123번 유저가 받기 요청
		String tokenStr = (String)getResVal(sprayReq, "token");
		mockMvc.perform(put("/token" + "/" + tokenStr).header("X-USER-ID", "123").header("X-ROOM-ID", "ABC"))
			   .andExpect(status().isBadRequest())
			   .andReturn();
	}
	
	@Test
	@DisplayName("TC22 : 한 유저가 뿌리고 다른 방에 있는 유저가 받기를 하면 예외 발생")
	void TC22() throws Exception {
		// ABC방의 123번 유저가 5명이 가져갈 10000원을 뿌림
		MvcResult sprayReq = 
				mockMvc.perform(post("/token").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC")
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		// 다른 방인 ABD방의 124번 유저가 첫번째 받기 요청
		String tokenStr = (String)getResVal(sprayReq, "token");
		mockMvc.perform(put("/token" + "/" + tokenStr).header("X-USER-ID", "124").header("X-ROOM-ID", "ABD"))
			   .andExpect(status().isBadRequest())
			   .andReturn();
	}
	
	@Test
	@DisplayName("TC23 : 첫번째 유저가 뿌리고, 두번재 유저가 10분이 넘은 건을 받기 요청하면 예외 발생")
	void TC23() throws Exception {
		// testRegDt
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, -10);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String testRegDt = sdf.format(c.getTime());
		
		// ABC방의 123번 유저가 5명이 가져갈 10000원을 뿌림, 이 때 테스트를 위해 등록시간을 10분 전으로 함
		MvcResult sprayReq = 
				mockMvc.perform(post("/token").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC")
				//.content("{\"amt\": 10000, \"personNum\": 5}")
				.content("{\"amt\": 10000, \"personNum\": 5, \"testRegDt\": \"" + testRegDt + "\"}")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		// ABC방의 124번 유저가 10분 지난 건을 받기 요청
		String tokenStr = (String)getResVal(sprayReq, "token");
		mockMvc.perform(put("/token" + "/" + tokenStr).header("X-USER-ID", "124").header("X-ROOM-ID", "ABC"))
			   .andExpect(status().isBadRequest())
			   .andReturn();
	}
	
	@Test
	@DisplayName("TC30 : 첫번째 유저가 뿌리고, 첫번째와 다른 두번재 유저가 조회하면 예외 발생")
	void TC30() throws Exception {
		// ABC방의 123번 유저가 5명이 가져갈 10000원을 뿌림
		MvcResult sprayReq = 
				mockMvc.perform(post("/token").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC")
				.content("{\"amt\": 10000, \"personNum\": 5}")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		
		// ABC방의 124번 유저가 조회를 요청함
		String tokenStr = (String)getResVal(sprayReq, "token");
		mockMvc.perform(get("/token" + "/" + tokenStr).header("X-USER-ID", "124").header("X-ROOM-ID", "ABC"))
			   .andExpect(status().isBadRequest())
			   .andReturn();
	}
	
	@Test
	@DisplayName("TC31 : 임의의 토큰을 요청하면 예외발생")
	void TC31() throws Exception {
		// ABC방의 123번 유저가 생성불가 토큰 "-_-"을 생성하여 전송
		mockMvc.perform(get("/token/-_-").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC"))
		.andExpect(status().isBadRequest())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof NotValidTokenException));
	}
	
	@Test
	@DisplayName("TC32 : 첫번째 유저가 뿌리고, 같은 유저가 7일이 지난 건을 조회요청하면 예외 발생")
	void TC32() throws Exception {
		// testRegDt
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String testRegDt = sdf.format(c.getTime());
		
		// ABC방의 123번 유저가 5명이 가져갈 10000원을 뿌림, 이 때 테스트를 위해 등록시간을 7일 전으로 함
		MvcResult sprayReq = 
				mockMvc.perform(post("/token").header("X-USER-ID", "123").header("X-ROOM-ID", "ABC")
				.content("{\"amt\": 10000, \"personNum\": 5, \"testRegDt\": \"" + testRegDt + "\"}")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		// ABC방의 123번 유저가 7일 지난 건을 조회 요청
		String tokenStr = (String)getResVal(sprayReq, "token");
		mockMvc.perform(get("/token" + "/" + tokenStr).header("X-USER-ID", "123").header("X-ROOM-ID", "ABC"))
			   .andExpect(status().isBadRequest())
			   .andReturn();
	}
}
