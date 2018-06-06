import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by suman.das on 10/27/16.
 */
@SpringBootApplication
//@EnableOAuth2Sso
@Configuration
@ComponentScan(basePackages = { "com.fractal.oauth" })
@EnableAutoConfiguration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class SocialApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SocialApplication.class, args);
    }
    /*
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login**", "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated().and().logout().logoutSuccessUrl("/").permitAll();
    }
    */
}
