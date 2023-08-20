package sandipchitale.fileupreceiver;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.file.Files.createTempDirectory;

@SpringBootApplication
public class FileupreceiverApplication {
	@RestController
	public static class ReceiveController {

		private final File uploadFolder;

		public ReceiveController() throws IOException {
			this.uploadFolder = createTempDirectory("upload").toFile();
		}

		@PostMapping("/receive")
		public ResponseEntity<String> receive(HttpServletRequest httpServletRequest) {
			boolean multipartContent = JakartaServletFileUpload.isMultipartContent(httpServletRequest);
			if (multipartContent) {
				JakartaServletFileUpload jakartaServletFileUpload = new JakartaServletFileUpload();
				try {
					FileItemInputIterator itemIterator = jakartaServletFileUpload.getItemIterator(httpServletRequest);
					// While is OK here only because we consume the fileItemInput.getInputStream() in each iteration
					while (itemIterator.hasNext()) {
						FileItemInput fileItemInput = itemIterator.next();
						if (!fileItemInput.isFormField() && StringUtils.hasText(fileItemInput.getName())) {
							System.out.println("Received file: Field: " + fileItemInput.getFieldName() + " Filename: " + fileItemInput.getName() + " content type: " + fileItemInput.getContentType());

							File receivedFile = new File(uploadFolder, fileItemInput.getName());
							try (FileOutputStream fos = new FileOutputStream(receivedFile)) {
								IOUtils.copy(fileItemInput.getInputStream(), fos, 32768);
								System.out.println("Wrote received file to: " + receivedFile.getAbsolutePath());
							}
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return ResponseEntity.ok("File Receiver Application! Received files");
		}
	}


	public static void main(String[] args) {
		SpringApplication.run(FileupreceiverApplication.class, args);
	}

}
