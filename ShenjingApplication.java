package shenjing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class ShenjingApplication {

    public static void main(String[] args) {
        RStart();
        SpringApplication.run(ShenjingApplication.class, args);
    }

    public static void RStart() {
        Properties properties = new Properties();
        String r_exe = null;
        try {
            properties.load((ShenjingApplication.class.getResourceAsStream("/r.properties")));
            r_exe = properties.getProperty("r_exe");
            if (new org.rosuda.REngine.Rserve.RConnection().isConnected()) {
                //System.out.println("R已经启动了！！！");
            }
        } catch (Exception e) {
            try {
                Runtime.getRuntime()
                        .exec("\"" + r_exe + "\" -e \"library(Rserve);Rserve()\"");
                //System.out.println("R成功开启");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
