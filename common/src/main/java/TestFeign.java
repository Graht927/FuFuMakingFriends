import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author GRAHT
 */

@FeignClient
public class TestFeign {
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
