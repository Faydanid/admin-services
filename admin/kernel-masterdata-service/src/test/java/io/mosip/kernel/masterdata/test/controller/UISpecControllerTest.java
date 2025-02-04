package io.mosip.kernel.masterdata.test.controller;

import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.entity.UISpec;
import io.mosip.kernel.masterdata.repository.UISpecRepository;
import io.mosip.kernel.masterdata.service.UISpecService;
import io.mosip.kernel.masterdata.test.TestBootApplication;
import io.mosip.kernel.masterdata.test.utils.MasterDataTest;
import io.mosip.kernel.masterdata.uispec.dto.UISpecDto;
import io.mosip.kernel.masterdata.uispec.dto.UISpecPublishDto;
import io.mosip.kernel.masterdata.uispec.dto.UISpecResponseDto;
import io.mosip.kernel.masterdata.utils.AuditUtil;

/**
 * 
 * @author Nagarjuna
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootApplication.class)
@AutoConfigureMockMvc
public class UISpecControllerTest {

	@Autowired
	public MockMvc mockMvc;
	
		@MockBean
	private PublisherClient<String,EventModel,HttpHeaders> publisher;
		@MockBean
		private AuditUtil auditUtil;
		
		private ObjectMapper mapper;

	@MockBean
	private UISpecService uiSpecService;

	@MockBean
	private UISpecRepository uiSpecRepository;
	
	private RequestWrapper<UISpecDto> request=new RequestWrapper<UISpecDto>();
	
	@Before
	public void setUp() {

		doNothing().when(auditUtil).auditRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		String json="[{\"order\":1,\"name\":\"ConsentDetails\",\"label\":{\"ara\":\"مافقة\",\"fra\":\"Consentement\",\"eng\":\"Consent\"},\"caption\":{\"ara\":\"موافقة\",\"fra\":\"Consentement\",\"eng\":\"Consent\"},\"fields\":[\"consentText\",\"consent\"],\"layoutTemplate\":null,\"preRegFetchRequired\":false,\"active\":false},{\"order\":2,\"name\":\"DemographicDetails\",\"label\":{\"ara\":\"التفاصيل الديموغرافية\",\"fra\":\"Détails démographiques\",\"eng\":\"Demographic Details\"},\"caption\":{\"ara\":\"التفاصيل الديموغرافية\",\"fra\":\"Détails démographiques\",\"eng\":\"Demographic Details\"},\"fields\":[\"fullName\",\"dateOfBirth\",\"gender\",\"residenceStatus\",\"addressLine1\",\"addressLine2\",\"addressLine3\",\"referenceIdentityNumber\",\"region\",\"province\",\"city\",\"zone\",\"postalCode\",\"phone\",\"email\",\"introducerName\",\"introducerRID\",\"introducerUIN\"],\"layoutTemplate\":null,\"preRegFetchRequired\":true,\"active\":false},{\"order\":3,\"name\":\"DocumentsDetails\",\"label\":{\"ara\":\"تحميل الوثيقة\",\"fra\":\"Des documents\",\"eng\":\"Document Upload\"},\"caption\":{\"ara\":\"وثائق\",\"fra\":\"Des documents\",\"eng\":\"Documents\"},\"fields\":[\"proofOfAddress\",\"proofOfIdentity\",\"proofOfRelationship\",\"proofOfDateOfBirth\",\"proofOfException\",\"proofOfException-1\"],\"layoutTemplate\":null,\"preRegFetchRequired\":false,\"active\":false},{\"order\":4,\"name\":\"BiometricDetails\",\"label\":{\"ara\":\"التفاصيل البيومترية\",\"fra\":\"Détails biométriques\",\"eng\":\"Biometric Details\"},\"caption\":{\"ara\":\"التفاصيل البيومترية\",\"fra\":\"Détails biométriques\",\"eng\":\"Biometric Details\"},\"fields\":[\"individualBiometrics\",\"individualAuthBiometrics\",\"introducerBiometrics\"],\"layoutTemplate\":null,\"preRegFetchRequired\":false,\"active\":false}]";
		JsonNode jn;
		UISpecDto dto=new UISpecDto();
		try {
			jn = mapper.readTree(json);
			dto.setJsonspec(jn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dto.setDescription("screen spec");
		dto.setDomain("reg-client");
		dto.setIdentitySchemaId("1001");
		
		dto.setTitle("screenspec");
		dto.setType("screen");
		request.setRequest(dto);
		
		getLstUISpec();
		
	}
	
	private List<UISpec> getLstUISpec()
	{
		List<UISpec> lst=new ArrayList<>();
		UISpec u=new UISpec();
		u.setCreatedBy("superuser");
		u.setCreatedDateTime(LocalDateTime.now());
		u.setDescription("Test");
		u.setDomain("test");
		u.setVersion(1.0);
		u.setId("test");
		u.setIdentitySchemaId("Test");
		u.setIsActive(true);
		u.setIsDeleted(false);
		u.setJsonSpec("{\"code\":\"value\"}");
		u.setTitle("test");
		u.setType("test");
		lst.add(u);
		return lst;
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void t001defineUISpecTest() throws Exception {
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.post("/uispec").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request))).andReturn(), null);
	}
	@Test
	@WithUserDetails("global-admin")
	public void t002updateUISpecTest() throws Exception {
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.put("/uispec").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)).param("id", "1")).andReturn(), null);
	}

/*	@Test
	@WithUserDetails("global-admin")
	public void defineUISpecTest() throws Exception {
		
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.post("/uispec").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)).andReturn(),null);
		
		//Mockito.when(uiSpecService.defineUISpec(Mockito.any(UISpecDto.class))).thenReturn(new UISpecResponseDto());
	/*	MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.post("/uispec").contentType(MediaType.APPLICATION_JSON).content(
				"{\"id\":\"string\",\"version\":\"string\",\"requesttime\":\"2018-12-17T07:15:06.724Z\",\"request\":"
						+ "{\"title\":\"title\",\"description\":\"test desc\",\"schema\":[{\"id\":\"IDSchemaVersion\",\"inputRequired\":false,"
						+ "\"type\":\"number\",\"minimum\":0,\"maximum\":0,\"description\":\"\",\"label\":[{\"value\":\"IDSchemaVersion\",\"language\":\"eng\"}],\"controlType\":\"none\","
						+ "\"fieldType\":\"default\",\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"Pvt\",\"required\":false},"
						+ "{\"id\":\"UIN\",\"inputRequired\":false,\"type\":\"integer\",\"minimum\":0,\"maximum\":0,\"description\":\"\","
						+ "\"label\":[{\"value\":\"UIN\",\"language\":\"eng\"}],\"controlType\":\"none\",\"fieldType\":\"default\",\"format\":\"none\",\"validators\":[],"
						+ "\"fieldCategory\":\"Pvt\",\"required\":false},{\"id\":\"fullName\",\"inputRequired\":true,\"type\":\"simpleType\","
						+ "\"minimum\":0,\"maximum\":0,\"description\":\"\",\"label\":[{\"value\":\"Full Name\",\"language\":\"eng\"}],\"controlType\":\"textbox\",\"fieldType\":\"default\","
						+ "\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"Pvt\",\"required\":true}],"
						+ "\"effectiveFrom\":\"2018-12-17T07:15:06.724Z\"}}")).andReturn(),null);
				//.andExpect(MockMvcResultMatchers.status().isOk());
	}*/
	
	@Test
	@WithUserDetails("global-admin")
	public void defineUISpecTest() throws Exception {
		
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.post("/uispec").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request))).andReturn(),null);
		
	}

	/*@Test
	@WithUserDetails("global-admin")
	public void updateUISpec() throws Exception {
		//Mockito.when(uiSpecService.updateUISpec(Mockito.anyString(), Mockito.any(UISpecDto.class)))
		//		.thenReturn(new UISpecResponseDto());
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.put("/uispec").param("id", "test-test-test-test")
				.contentType(MediaType.APPLICATION_JSON).content(
						"{\"id\":\"string\",\"version\":\"string\",\"requesttime\":\"2018-12-17T07:15:06.724Z\",\"request\":"
								+ "{\"title\":\"MOSIP ID Schema\",\"description\":\"test desc\",\"schema\":[{\"id\":\"IDSchemaVersion\",\"inputRequired\":false,"
								+ "\"type\":\"number\",\"minimum\":0,\"maximum\":0,\"description\":\"\",\"labelName\":[{\"value\":\"IDSchemaVersion\",\"language\":\"eng\"}],\"controlType\":\"none\","
								+ "\"fieldType\":\"default\",\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"Pvt\",\"required\":false},"
								+ "{\"id\":\"UIN\",\"inputRequired\":false,\"type\":\"integer\",\"minimum\":0,\"maximum\":0,\"description\":\"\","
								+ "\"labelName\":[{\"value\":\"UIN\",\"language\":\"eng\"}],\"controlType\":\"none\",\"fieldType\":\"default\",\"format\":\"none\",\"validators\":[],"
								+ "\"fieldCategory\":\"Pvt\",\"required\":false},{\"id\":\"fullName\",\"inputRequired\":true,\"type\":\"simpleType\","
								+ "\"minimum\":0,\"maximum\":0,\"description\":\"\",\"label\":[{\"value\":\"Full Name\",\"language\":\"eng\"}],\"controlType\":\"textbox\",\"fieldType\":\"default\","
								+ "\"format\":\"none\",\"validators\":[],\"fieldCategory\":\"Pvt\",\"required\":true}],"
								+ "\"effectiveFrom\":\"2018-12-17T07:15:06.724Z\"}}")).andReturn(),null);
				//.andExpect(MockMvcResultMatchers.status().isOk());

	}*/
	
	@Test
	@WithUserDetails("global-admin")
	public void updateUISpec() throws Exception {

		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.put("/uispec").param("id", "test-test-test-test")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request))).andReturn(),null);


	}

	@Test
	@WithUserDetails("global-admin")
	public void publishUISpec() throws Exception {
	//	Mockito.when(uiSpecService.publishUISpec(Mockito.any(UISpecPublishDto.class))).thenReturn("");
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.put("/uispec/publish").contentType(MediaType.APPLICATION_JSON).content(
				"{\"id\":\"string\",\"version\":\"string\",\"requesttime\":\"2018-12-17T07:15:06.724Z\",\"request\":"
						+ "{\"id\":\"test-test-test-test\",\"effectiveFrom\":\"2048-12-17T07:15:06.724Z\"}}")).andReturn(),null);
				//.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@WithUserDetails("global-admin")
	public void deleteUISpec() throws Exception {
		//Mockito.when(uiSpecService.deleteUISpec(Mockito.anyString())).thenReturn("");
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.delete("/uispec").param("id", "test-test-test-test")
				.contentType(MediaType.APPLICATION_JSON)).andReturn(),null);//.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@WithUserDetails("global-admin")
	public void fetchAllUISpec() throws Exception {
	//	Mockito.when(uiSpecService.getAllUISpecs(1, 10, "id", "desc")).thenReturn(new PageDto<UISpecResponseDto>());
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/all")).andReturn(),null);//.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestUISpec() throws Exception {
		//Mockito.when(uiSpecService.getLatestUISpec(Mockito.anyString())).thenReturn(new ArrayList<>());
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/regclient/latest")).andReturn(),null);
			//	.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	

	@Test
	@WithUserDetails("global-admin")
	public void getLatestUISpec1() throws Exception {
		
	//	Mockito.when(uiSpecService.getLatestUISpec(Mockito.anyString())).thenReturn(new ArrayList<>());
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/regclient/latest")).andReturn(),null);
				//.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema1() throws Exception {
		
	//	Mockito.when(uiSpecRepository.findPublishedUISpec(Mockito.anyInt(),Mockito.anyString())).thenReturn(getLstUISpec());
				MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "1.0"))
				.andReturn(),null);
	}
	
	
	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema2() throws Exception {
		
		//Mockito.when(uiSpecRepository.findPublishedUISpec(Mockito.anyInt(),Mockito.anyString())).thenReturn(getLstUISpec());
				MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "0.1"))
				.andReturn(),null);
	}
	
	
	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema3() throws Exception {
		
		//Mockito.when(uiSpecRepository.findPublishedUISpec(Mockito.anyInt(),Mockito.anyString())).thenReturn(getLstUISpec());
				MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "1.0").param("type", "dom"))
				.andReturn(),null);
	}
	
	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema4() throws Exception {
		
		//Mockito.when(uiSpecRepository.findPublishedUISpec(Mockito.anyInt(),Mockito.anyString())).thenReturn(getLstUISpec());
				MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "0.1").param("type", "dom"))
				.andReturn(),null);
	}
}


