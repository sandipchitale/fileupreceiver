package sandipchitale.fileupreceiver;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class FileupreceiverApplication {
	@RestController
	public static class ReceiveController {

		private final RestTemplate restTemplate;

		public ReceiveController(RestTemplateBuilder restTemplateBuilder) {
			this.restTemplate = restTemplateBuilder
					.setBufferRequestBody(false)
					.build();

		}

		@PostMapping("/receive")
		public String receive(HttpServletRequest httpServletRequest) {
			boolean multipartContent = JakartaServletFileUpload.isMultipartContent(httpServletRequest);
			if (multipartContent) {
				JakartaServletFileUpload jakartaServletFileUpload = new JakartaServletFileUpload();
				try {
					FileItemInputIterator itemIterator = jakartaServletFileUpload.getItemIterator(httpServletRequest);
					if (itemIterator.hasNext()) {
						FileItemInput fileItemInput = itemIterator.next();
						return "Received file: " + fileItemInput.getFieldName() + " content type: " + fileItemInput.getContentType();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return "File Receiver Application! Send file!";
		}
	}


	public static void main(String[] args) {
		SpringApplication.run(FileupreceiverApplication.class, args);
	}

}
