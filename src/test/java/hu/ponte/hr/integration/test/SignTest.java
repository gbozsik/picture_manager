package hu.ponte.hr.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.controller.upload.UploadController;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Signature;
import java.util.Base64;
import java.util.stream.Stream;

import static hu.ponte.hr.services.SignService.SIGNING_ALGORITHM;
import static hu.ponte.hr.utils.SignUtils.getPrivateKey;
import static hu.ponte.hr.utils.SignUtils.getPublicKey;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author zoltan
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignTest {

	@Autowired
	private UploadController uploadController;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() {
		assertNotNull(uploadController);
	}

	private static Stream<Arguments> uploadTestSources() {
		return Stream.of(Arguments.of("cat.jpg", "XYZ+wXKNd3Hpnjxy4vIbBQVD7q7i0t0r9tzpmf1KmyZAEUvpfV8AKQlL7us66rvd6eBzFlSaq5HGVZX2DYTxX1C5fJlh3T3QkVn2zKOfPHDWWItdXkrccCHVR5HFrpGuLGk7j7XKORIIM+DwZKqymHYzehRvDpqCGgZ2L1Q6C6wjuV4drdOTHps63XW6RHNsU18wHydqetJT6ovh0a8Zul9yvAyZeE4HW7cPOkFCgll5EZYZz2iH5Sw1NBNhDNwN2KOxrM4BXNUkz9TMeekjqdOyyWvCqVmr5EgssJe7FAwcYEzznZV96LDkiYQdnBTO8jjN25wlnINvPrgx9dN/Xg=="),
				Arguments.of("enhanced-buzz.jpg","tsLUqkUtzqgeDMuXJMt1iRCgbiVw13FlsBm2LdX2PftvnlWorqxuVcmT0QRKenFMh90kelxXnTuTVOStU8eHRLS3P1qOLH6VYpzCGEJFQ3S2683gCmxq3qc0zr5kZV2VcgKWm+wKeMENyprr8HNZhLPygtmzXeN9u6BpwUO9sKj7ImBvvv/qZ/Tht3hPbm5SrDK4XG7G0LVK9B8zpweXT/lT8pqqpYx4/h7DyE+L5bNHbtkvcu2DojgJ/pNg9OG+vTt/DfK7LFgCjody4SvZhSbLqp98IAaxS9BT6n0Ozjk4rR1l75QP5lbJbpQ9ThAebXQo+Be4QEYV/YXf07WXTQ=="),
				Arguments.of("rnd.jpg","lM6498PalvcrnZkw4RI+dWceIoDXuczi/3nckACYa8k+KGjYlwQCi1bqA8h7wgtlP3HFY37cA81ST9I0X7ik86jyAqhhc7twnMUzwE/+y8RC9Xsz/caktmdA/8h+MlPNTjejomiqGDjTGvLxN9gu4qnYniZ5t270ZbLD2XZbuTvUAgna8Cz4MvdGTmE3MNIA5iavI1p+1cAN+O10hKwxoVcdZ2M3f7/m9LYlqEJgMnaKyI/X3m9mW0En/ac9fqfGWrxAhbhQDUB0GVEl7WBF/5ODvpYKujHmBAA0ProIlqA3FjLTLJ0LGHXyDgrgDfIG/EDHVUQSdLWsM107Cg6hQg=="));
	}

	@ParameterizedTest
	@MethodSource("uploadTestSources")
	public void uploadTest(String fileName, String base64Signature) throws Exception {
		var projectFolder = System.getProperty("user.dir");
		String uploadsDir = format("%s/src/test/resources/images", projectFolder);
		byte[] messageBytes = Files.readAllBytes(Paths.get(String.format("%s/%s", uploadsDir, fileName)));
		MockMultipartFile firstFile = new MockMultipartFile("file", fileName, "text/plain", messageBytes);
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/post")
						.file(firstFile))
				.andExpect(status().is(201)).andReturn();

		var content = mvcResult.getResponse().getContentAsString();
		var imageMeta = new ObjectMapper().readValue(content, ImageMeta.class);

		assertTrue(verifySignature(fileName, base64Signature));
		assertEquals(base64Signature, imageMeta.getDigitalSign());
	}

	public boolean verifySignature(String fileName, String base64Signature) throws Exception {
		var projectFolder = System.getProperty("user.dir");
		String uploadsDir = format("%s/src/test/resources/images", projectFolder);
		byte[] messageBytes = Files.readAllBytes(Paths.get(String.format("%s/%s", uploadsDir, fileName)));
		byte[] signature = Base64.getDecoder().decode(base64Signature);
		Signature publicSignature = Signature.getInstance(SIGNING_ALGORITHM);
		publicSignature.initSign(getPrivateKey());
		publicSignature.initVerify(getPublicKey());
		publicSignature.update(messageBytes);

		return publicSignature.verify(signature);
	}
}
