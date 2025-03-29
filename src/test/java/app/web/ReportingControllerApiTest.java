package app.web;

import app.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportingController.class)
public class ReportingControllerApiTest {

    @MockitoBean
    private ReportingService reportingService;

    @Autowired
    private MockMvc mockMvc;
}
