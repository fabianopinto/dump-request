package fabianopinto.test.testrequest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DumpController {

    @RequestMapping("/**")
    public @ResponseBody String test(HttpServletRequest request) {
        return DumpRequest.dump(request);
    }

}
